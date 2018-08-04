using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Views
{
    public class BookViewWithISBN
    {
        public int ID { get; set; }
        public string isbn { get; set; }
        public string loggedUserID { get; set; }
    }
}