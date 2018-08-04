using InventoryManagement.Views;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace InventoryManagement.Services
{
    public interface IRatingService
    {
        RatingView getRating(int bookID, int userID);
        bool addRating(RatingView rating);

        bool checkIfRatingExists(int bookID, int userID);
        bool deleteRating(int userID);
    }
}
