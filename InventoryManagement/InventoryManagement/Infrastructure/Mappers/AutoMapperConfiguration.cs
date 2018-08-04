using AutoMapper;
using InventoryManagement.Models;
using InventoryManagement.Views;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Infrastructure.Mappers
{
    public class AutoMapperConfiguration
    {
        public static void Initialize()
        {
            Mapper.Initialize(x =>
            {
                //x.CreateMap<FixedAssetsView, FixedAssets>().ForMember(e => e.categoryId, opt => opt.MapFrom(scr => scr.category.ID)).ForMember(e => e.category, opt => opt.Ignore());

                //x.CreateMap<InventoryObjectsView, InventoryObjects>().ForMember(e => e.categoryId, opt => opt.MapFrom(scr => scr.category.ID)).ForMember(e => e.category, opt => opt.Ignore());

                x.CreateMap<UserData, UserDataView>().ReverseMap();
                x.CreateMap<UserData, UserDataViewID>().ReverseMap();
                x.CreateMap<UserData, UserDataNoPass>().ReverseMap();
                x.CreateMap<Book, BookViewList>().ReverseMap();
                x.CreateMap<Genre, GenreViewName>().ReverseMap();
                x.CreateMap<Author, AuthorViewName>().ReverseMap();
                x.CreateMap<Rating, RatingView>().ReverseMap();
                x.CreateMap<Review, ReviewView>().ReverseMap();
                x.CreateMap<BookXUser, BookXUserView>().ReverseMap();
                x.CreateMap<UserData, UserFriendsView>().ReverseMap();
                x.CreateMap<UserData, UserDataViewProfileDetail>().ReverseMap();
                x.CreateMap<UserData, UserDataChange>().ReverseMap();
                x.CreateMap<UserDataView, UserDataChange>().ReverseMap();

            });
        }
    }

}