using InventoryManagement.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace InventoryManagement.Services
{
    public interface IUserBookService
    {
        bool deleteUserBook(BookXUser bookUser);
        bool addUserBook(BookXUser bookUser);
        IEnumerable<Book> getUserBooks(int currentPage, int pageSize, string userID, string bookShelf);
    }
}
