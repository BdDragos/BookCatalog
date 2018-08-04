using AutoMapper;
using InventoryManagement.Models;
using InventoryManagement.Repositories;
using InventoryManagement.Views;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Linq.Expressions;
using System.Net;
using System.Net.Mail;
using System.Web;

namespace InventoryManagement.Services
{
    public class UserDataService : IUserDataService<UserDataView> 
    {
        private IEntityBaseRepository<UserData> userDataRepo;
        public UserDataService(IEntityBaseRepository<UserData> repo)
        {
            this.userDataRepo = repo;
        }

        public UserDataService()
        {

        }

        public bool addUser(UserData data)
        {
            userDataRepo.Add(data);
            return true;
        }

        public bool verifyUser(UserDataView entry)
        {
            UserDataView data = Mapper.Map<UserData, UserDataView>(userDataRepo.FindBy(user => user.email == entry.email).FirstOrDefault());
            if (data != null)
                if (String.Compare(data.userpass, entry.userpass) == 0)
                    return true;
                else
                    return false;
            return false;
        }

        public string verifyUserChange(UserDataView entry, UserDataChange data)
        {
            UserData dataFound = userDataRepo.FindBy(user => user.ID == entry.ID).FirstOrDefault();
            if (dataFound != null)
                if (String.Compare(data.userpass, entry.userpass) == 0)
                {
                    if (data.email != null)
                        if (!data.email.Equals(""))
                        {
                            dataFound.email = data.email;
                        }
                    if (data.userPic != null)
                        if (!data.userPic.Equals(""))
                        {
                            dataFound.userPic = Convert.FromBase64String(data.userPic);
                        }
                    if (data.username != null)
                        if (!data.username.Equals(""))
                        {
                            dataFound.username = data.username;
                        }
                    if (data.newPass != null)
                        if (!data.newPass.Equals(""))
                        {
                            dataFound.userpass = data.newPass;
                        }
                    if (data.overview != null)
                        if (!data.overview.Equals(""))
                        {
                            dataFound.userOverview = data.overview;
                        }
                    userDataRepo.Edit(dataFound);
                    return "valid";
                }
                else
                    return "autherror";
            return "invalid";
        }

        public bool changePassword(UserDataView entry, string newPassword)
        {
            UserData convertedUser = Mapper.Map<UserDataView, UserData>(entry);
            if (convertedUser.userpass.CompareTo(newPassword) == 0)
            {
                convertedUser.userpass = newPassword;
                userDataRepo.Edit(convertedUser);
                return true;
            }
            else
                return false;
            
        }

        public bool searchForUser(string cuv,string usern)
        {

            if (userDataRepo.FindBy(obj => obj.email == cuv).Any() == false && userDataRepo.FindBy(obj => obj.username == usern).Any() == false)
            {
                return true;
            }
            else return false;

        }

        public UserDataNoPass getUser(string email)
        {
            UserData foundUser = userDataRepo.FindBy(obj => obj.email == email).FirstOrDefault();
            if (foundUser == null)
            {
                return null;
            }
            else
            {
                UserDataNoPass convertedUser = Mapper.Map<UserData, UserDataNoPass>(foundUser);
                return convertedUser;
            }
        }

        public UserDataNoPass getUserAfterID(int ID)
        {
            UserData foundUser = userDataRepo.FindBy(obj => obj.ID == ID).FirstOrDefault();
            if (foundUser == null)
            {
                return null;
            }
            else
            {
                UserDataNoPass convertedUser = Mapper.Map<UserData, UserDataNoPass>(foundUser);
                return convertedUser;
            }
        }

        public UserDataViewProfileDetail getUserAfterIDetail(int ID, int loggedInID)
        {
            UserData foundUser = userDataRepo.FindBy(obj => obj.ID == ID).FirstOrDefault();
            UserDataViewID userOnlyID = new UserDataViewID();
            userOnlyID.ID = 0;

            if (foundUser == null)
            {
                return null;
            }
            else
            {
                foreach (UserData user in foundUser.user)
                {
                    if (user.ID == loggedInID)
                    {
                        userOnlyID.ID = loggedInID;
                    }
                        
                }
                UserDataViewProfileDetail convertedUser = Mapper.Map<UserData, UserDataViewProfileDetail>(foundUser);
                convertedUser.userLogged = userOnlyID;
                return convertedUser;
            }
        }


        public IEnumerable<UserData> getFriendsPaginated(int currentPage, int pageSize, int userID, string username)
        {
            UserData usr = null;
            if (username != null)
            {
                if (!username.Equals(""))
                {
                    usr = userDataRepo.FindBy(obj => obj.ID == userID || obj.username == username).FirstOrDefault();
                }
            }
            else
            {
                usr = userDataRepo.FindBy(obj => obj.ID == userID).FirstOrDefault();
            }

            if (usr != null)
            {
                IEnumerable<UserData> source = usr.user;
                int count = source.Count();

                int CurrentPage = currentPage;
                int PageSize = pageSize;
                var items = source.Skip(CurrentPage * PageSize).Take(PageSize).ToList();
                return items;
            }

            return null;

        }

        public bool deleteUserData(int ID, string userpass)
        {
            var data = userDataRepo.FindBy(u => u.ID == ID).FirstOrDefault();
            if (userpass.Equals(data.userpass))
                if (data != null)
                {
                    userDataRepo.Delete(data);
                    return true;
                }
            return false;
            
        }

        public bool deleteFriend(RelationshipUsers data)
        {
            var userMain = userDataRepo.FindBy(u => u.ID == data.mainUserID).FirstOrDefault();
            var userToAdd = userDataRepo.FindBy(u => u.ID == data.secondUserID).FirstOrDefault();
            if (userMain != null && userToAdd != null)
            {
                userMain.user.Remove(userToAdd);
                userToAdd.user.Remove(userMain);
                userDataRepo.Edit(userToAdd);
                userDataRepo.Edit(userMain);
                return true;
            }
            else
            {
                return false;
            }

        }

        public bool addFriend(RelationshipUsers data)
        {
            UserData userMain = userDataRepo.FindBy(u => u.ID == data.mainUserID).FirstOrDefault();
            UserData userToAdd = userDataRepo.FindBy(u => u.ID == data.secondUserID).FirstOrDefault();
            if (userMain != null && userToAdd != null)
            {
                userMain.user.Add(userToAdd);
                userToAdd.user.Add(userMain);
                userDataRepo.Edit(userToAdd);
                userDataRepo.Edit(userMain);
                return true;
            }
            else
            {
                return false;
            }
        }

        public IEnumerable<UserData> getSearch(int currentPage, int pageSize, string username)
        {
            IEnumerable<UserData> usr = null;
            if (username != null)
            {
                if (!username.Equals(""))
                {
                    usr = userDataRepo.FindBy(obj => obj.username.Contains(username));
                }
            }

            if (usr != null)
            {
                int count = usr.Count();

                int CurrentPage = currentPage;
                int PageSize = pageSize;
                var items = usr.Skip(CurrentPage * PageSize).Take(PageSize).ToList();
                return items;
            }

            return null;
        }

        public bool sendEmail(String userEmail)
        {

            UserData foundUser = userDataRepo.FindBy(obj => obj.email == userEmail).FirstOrDefault();
            if (foundUser == null)
            {
                return false;
            }
            else
            {
                string mailBodyhtml = "<p>Your account with the email " +  foundUser.email  + " has the following password: " + foundUser.userpass + " </p>";
                var msg = new MailMessage("dragosbad2009@gmail.com", userEmail);

                msg.Subject = "BookDeposit Account Recovery";
                msg.Body = "Hello. Your account with the email " + foundUser.email + " has the following password: " + foundUser.userpass + ". After logging in please change the password";

                var smtpClient = new SmtpClient("smtp.gmail.com", 587);
                smtpClient.UseDefaultCredentials = true;
                smtpClient.Credentials = new NetworkCredential("dragosbad2009@gmail.com", "nkjwbssckltpujkh");
                smtpClient.EnableSsl = true;
                smtpClient.Send(msg);

                return true;
            }

        }
    }
}