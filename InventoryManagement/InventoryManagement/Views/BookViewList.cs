using InventoryManagement.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Views
{
    public class BookViewList
    {
        public int ID { get; set; }
        public string title { get; set; }
        public byte[] bookPic { get; set; }
        public DateTime releaseDate { get; set; }
        public virtual List<AuthorViewName> author { get; set; }
        public virtual List<GenreViewName> genre { get; set; }
        public double rating { get; set; }
        public BookViewList()
        {
            this.author = new List<AuthorViewName>();
            this.genre = new List<GenreViewName>();
        }
    }
}