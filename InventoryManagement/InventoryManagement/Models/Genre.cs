using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Models
{
    public class Genre : IEntityBase
    {
        public int ID { get; set; }

        public string genreName { get; set; }

        public virtual ICollection<Book> book { get; set; }

        public Genre()
        {
            this.book = new HashSet<Book>();
        }
    }
}