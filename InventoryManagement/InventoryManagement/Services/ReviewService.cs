using AutoMapper;
using InventoryManagement.Models;
using InventoryManagement.Repositories;
using InventoryManagement.Views;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Linq.Expressions;
using System.Web;

namespace InventoryManagement.Services
{
    public class ReviewService : IReviewService
    {
        private IEntityBaseRepository<Review> reviewRepo;

        public ReviewService(IEntityBaseRepository<Review> repo)
        {
            reviewRepo = repo;
        }

        public bool addReview(ReviewView review)
        {
            Review reviewModel = Mapper.Map<ReviewView, Review>(review);
            reviewModel.reviewScore = review.ratingScore;
            reviewModel.addedTime = DateTime.Today;
            reviewRepo.Add(reviewModel);
            return true;
        }

        public bool deleteReview(int iD)
        {
            Review rev = reviewRepo.FindBy(obj => obj.ID == iD).FirstOrDefault();
            reviewRepo.Delete(rev);
            return true;
        }

        public Review getOneReview(int bookID, int userID)
        {
            return reviewRepo.FindBy(obj => obj.bookID == userID && obj.userID == bookID).FirstOrDefault();
        }

        public bool getOneReviewCheck(int bookID, int userID)
        {
            return reviewRepo.FindBy(obj => obj.bookID == bookID && obj.userID == userID).Any();
        }

        public IEnumerable<Review> getReviewAfterUser(string iDURL, int currentPage, int pageSize, string filterAfter, string filterField, string sortMethod)
        {
            IEnumerable<Review> reviews = null;

            int userID = Int32.Parse(iDURL);

            bool ok = filterAfter.Equals("null");
            if (!ok)
            {
                var parameter = Expression.Parameter(typeof(Review), "b");
                var predicate = Expression.Lambda<Func<Review, bool>>(Expression.Equal(Expression.PropertyOrField(parameter, filterAfter), Expression.Constant(filterField)), parameter);

                if (sortMethod.CompareTo("ASCENDING") == 0)
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.user.ID == userID , null, currentPage, pageSize, new SortExpression<Review>(p => p.ID, ListSortDirection.Ascending));
                }
                else if (sortMethod.CompareTo("DESCENDING") == 0)
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.user.ID == userID , null, currentPage, pageSize, new SortExpression<Review>(p => p.ID, ListSortDirection.Descending));
                }
                else
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.user.ID == userID , null, currentPage, pageSize, null);
                }
            }
            else
            {
                if (sortMethod.CompareTo("ASCENDING") == 0)
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.user.ID == userID, null, currentPage, pageSize, new SortExpression<Review>(p => p.ID, ListSortDirection.Ascending));
                }
                else if (sortMethod.CompareTo("DESCENDING") == 0)
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.user.ID == userID , null, currentPage, pageSize, new SortExpression<Review>(p => p.ID, ListSortDirection.Descending));
                }
                else
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.user.ID == userID , null, currentPage, pageSize, null);
                }
            }

            return reviews;
        }

        public IEnumerable<Review> getReviewFilteredPaginated(BookViewWithISBN book,int currentPage, int pageSize, string filterAfter, string filterField, string sortMethod)
        {
            IEnumerable<Review> reviews = null;

            bool ok = filterAfter.Equals("null");
            if (!ok)
            {
                var parameter = Expression.Parameter(typeof(Review), "b");
                var predicate = Expression.Lambda<Func<Review, bool>>(Expression.Equal(Expression.PropertyOrField(parameter, filterAfter), Expression.Constant(filterField)), parameter);

                if (sortMethod.CompareTo("ASCENDING") == 0)
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.book.ID == book.ID || p.book.isbn == book.isbn, null, currentPage, pageSize, new SortExpression<Review>(p => p.ID, ListSortDirection.Ascending));
                }
                else if (sortMethod.CompareTo("DESCENDING") == 0)
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.book.ID == book.ID || p.book.isbn == book.isbn, null, currentPage, pageSize, new SortExpression<Review>(p => p.ID, ListSortDirection.Descending));
                }
                else
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.book.ID == book.ID || p.book.isbn == book.isbn, null, currentPage, pageSize, null);
                }
            }
            else
            {
                if (sortMethod.CompareTo("ASCENDING") == 0)
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.book.ID == book.ID || p.book.isbn == book.isbn, null, currentPage, pageSize, new SortExpression<Review>(p => p.ID, ListSortDirection.Ascending));
                }
                else if (sortMethod.CompareTo("DESCENDING") == 0)
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.book.ID == book.ID || p.book.isbn == book.isbn, null, currentPage, pageSize, new SortExpression<Review>(p => p.ID, ListSortDirection.Descending));
                }
                else
                {
                    reviews = reviewRepo.GetSortOrFiltered(p => p.book.ID == book.ID || p.book.isbn == book.isbn, null, currentPage, pageSize, null);
                }
            }

            return reviews;
        }

    }
}