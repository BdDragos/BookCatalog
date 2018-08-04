using InventoryManagement.Models;
using InventoryManagement.Views;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace InventoryManagement.Services
{
    public interface IUserDataService<T> where T : class, new()
    {
        bool addUser(UserData data);
        bool verifyUser(T entry);
        bool searchForUser(string cuv, string usern);
        bool changePassword(UserDataView entry, string newPassword);
        UserDataNoPass getUser(string email);
        UserDataNoPass getUserAfterID(int ID);
        IEnumerable<UserData> getFriendsPaginated(int currentPage, int pageSize, int userID, string username);
        bool deleteUserData(int ID, string userpass);
        string verifyUserChange(UserDataView entry, UserDataChange data);
        bool deleteFriend(RelationshipUsers data);
        bool addFriend(RelationshipUsers data);
        UserDataViewProfileDetail getUserAfterIDetail(int ID, int v);
        IEnumerable<UserData> getSearch(int currentPage, int pageSize, string username);
        bool sendEmail(String userEmail);
    }
}

