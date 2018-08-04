using InventoryManagement.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace InventoryManagement.Repositories
{
    public interface IEntityBaseRepository<T> where T : class, IEntityBase, new()
    {
        IQueryable<T> GetAll();
        T GetSingle(int id);
        IQueryable<T> FindBy(Expression<Func<T, bool>> predicate);
        void Add(T entity);
        void Delete(T entity);
        void Edit(T entity);

        IEnumerable<T> GetSortOrFiltered(
           Expression<Func<T, bool>> filter = null,
           string[] includePaths = null,
           int? page = 0,
           int? pageSize = null,
           params SortExpression<T>[] sortExpressions);
    }
}
