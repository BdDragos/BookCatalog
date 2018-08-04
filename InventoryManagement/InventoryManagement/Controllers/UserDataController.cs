using InventoryManagement.Infrastructure;
using InventoryManagement.Models;
using InventoryManagement.Repositories;
using InventoryManagement.Services;
using InventoryManagement.Views;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Description;
using InventoryManagement.jwt;
using System.Net.Http.Formatting;
using System.Text;
using System.Collections.Generic;
using AutoMapper;
using System.Linq;
using System;

namespace InventoryManagement.Controllers
{
    [RoutePrefix("api/userdata")]
    public class UserDataController : ApiControllerBase
    {
        private IUserDataService<UserDataView> service;

        public UserDataController(IUserDataService<UserDataView> serv, IEntityBaseRepository<Error> errorsRepository, IUnitOfWork unitOfWork) : base(errorsRepository, unitOfWork)
        {
            service = serv;
        }

        [HttpPost]
        [Route("VerifyUser")]
        [ResponseType(typeof(string))]
        [AllowAnonymous]
        public HttpResponseMessage VerifyUser(HttpRequestMessage request, UserDataView data)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;
                if (service.verifyUser(data))
                {
                    response = request.CreateResponse(HttpStatusCode.OK, JwtManager.GenerateToken(data.username));
                }
                else
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                return response;
            });
        }

        [HttpPost]
        [Route("AddFriend")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage AddFriend(HttpRequestMessage request, RelationshipUsers data)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                bool wasAdded = service.addFriend(data);

                if (wasAdded)
                    response = request.CreateResponse(HttpStatusCode.OK, true);
                else
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                unitOfWork.Commit();
                return response;
            });
        }

        [HttpPost]
        [Route("DeleteFriend")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage DeleteFriend(HttpRequestMessage request, RelationshipUsers data)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                bool wasAdded = service.deleteFriend(data);
                
                if (wasAdded)
                    response = request.CreateResponse(HttpStatusCode.OK, true);
                else
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                unitOfWork.Commit();
                return response;
            });
        }

        [HttpPost]
        [Route("GetAccount")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage GetAccount(HttpRequestMessage request, UserDataNoPass userData)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;
                UserDataNoPass userdata = service.getUser(userData.email);
                if (userdata != null)
                {
                    response = request.CreateResponse(HttpStatusCode.OK, userdata, JsonMediaTypeFormatter.DefaultMediaType);
                }
                else
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                return response;
            });
        }

        [HttpPost]
        [Route("GetUserAfterID")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage GetUserAfterID(HttpRequestMessage request, UserDataNoPass userData)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;
                UserDataViewProfileDetail userdata = service.getUserAfterIDetail(userData.ID, Int32.Parse(userData.username));
                if (userdata != null)
                {
                    response = request.CreateResponse(HttpStatusCode.OK, userdata, JsonMediaTypeFormatter.DefaultMediaType);
                }
                else
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                return response;
            });
        }

        [HttpPost]
        [Route("AddUser")]
        [ResponseType(typeof(string))]
        [AllowAnonymous]
        public HttpResponseMessage AddUser(HttpRequestMessage request, UserDataViewStringPic data)
        {
            return CreateHttpResponse(request, () =>
            {

                HttpResponseMessage response = null;

                if (!service.searchForUser(data.email, data.username))
                {
                    response = request.CreateResponse(HttpStatusCode.OK, false);

                }
                else
                {
                    UserData userD = new UserData();
                    userD.userPic = Convert.FromBase64String(data.userPic);
                    userD.username = data.username;
                    userD.userpass = data.userpass;
                    userD.email = data.email;
                    userD.joinedDate = DateTime.Today;
                    if (service.addUser(userD))
                        response = request.CreateResponse(HttpStatusCode.OK, true);
                    else
                        response = request.CreateResponse(HttpStatusCode.InternalServerError);
                    unitOfWork.Commit();
                }
                return response;
            });
        }

        [HttpPost]
        [Route("ChangeUserData")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage ChangeUserData(HttpRequestMessage request, UserDataChange data)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                UserDataView user = Mapper.Map<UserDataChange, UserDataView>(data);
                string verifyUserData = service.verifyUserChange(user, data);
                if (verifyUserData.Equals("valid"))
                {
                    response = request.CreateResponse(HttpStatusCode.OK, true);
                }
                else if (verifyUserData.Equals("autherror"))
                {
                    response = request.CreateResponse(HttpStatusCode.OK, "invalid");
                }
                else
                {
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                }
                unitOfWork.Commit();
                return response;

            });
        }


        [HttpPost]
        [Route("RecoverPassword")]
        [ResponseType(typeof(string))]
        [AllowAnonymous]
        public HttpResponseMessage RecoverPassword(HttpRequestMessage request, UserDataViewStringPic data)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                bool emailWasSent = service.sendEmail(data.email);
                if (emailWasSent)
                {
                    response = request.CreateResponse(HttpStatusCode.OK, true);
                }
                else
                {
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                }
                unitOfWork.Commit();
                return response;

            });
        }


        [HttpPost]
        [Route("DeleteUserData")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage AddToShelf(HttpRequestMessage request, UserDataView data)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                bool wasDeleted = service.deleteUserData(data.ID, data.userpass);
                if (wasDeleted)
                    response = request.CreateResponse(HttpStatusCode.OK, true);
                else
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                unitOfWork.Commit();
                return response;

            });
        }

        [HttpGet]
        [Route("allPagined")]
        [ResponseType(typeof(List<UserFriendsView>))]
        [JwtAuthentication]
        public HttpResponseMessage GetPagined(HttpRequestMessage request, [FromUri]PagingParameterModel pagingparametermodel,[FromUri] string userID, [FromUri] string username)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                int CurrentPage = pagingparametermodel.pageNumber;
                int PageSize = pagingparametermodel.pageSize;

                IEnumerable<UserData> users = service.getFriendsPaginated(CurrentPage, PageSize, Int32.Parse(userID), username);

                if (users == null)
                {
                    response = request.CreateResponse(HttpStatusCode.OK, users, JsonMediaTypeFormatter.DefaultMediaType);
                }

                List<UserFriendsView> userList = new List<UserFriendsView>();

                foreach (UserData b in users)
                {
                    UserFriendsView obj = new UserFriendsView();
                    obj.username = b.username;
                    obj.userPic = b.userPic;
                    obj.ID = b.ID;

                    int num = b.user.Count;
                    obj.noOfFriends = num.ToString();

                    List<UserFriendsView> userView = Mapper.Map<List<UserData>, List<UserFriendsView>>(b.user.ToList());
                    obj.userList = userView;
                    userList.Add(obj);
                }


              
                response = request.CreateResponse(HttpStatusCode.OK, userList, JsonMediaTypeFormatter.DefaultMediaType);


                return response;
            });
        }

        [HttpGet]
        [Route("getSearch")]
        [ResponseType(typeof(List<UserFriendsView>))]
        [JwtAuthentication]
        public HttpResponseMessage GetSearch(HttpRequestMessage request, [FromUri]PagingParameterModel pagingparametermodel, [FromUri] string userID, [FromUri] string username)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                int CurrentPage = pagingparametermodel.pageNumber;
                int PageSize = pagingparametermodel.pageSize;

                IEnumerable<UserData> users = service.getSearch(CurrentPage, PageSize, username);

                if (users == null)
                {
                    response = request.CreateResponse(HttpStatusCode.OK, users, JsonMediaTypeFormatter.DefaultMediaType);
                }

                List<UserFriendsView> userList = new List<UserFriendsView>();

                foreach (UserData b in users)
                {
                    UserFriendsView obj = new UserFriendsView();
                    obj.username = b.username;
                    obj.userPic = b.userPic;
                    obj.ID = b.ID;

                    int num = b.user.Count;
                    obj.noOfFriends = num.ToString();

                    List<UserFriendsView> userView = Mapper.Map<List<UserData>, List<UserFriendsView>>(b.user.ToList());
                    obj.userList = userView;
                    userList.Add(obj);
                }



                response = request.CreateResponse(HttpStatusCode.OK, userList, JsonMediaTypeFormatter.DefaultMediaType);


                return response;
            });
        }

    }
}