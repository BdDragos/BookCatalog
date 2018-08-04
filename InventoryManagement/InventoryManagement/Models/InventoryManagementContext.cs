using InventoryManagement.Data;
using Microsoft.Owin.BuilderProperties;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Data.Entity.ModelConfiguration.Conventions;
using System.Linq;
using System.Web;

namespace InventoryManagement.Models
{
    public class InventoryManagementContext : DbContext
    {
        // You can add custom code to this file. Changes will not be overwritten.
        // 
        // If you want Entity Framework to drop and regenerate your database
        // automatically whenever you change your model schema, please use data migrations.
        // For more information refer to the documentation:
        // http://msdn.microsoft.com/en-us/data/jj591621.aspx
    
        public InventoryManagementContext() : base("name=InventoryManagementContext")
        {
            Database.SetInitializer<InventoryManagementContext>(null);
        }

        public System.Data.Entity.DbSet<InventoryManagement.Models.UserData> UserData { get; set; }

        public System.Data.Entity.DbSet<InventoryManagement.Models.Book> Book { get; set; }

        public System.Data.Entity.DbSet<InventoryManagement.Models.Rating> Rating { get; set; }

        public System.Data.Entity.DbSet<InventoryManagement.Models.Review> Review { get; set; }

        public System.Data.Entity.DbSet<InventoryManagement.Models.BookXUser> BookXUser { get; set; }

        public System.Data.Entity.DbSet<InventoryManagement.Models.Genre> Genre { get; set; }

        public System.Data.Entity.DbSet<InventoryManagement.Models.Author> Author { get; set; }

        public virtual void Commit()
        {
            base.SaveChanges();
        }
        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            modelBuilder.Conventions.Remove<PluralizingTableNameConvention>();

            modelBuilder.Entity<Book>()
                .HasMany<Author>(s => s.author)
                .WithMany(c => c.book)
                .Map(cs =>
                {
                    cs.MapLeftKey("bookID");
                    cs.MapRightKey("authorID");
                    cs.ToTable("Book_x_Author");
                });

            modelBuilder.Entity<UserData>()
                .HasMany<UserData>(s => s.user)
                .WithMany()
                .Map(cs =>
                {
                    cs.MapLeftKey("userMainID");
                    cs.MapRightKey("userRelationID");
                    cs.ToTable("User_Relationship");
                });

            modelBuilder.Entity<Book>()
                .HasMany<Genre>(s => s.genre)
                .WithMany(c => c.book)
                .Map(cs =>
                {
                    cs.MapLeftKey("bookID");
                    cs.MapRightKey("genreID");
                    cs.ToTable("Book_x_Genre");
                });

            modelBuilder.Entity<BookXUser>()
                .HasRequired<Book>(s => s.book)
                .WithMany(g => g.bookXuser)
                .HasForeignKey<int>(s => s.bookID);


            modelBuilder.Entity<Rating>()
                .HasRequired<Book>(r => r.book)
                .WithMany(g => g.rating)
                .HasForeignKey<int>(s => s.bookID);

            modelBuilder.Entity<Review>()
                .HasRequired<UserData>(s => s.user)
                .WithMany(g => g.review)
                .HasForeignKey<int>(s => s.userID);


        }
    }
}

