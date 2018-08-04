using InventoryManagement.Models;
using InventoryManagement.Repositories;
using InventoryManagement.Views;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Linq.Expressions;
using System.Reflection;
using System.Web;

namespace InventoryManagement.Services
{
    public class BookService : IBookService<Book>
    {
        private IEntityBaseRepository<Book> bookRepo;

        public BookService(IEntityBaseRepository<Book> repo)
        {
            bookRepo = repo;
        }
        public List<Book> getBook()
        {
            List<Book> objects = new List<Book>();
            objects = bookRepo.GetAll().ToList();
            return objects;
        }

        public IEnumerable<Book> getBookFilteredPaginated(int currentPage, int pageSize, string filterAfter,string filterField, string sortMethod)
        {
            IEnumerable<Book> books = null;

            bool ok = filterAfter.Equals("null");
            if (!ok)
            {

                var parameter = Expression.Parameter(typeof(Book), "b");
                var containsMethod = typeof(string).GetMethod(nameof(string.Contains), new[] { typeof(string) });
                var predicate = Expression.Lambda<Func<Book, bool>>(
                    Expression.Call(
                        Expression.PropertyOrField(parameter, filterAfter)
                    , containsMethod
                    , new Expression[] { Expression.Constant(filterField) }
                    )
                , parameter
                );

                if (sortMethod.CompareTo("ASCENDING") == 0)
                {
                    books = bookRepo.GetSortOrFiltered(predicate, null, currentPage, pageSize, new SortExpression<Book>(p => p.ID, ListSortDirection.Ascending));
                }
                else if (sortMethod.CompareTo("DESCENDING") == 0)
                {
                    books = bookRepo.GetSortOrFiltered(predicate, null, currentPage, pageSize, new SortExpression<Book>(p => p.ID, ListSortDirection.Descending));
                }
                else 
                {
                    books = bookRepo.GetSortOrFiltered(predicate, null, currentPage, pageSize, null);
                }
            }
            else
            {
                if (sortMethod.CompareTo("ASCENDING") == 0)
                {
                    books = bookRepo.GetSortOrFiltered(null, null, currentPage, pageSize, new SortExpression<Book>(p => p.ID, ListSortDirection.Ascending));
                }
                else if (sortMethod.CompareTo("DESCENDING") == 0)
                {
                    books = bookRepo.GetSortOrFiltered(null, null, currentPage, pageSize, new SortExpression<Book>(p => p.ID, ListSortDirection.Descending));
                }
                else
                {
                    books = bookRepo.GetSortOrFiltered(null, null, currentPage, pageSize, null);
                }
            }


            return books;
        }


        public Book getSingleBook(BookViewWithISBN bookID)
        {
            Book foundBook = bookRepo.FindBy(b => b.ID == bookID.ID).FirstOrDefault();
            Book foundBookISBN = bookRepo.FindBy(b => b.isbn == bookID.isbn).FirstOrDefault();

            if (foundBook != null)
            {
                return foundBook;
            }
            else
            {
                return foundBookISBN;
            }

        }

    }
}