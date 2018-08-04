using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Views
{
    public class BookXUserView
    {
        public int ID { get; set; }
        public int bookID { get; set; }
        public int userID { get; set; }
        public string bookShelf { get; set; }
    }
}