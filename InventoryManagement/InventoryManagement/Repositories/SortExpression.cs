using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Linq.Expressions;
using System.Web;

namespace InventoryManagement.Repositories
{
    public class SortExpression<TEntity> where TEntity : class
    {
        public SortExpression(Expression<Func<TEntity, object>> sortBy, ListSortDirection sortDirection)
        {
            SortBy = sortBy;
            SortDirection = sortDirection;
        }

        public Expression<Func<TEntity, object>> SortBy { get; set; }
        public ListSortDirection SortDirection { get; set; }
    }
}