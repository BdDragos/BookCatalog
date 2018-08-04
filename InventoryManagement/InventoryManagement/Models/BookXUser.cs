using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Models
{
    public class BookXUser : IEntityBase
    { 
        public int ID { get; set; }
        public int bookID { get; set; }
        public virtual Book book { get; set; }
        public int userID { get; set; }
        public virtual UserData user { get; set; }
        public string bookShelf { get; set; }

    }
    
}