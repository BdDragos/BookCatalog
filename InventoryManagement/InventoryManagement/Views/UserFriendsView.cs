using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Views
{
    public class UserFriendsView
    {
        public int ID { get; set; }
        public string username { get; set; }
        public byte[] userPic { get; set; }
        public string noOfFriends { get; set; }
        public virtual List<UserFriendsView> userList { get; set; }
        public UserFriendsView()
        {
            this.userList = new List<UserFriendsView>();
        }
    }
}