using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using InventoryManagement.Models;
using InventoryManagement.Repositories;

namespace InventoryManagement.Services
{
    public class UserBookService : IUserBookService
    {

        private IEntityBaseRepository<BookXUser> userBookSerivce;
        public UserBookService(IEntityBaseRepository<BookXUser> repo)
        {
            this.userBookSerivce = repo;
        }

        public UserBookService()
        {

        }

        public bool addUserBook(BookXUser bookUser)
        {
            if (bookUser.bookShelf.Equals("READ") || bookUser.bookShelf.Equals("TOREAD"))
            {
                deleteUserBook(bookUser);
                userBookSerivce.Add(bookUser);
                return true;
            }
            else
                return false;
        }

        public bool deleteUserBook(BookXUser bookUser)
        {
            if (bookUser.bookShelf.Equals("READ") || bookUser.bookShelf.Equals("TOREAD"))
            {
                BookXUser newBook = userBookSerivce.FindBy(b => b.userID == bookUser.userID && b.bookID == bookUser.bookID).FirstOrDefault();
                if (newBook != null)
                {
                    userBookSerivce.Delete(newBook);
                    return true;
                }
                else
                    return false;

            }
            else
                return false;
        }

        public IEnumerable<Book> getUserBooks(int currentPage, int pageSize, string userID, string bookShelf)
        {
            int idint = Int32.Parse(userID);
            List<BookXUser> source = userBookSerivce.FindBy(b => b.userID == idint && b.bookShelf.CompareTo(bookShelf) == 0).ToList();

            List<Book> bookSource = source.Select(b => b.book).ToList();
            int count = bookSource.Count();
            int CurrentPage = currentPage;
            int PageSize = pageSize;
            var items = bookSource.Skip(CurrentPage * PageSize).Take(PageSize);
            return items;

        }
    }
}