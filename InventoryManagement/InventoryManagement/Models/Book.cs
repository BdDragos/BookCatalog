using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Models
{
    public class Book : IEntityBase
    {
        public int ID { get; set; }
        public string title { get; set; }
        public string isbn { get; set; }
        public string series { get; set; }
        public int noPage { get; set; }
        public string edition { get; set; }
        public string bLanguage { get; set; }
        public byte[] bookPic { get; set; }
        public string publisherSite { get; set; }
        public string bookFormat { get; set; }
        public DateTime releaseDate { get; set; }
        public DateTime initialReleaseDate { get; set; }
        public string publisher { get; set; }
        public string overview { get; set; }
        public virtual ICollection<Author> author { get; set; }
        public virtual ICollection<Genre> genre { get; set; }
        public virtual ICollection<Rating> rating { get; set; }
        public virtual ICollection<Review> review { get; set; }
        public virtual ICollection<BookXUser> bookXuser { get; set; }

        public Book()
        {
            this.author = new HashSet<Author>();
            this.genre = new HashSet<Genre>();
            this.rating = new HashSet<Rating>();
            this.review = new HashSet<Review>();
            this.bookXuser = new HashSet<BookXUser>();
        }
    }
}