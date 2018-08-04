using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Description;
using InventoryManagement.Models;
using System.Web.Http.Cors;
using InventoryManagement.Repositories;
using InventoryManagement.Infrastructure;
using InventoryManagement.Views;
using AutoMapper;
using InventoryManagement.Services;
using System.Net.Http.Formatting;
using System.Web;
using Newtonsoft.Json;
using InventoryManagement.jwt;

namespace InventoryManagement.Controllers
{
    [RoutePrefix("api/book")]
    public class BookController : ApiControllerBase
    {
        private IBookService<Book> service;
        private IUserBookService serviceUB;

        public BookController(IBookService<Book> serv, IUserBookService servUB,IEntityBaseRepository<Error> errorsRepository, IUnitOfWork unitOfWork) : base(errorsRepository, unitOfWork)
        {
            service = serv;
            serviceUB = servUB;
        }

        [HttpGet]
        [Route("all")]
        [ResponseType(typeof(List<Book>))]
        [JwtAuthentication]
        public HttpResponseMessage Get(HttpRequestMessage request)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                response = request.CreateResponse(HttpStatusCode.OK, service.getBook(), JsonMediaTypeFormatter.DefaultMediaType);

                return response;
            });
        }

        [HttpGet]
        [Route("allPagined")]
        [ResponseType(typeof(List<Book>))]
        [JwtAuthentication]
        public HttpResponseMessage GetPagined(HttpRequestMessage request, [FromUri]PagingParameterModel pagingparametermodel, [FromUri]string filterAfter,[FromUri]string filterField ,[FromUri]string sortMethod)
        {
            return CreateHttpResponse(request, () =>
            {
                int CurrentPage = pagingparametermodel.pageNumber;
                int PageSize = pagingparametermodel.pageSize;

                IEnumerable<Book> books = service.getBookFilteredPaginated(CurrentPage, PageSize, filterAfter,filterField, sortMethod);

                List<BookViewList> bookList = new List<BookViewList>();

                foreach(Book b in books)
                {
                    BookViewList obj = new BookViewList();
                    obj.title = b.title;
                    obj.bookPic = b.bookPic;
                    obj.releaseDate = b.releaseDate;
                    obj.ID = b.ID;

                    List<GenreViewName> genreView = Mapper.Map<List<Genre>, List<GenreViewName>>(b.genre.ToList());
                    List<AuthorViewName> authorView = Mapper.Map<List<Author>, List<AuthorViewName>>(b.author.ToList());

                    double average = 0;

                    if (b.rating.Select(x => x.ratingScore).Any())
                    {
                        average = b.rating.Select(x => x.ratingScore).Average();
                    }
                    
                    obj.author = authorView;
                    obj.genre = genreView;
                    obj.rating = average;

                    bookList.Add(obj);
                }


                HttpResponseMessage response = null;

                response = request.CreateResponse(HttpStatusCode.OK, bookList, JsonMediaTypeFormatter.DefaultMediaType);

                return response;
            });
        }

        [HttpPost]
        [Route("AddBookToShelf")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage AddToShelf(HttpRequestMessage request, BookXUser data)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;


                bool wasAdded = serviceUB.addUserBook(data);
                if (wasAdded)
                    response = request.CreateResponse(HttpStatusCode.OK, true);
                else
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                unitOfWork.Commit();
                return response;

            });
        }

        [HttpPost]
        [Route("DeleteBookFromShelf")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage DeleteFromShelf(HttpRequestMessage request, BookXUser data)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;


                bool wasDeleted = serviceUB.deleteUserBook(data);
                if (wasDeleted)
                    response = request.CreateResponse(HttpStatusCode.OK, true);
                else
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                unitOfWork.Commit();
                return response;

            });
        }


        [HttpPost]
        [Route("getSingleBook")]
        [ResponseType(typeof(BookViewDetail))]
        [JwtAuthentication]
        public HttpResponseMessage GetASingleBook(HttpRequestMessage request, BookViewWithISBN bookID)
        {
            return CreateHttpResponse(request, () =>
            {
                Book b = service.getSingleBook(bookID);
                if (b != null)
                {
                    BookViewDetail obj = new BookViewDetail();
                    obj.title = b.title;
                    obj.bookPic = b.bookPic;
                    obj.releaseDate = b.releaseDate;
                    obj.series = b.series;
                    obj.initialReleaseDate = b.initialReleaseDate;
                    obj.ID = b.ID;
                    obj.isbn = b.isbn;
                    obj.noPage = b.noPage;
                    obj.edition = b.edition;
                    obj.bLanguage = b.bLanguage;
                    obj.publisherSite = b.publisherSite;
                    obj.bookFormat = b.bookFormat;
                    obj.publisher = b.publisher;
                    obj.overview = b.overview;

                    UserDataViewID userViewID = new UserDataViewID();
                    List<GenreViewName> genreView = Mapper.Map<List<Genre>, List<GenreViewName>>(b.genre.ToList());
                    List<AuthorViewName> authorView = Mapper.Map<List<Author>, List<AuthorViewName>>(b.author.ToList());
                    double average = 0;

                    int searchID = Int32.Parse(bookID.loggedUserID);

                    BookXUser sol = b.bookXuser.FirstOrDefault(a => a.user.ID == searchID);

                    if (sol != null)
                    {
                        UserData rez = sol.user;
                        if (rez != null)
                        {
                            userViewID = Mapper.Map<UserData, UserDataViewID>(rez);
                        }
                    }

                    if (b.rating.Select(x => x.ratingScore).Any())
                    {
                        average = b.rating.Select(x => x.ratingScore).Average();
                    }

                    obj.user = userViewID;
                    obj.author = authorView;
                    obj.genre = genreView;
                    obj.rating = average;


                    HttpResponseMessage response = null;

                    response = request.CreateResponse(HttpStatusCode.OK, obj, JsonMediaTypeFormatter.DefaultMediaType);

                    return response;
                }
                else
                {
                    HttpResponseMessage response = null;

                    BookViewDetail obj = new BookViewDetail();

                    response = request.CreateResponse(HttpStatusCode.OK, obj, JsonMediaTypeFormatter.DefaultMediaType);

                    return response;
                }

            });
        }

        [HttpGet]
        [Route("allShelfPaginated")]
        [ResponseType(typeof(List<Book>))]
        [JwtAuthentication]
        public HttpResponseMessage GetShelfPaginated(HttpRequestMessage request, [FromUri]PagingParameterModel pagingparametermodel, [FromUri]string userID, [FromUri]string bookShelf)
        {
            return CreateHttpResponse(request, () =>
            {
                int CurrentPage = pagingparametermodel.pageNumber;
                int PageSize = pagingparametermodel.pageSize;

                IEnumerable<Book> books = serviceUB.getUserBooks(CurrentPage, PageSize, userID, bookShelf);

                List<BookViewList> bookList = new List<BookViewList>();

                foreach (Book b in books)
                {
                    BookViewList obj = new BookViewList();
                    obj.title = b.title;
                    obj.bookPic = b.bookPic;
                    obj.releaseDate = b.releaseDate;
                    obj.ID = b.ID;

                    List<GenreViewName> genreView = Mapper.Map<List<Genre>, List<GenreViewName>>(b.genre.ToList());
                    List<AuthorViewName> authorView = Mapper.Map<List<Author>, List<AuthorViewName>>(b.author.ToList());

                    double average = 0;

                    if (b.rating.Select(x => x.ratingScore).Any())
                    {
                        average = b.rating.Select(x => x.ratingScore).Average();
                    }

                    obj.author = authorView;
                    obj.genre = genreView;
                    obj.rating = average;

                    bookList.Add(obj);
                }


                HttpResponseMessage response = null;

                response = request.CreateResponse(HttpStatusCode.OK, bookList, JsonMediaTypeFormatter.DefaultMediaType);

                return response;
            });
        }

    }
}