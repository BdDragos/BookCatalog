using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Views
{
    public class UserDataView
    {
        public int ID { get; set; }
        public string username { get; set; }
        public string userpass { get; set; }
        public string email { get; set; }
        public byte[] userPic { get; set; }
    }
}