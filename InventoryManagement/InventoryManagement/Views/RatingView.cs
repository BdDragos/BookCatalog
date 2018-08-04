using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Views
{
    public class RatingView
    {
        public int ID { get; set; }

        public double ratingScore { get; set; }

        public int bookID { get; set; }

        public int userID { get; set; }
    }
}