using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Views
{
    public class UserDataViewProfileDetail
    {
        public int ID { get; set; }
        public string username { get; set; }
        public byte[] userPic { get; set; }
        public string userOverview { get; set; }
        public DateTime joinedDate { get; set; }

        public UserDataViewID userLogged { get; set; }
    }
}