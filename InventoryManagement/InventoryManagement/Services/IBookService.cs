using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using InventoryManagement.Models;
using InventoryManagement.Views;

namespace InventoryManagement.Services
{
    public interface IBookService<T> where T : class, new()
    {
        List<T> getBook();
        IEnumerable<T> getBookFilteredPaginated(int currentPage, int cageSize, string filterAfter, string filterField, string sortMethod);
        T getSingleBook(BookViewWithISBN bookID);
    }
}