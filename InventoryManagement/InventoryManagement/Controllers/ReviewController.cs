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
using System.Collections.Generic;
using AutoMapper;
using System.Net.Http.Formatting;
using System;
using System.Linq;

namespace InventoryManagement.Controllers
{
    [RoutePrefix("api/review")]
    public class ReviewController : ApiControllerBase
    {
        private IReviewService service;
        private IRatingService servRat;
        private IUserDataService<UserDataView> servUs;

        public ReviewController(IReviewService serv, IRatingService servRating, IUserDataService<UserDataView> servUser, IEntityBaseRepository<Error> errorsRepository, IUnitOfWork unitOfWork) : base(errorsRepository, unitOfWork)
        {
            service = serv;
            servRat = servRating;
            servUs = servUser;
        }

        [HttpPost]
        [Route("AddReviewAndRating")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage AddReview(HttpRequestMessage request, ReviewView review)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                RatingView rating = new RatingView();
                rating.bookID = review.bookID;
                rating.userID = review.userID;
                rating.ratingScore = review.ratingScore;

                bool getReview = service.getOneReviewCheck(review.bookID,review.userID);
                if (getReview)
                {
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                    unitOfWork.Commit();
                    return response;
                }

                bool getRating = servRat.checkIfRatingExists(review.bookID, review.userID);
                if (getRating)
                {
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                    unitOfWork.Commit();
                    return response;
                }

                bool wasAddedReview = service.addReview(review);
                bool wasAddedRating = servRat.addRating(rating);

                if (wasAddedReview && wasAddedRating)
                    response = request.CreateResponse(HttpStatusCode.OK, true);
                else
                    response = request.CreateResponse(HttpStatusCode.InternalServerError);
                unitOfWork.Commit();

                return response;
            });
        }


        [HttpPost]
        [Route("DeleteReview")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage DeleteReview(HttpRequestMessage request, ReviewViewID reviewID)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                bool wasDeleted = service.deleteReview(reviewID.ID);
                bool wasDeletedRating = servRat.deleteRating(reviewID.userID);
                if (wasDeleted && wasDeletedRating)
                    response = request.CreateResponse(HttpStatusCode.OK, true);
                else
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                unitOfWork.Commit();

                return response;
            });
        }

        [HttpPost]
        [Route("GetOneReview")]
        [ResponseType(typeof(ReviewView))]
        [JwtAuthentication]
        public HttpResponseMessage GetOneReview(HttpRequestMessage request, ReviewViewID reviewID)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;

                Review review = service.getOneReview(reviewID.ID, reviewID.userID);
                if (review != null)
                {
                    ReviewView obj = new ReviewView();
                    obj.reviewText = review.reviewText;
                    obj.userID = review.userID;
                    obj.bookID = review.bookID;
                    obj.ID = review.ID;
                    obj.ratingScore = 0;
                    obj.addedTime = review.addedTime;

                    UserDataNoPass usr = servUs.getUserAfterID(obj.userID);
                    if (usr != null)
                    {
                        obj.user = usr;
                    }
                    else
                    {
                        obj.user = new UserDataNoPass();
                    }

                    RatingView rati = servRat.getRating(obj.bookID, obj.userID);
                    if (rati != null)
                    {
                        obj.ratingScore = rati.ratingScore;
                        obj.rating = rati;
                    }
                    else
                    {
                        obj.ratingScore = 0;
                    }
                    response = request.CreateResponse(HttpStatusCode.OK, obj, JsonMediaTypeFormatter.DefaultMediaType);
                }

                else
                {
                    ReviewView rev = new ReviewView();
                    response = request.CreateResponse(HttpStatusCode.OK, rev, JsonMediaTypeFormatter.DefaultMediaType);
                }
                unitOfWork.Commit();

                return response;
            });
        }

        [Route("allPaginedAfterID")]
        [ResponseType(typeof(List<ReviewView>))]
        [JwtAuthentication]
        public HttpResponseMessage GetReviewPagined(HttpRequestMessage request, [FromUri]PagingParameterModel pagingparametermodel, [FromUri]string filterAfter, [FromUri]string filterField, [FromUri]string sortMethod, [FromUri]string IDURL, [FromUri]string isbn)
        {
            return CreateHttpResponse(request, () =>
            {
                int CurrentPage = pagingparametermodel.pageNumber;
                int PageSize = pagingparametermodel.pageSize;

                BookViewWithISBN bookID = new BookViewWithISBN();

                if (IDURL.Equals("null"))
                {
                    bookID.ID = 0;
                }
                else
                {
                    bookID.ID = Int32.Parse(IDURL);
                }

                if (isbn.Equals("null"))
                {
                    bookID.isbn = "0";
                }
                else
                {
                    bookID.isbn = isbn;
                }

                IEnumerable<Review> reviews = service.getReviewFilteredPaginated(bookID,CurrentPage, PageSize, filterAfter, filterField, sortMethod);

                List<ReviewView> reviewList = new List<ReviewView>();

                foreach (Review b in reviews)
                {
                    ReviewView obj = new ReviewView();
                    obj.reviewText = b.reviewText;
                    obj.userID = b.userID;
                    obj.bookID = b.bookID;
                    obj.ID = b.ID;
                    obj.ratingScore = 0;
                    obj.addedTime = b.addedTime;

                    UserDataNoPass usr = servUs.getUserAfterID(obj.userID);
                    if (usr != null)
                    {
                        obj.user = usr;
                    }
                    else
                    {
                        obj.user = new UserDataNoPass(); 
                    }

                    RatingView rati = servRat.getRating(obj.bookID, obj.userID);
                    if (rati !=null)
                    {
                        obj.ratingScore = rati.ratingScore;
                    }
                    else
                    {
                        obj.ratingScore = 0;
                    }
                    
                    reviewList.Add(obj);
                }

                HttpResponseMessage response = null;

                response = request.CreateResponse(HttpStatusCode.OK, reviewList, JsonMediaTypeFormatter.DefaultMediaType);

                return response;
            });
        }

        [Route("allPaginedAfterUserID")]
        [ResponseType(typeof(List<ReviewView>))]
        [JwtAuthentication]
        public HttpResponseMessage GetReviewUsers(HttpRequestMessage request, [FromUri]PagingParameterModel pagingparametermodel, [FromUri]string filterAfter, [FromUri]string filterField, [FromUri]string sortMethod, [FromUri]string IDURL)
        {
            return CreateHttpResponse(request, () =>
            {
                int CurrentPage = pagingparametermodel.pageNumber;
                int PageSize = pagingparametermodel.pageSize;

                IEnumerable<Review> reviews = service.getReviewAfterUser(IDURL, CurrentPage, PageSize, filterAfter, filterField, sortMethod);

                List<ReviewOfUserView> reviewList = new List<ReviewOfUserView>();

                foreach (Review b in reviews)
                {
                    ReviewOfUserView obj = new ReviewOfUserView();
                    obj.reviewText = b.reviewText;
                    obj.userID = b.userID;
                    obj.bookID = b.bookID;
                    obj.ID = b.ID;
                    obj.ratingScore = 0;
                    obj.addedTime = b.addedTime;

                    UserDataNoPass usr = servUs.getUserAfterID(obj.userID);
                    if (usr != null)
                    {
                        obj.user = usr;
                    }
                    else
                    {
                        obj.user = new UserDataNoPass();
                    }

                    RatingView rati = servRat.getRating(obj.bookID, obj.userID);
                    if (rati != null)
                    {
                        obj.ratingScore = rati.ratingScore;
                    }
                    else
                    {
                        obj.ratingScore = 0;
                    }

                    Book bookOfRev = b.book;
                    BookViewList bookObj = new BookViewList();
                    bookObj.title = bookOfRev.title;
                    bookObj.bookPic = bookOfRev.bookPic;
                    bookObj.releaseDate = bookOfRev.releaseDate;
                    bookObj.ID = bookOfRev.ID;

                    List<GenreViewName> genreView = Mapper.Map<List<Genre>, List<GenreViewName>>(bookOfRev.genre.ToList());
                    List<AuthorViewName> authorView = Mapper.Map<List<Author>, List<AuthorViewName>>(bookOfRev.author.ToList());

                    double average = 0;

                    if (bookOfRev.rating.Select(x => x.ratingScore).Any())
                    {
                        average = bookOfRev.rating.Select(x => x.ratingScore).Average();
                    }

                    bookObj.author = authorView;
                    bookObj.genre = genreView;
                    bookObj.rating = average;

                    obj.book = bookObj;
                    reviewList.Add(obj);
                }

                HttpResponseMessage response = null;

                response = request.CreateResponse(HttpStatusCode.OK, reviewList, JsonMediaTypeFormatter.DefaultMediaType);

                return response;
            });
        }
    }
}