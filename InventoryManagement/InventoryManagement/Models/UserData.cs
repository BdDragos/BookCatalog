using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Models
{
    public class UserData : IEntityBase
    {
        public int ID { get; set; }
        public string username { get; set; }
        public string userpass { get; set; }
        public string email { get; set; }
        public byte[] userPic { get; set; }
        public string userOverview { get; set; }
        public DateTime joinedDate { get; set; }
        public virtual ICollection<Review> review { get; set; }
        public virtual ICollection<Rating> rating { get; set; }
        public virtual ICollection<UserData> user { get; set; }

        public UserData()
        {
            this.review = new HashSet<Review>();
            this.rating = new HashSet<Rating>();
            this.user = new HashSet<UserData>();
        }
    }
}