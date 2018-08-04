using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Models
{
    public class Author : IEntityBase
    {
        public int ID { get; set; }

        public string authorName { get; set; }

        public virtual ICollection<Book> book { get; set; }

        public Author()
        {
            this.book = new HashSet<Book>();
        }
    }
}