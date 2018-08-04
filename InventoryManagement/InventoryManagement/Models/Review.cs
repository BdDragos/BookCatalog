using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Web;

namespace InventoryManagement.Models
{
    public class Review : IEntityBase
    {
        public int ID { get; set; }

        public string reviewText { get; set; }

        public double reviewScore { get; set; }

        public int bookID { get; set; }

        public int userID { get; set; }

        public virtual Book book { get; set; }

        public DateTime addedTime { get; set; }

        public virtual UserData user { get; set; }

        public Review()
        {

        }
    }
}