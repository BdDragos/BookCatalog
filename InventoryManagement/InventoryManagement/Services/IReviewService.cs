using InventoryManagement.Models;
using InventoryManagement.Views;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Services
{
    public interface IReviewService
    {
        IEnumerable<Review> getReviewFilteredPaginated(BookViewWithISBN bookID,int currentPage, int cageSize, string filterAfter, string filterField, string sortMethod);
        bool addReview(ReviewView review);
        Review getOneReview(int bookID, int userID);
        bool getOneReviewCheck(int bookID, int userID);
        bool deleteReview(int iD);
        IEnumerable<Review> getReviewAfterUser(string iDURL, int currentPage, int pageSize, string filterAfter, string filterField, string sortMethod);
    }
}