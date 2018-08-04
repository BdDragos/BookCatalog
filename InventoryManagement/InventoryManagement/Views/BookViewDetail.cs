using InventoryManagement.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Views
{
    public class BookViewDetail
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
        public virtual ICollection<AuthorViewName> author { get; set; }
        public virtual ICollection<GenreViewName> genre { get; set; }
        public double rating { get; set; }

        public UserDataViewID user { get; set; }

        public BookViewDetail()
        {
            this.author = new HashSet<AuthorViewName>();
            this.genre = new HashSet<GenreViewName>();
        }
    }
}