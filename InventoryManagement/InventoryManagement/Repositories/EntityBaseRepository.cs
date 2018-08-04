using InventoryManagement.Infrastructure;
using InventoryManagement.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Data.Entity.Migrations;
using System.Linq;
using System.Linq.Expressions;
using System.Web;

namespace InventoryManagement.Repositories
{
    public class EntityBaseRepository<T> : IEntityBaseRepository<T> where T : class, IEntityBase, new()
    {
        private InventoryManagementContext dataContext;

        #region Properties
        protected IDbFactory DbFactory
        {
            get;
            private set;
        }

        protected InventoryManagementContext DbContext
        {
            get { return dataContext ?? (dataContext = DbFactory.Init()); }
        }
        public EntityBaseRepository(IDbFactory dbFactory)
        {
            DbFactory = dbFactory;
        }
        #endregion
        public virtual IQueryable<T> GetAll()
        {
            return DbContext.Set<T>();
        }

        public T GetSingle(int id)
        {
            return GetAll().FirstOrDefault(x => x.ID == id);
        }


        public IEnumerable<T> GetSortOrFiltered(Expression<Func<T, bool>> filter = null,string[] includePaths = null,int? page = null,int? pageSize = null,params SortExpression<T>[] sortExpressions)
        {
            IQueryable<T> query = DbContext.Set<T>();

            if (filter != null)
            {
                query = query.Where(filter);
            }

            if (includePaths != null)
            {
                for (var i = 0; i < includePaths.Count(); i++)
                {
                    query = query.Include(includePaths[i]);
                }
            }

            if (sortExpressions != null)
            {
                IOrderedQueryable<T> orderedQuery = null;
                for (var i = 0; i < sortExpressions.Count(); i++)
                {
                    if (i == 0)
                    {
                        if (sortExpressions[i].SortDirection == ListSortDirection.Ascending)
                        {
                            orderedQuery = query.OrderBy(sortExpressions[i].SortBy);
                        }
                        else
                        {
                            orderedQuery = query.OrderByDescending(sortExpressions[i].SortBy);
                        }
                    }
                    else
                    {
                        if (sortExpressions[i].SortDirection == ListSortDirection.Ascending)
                        {
                            orderedQuery = orderedQuery.ThenBy(sortExpressions[i].SortBy);
                        }
                        else
                        {
                            orderedQuery = orderedQuery.ThenByDescending(sortExpressions[i].SortBy);
                        }

                    }
                }

                if (page != null)
                {
                    query = orderedQuery.Skip(((int)page - 1) * (int)pageSize);
                }
            }

            if (pageSize != null && page != null)
            {
                query = query.OrderBy(g => g.ID).Skip(((int)page) * (int)pageSize).Take((int)pageSize);
            }

            return query.ToList();
        }


        public virtual IQueryable<T> FindBy(Expression<Func<T, bool>> predicate)
        {
            return DbContext.Set<T>().Where(predicate);
        }

        public virtual void Add(T entity)
        {
            DbEntityEntry dbEntityEntry = DbContext.Entry<T>(entity);
            DbContext.Set<T>().Add(entity);
        }

        public virtual void Edit(T entity)
        {
            DbEntityEntry dbEntityEntry = DbContext.Entry<T>(entity);
            //DbContext.Set<T>().Attach(entity);
            dbEntityEntry.State = EntityState.Modified;
        }
        public virtual void Delete(T entity)
        {
            DbEntityEntry dbEntityEntry = DbContext.Entry<T>(entity);
            dbEntityEntry.State = EntityState.Deleted;
        }

        public virtual void DeleteWhere(Expression<Func<T, bool>> predicate)
        {
            IEnumerable<T> entities = DbContext.Set<T>().Where(predicate);

            foreach (var entity in entities)
            {
                DbContext.Entry<T>(entity).State = EntityState.Deleted;
            }
        }

        public virtual void Commit()
        {
            DbContext.SaveChanges();
        }
    }
}