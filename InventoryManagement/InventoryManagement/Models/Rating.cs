using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Web;

namespace InventoryManagement.Models
{
    public class Rating : IEntityBase
    {
        public int ID { get; set; }

        public double ratingScore { get; set; }

        public int userID { get; set; }

        public int bookID { get; set; }
        public virtual UserData user { get; set; }
        public virtual Book book { get; set; }

        public Rating()
        {
            
        }
    }
}