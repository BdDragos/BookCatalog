using Autofac;
using Autofac.Integration.WebApi;
using InventoryManagement.Infrastructure;
using InventoryManagement.Models;
using InventoryManagement.Repositories;
using InventoryManagement.Services;
using InventoryManagement.Views;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Reflection;
using System.Web;
using System.Web.Http;

namespace InventoryManagement.App_Start
{
    public class AutofacWebapiConfig
    {
        public static IContainer Container;
        public static void Initialize(HttpConfiguration config)
        {
            Initialize(config, RegisterServices(new ContainerBuilder()));
        }

        public static void Initialize(HttpConfiguration config, IContainer container)
        {
            config.DependencyResolver = new AutofacWebApiDependencyResolver(container);
        }

        private static IContainer RegisterServices(ContainerBuilder builder)
        {
            builder.RegisterApiControllers(Assembly.GetExecutingAssembly());
            
            builder.RegisterType<InventoryManagementContext>()
                   .As<DbContext>()
                   .InstancePerRequest();

            builder.RegisterType<DbFactory>()
                .As<IDbFactory>()
                .InstancePerRequest();

            builder.RegisterType<UnitOfWork>()
                .As<IUnitOfWork>()
                .InstancePerRequest();

            builder.RegisterGeneric(typeof(EntityBaseRepository<>))
                   .As(typeof(IEntityBaseRepository<>))
                   .InstancePerRequest();

            builder.RegisterType<UserDataService>()
                .As(typeof(IUserDataService<UserDataView>))
                .InstancePerRequest();

            builder.RegisterType<BookService>()
                .As(typeof(IBookService<Book>))
                .InstancePerRequest();


            builder.RegisterType<ReviewService>()
                .As(typeof(IReviewService))
                .InstancePerRequest();

            builder.RegisterType<RatingService>()
                .As(typeof(IRatingService))
                .InstancePerRequest();

            builder.RegisterType<UserBookService>()
                .As(typeof(IUserBookService))
                .InstancePerRequest();

            Container = builder.Build();

            return Container;
        }
    }
}