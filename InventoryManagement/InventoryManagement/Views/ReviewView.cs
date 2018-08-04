using InventoryManagement.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Views
{
    public class ReviewView
    {
        public int ID { get; set; }

        public string reviewText { get; set; }

        public int bookID { get; set; }

        public int userID { get; set; }

        public DateTime addedTime { get; set; }

        public UserDataNoPass user { get; set; }

        public BookViewDetail book { get; set; }

        public RatingView rating { get; set; }

        public double ratingScore { get; set; }

        public ReviewView()
        {

        }
    }
}