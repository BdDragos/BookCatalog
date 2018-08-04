using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using AutoMapper;
using InventoryManagement.Models;
using InventoryManagement.Repositories;
using InventoryManagement.Views;

namespace InventoryManagement.Services
{
    public class RatingService : IRatingService
    {
        private IEntityBaseRepository<Rating> ratingRepo;
        public RatingService(IEntityBaseRepository<Rating> repo)
        {
            this.ratingRepo = repo;
        }

        public RatingService()
        {

        }

        public RatingView getRating(int bookID, int userID)
        {
            Rating foundRating = ratingRepo.FindBy(obj => obj.bookID == bookID && obj.userID == userID).FirstOrDefault();
            if (foundRating == null)
            {
                return null;
            }
            else
            {
                RatingView convertedRating = Mapper.Map<Rating, RatingView>(foundRating);
                return convertedRating;
            }
        }

        public bool checkIfRatingExists(int bookID, int userID)
        {
            return ratingRepo.FindBy(obj => obj.bookID == bookID && obj.userID == userID).Any();

        }

        public bool addRating(RatingView rating)
        {
            Rating ratingModel = Mapper.Map<RatingView, Rating>(rating);
            ratingRepo.Add(ratingModel);
            return true;
        }

        public bool deleteRating(int userID)
        {
            Rating rev = ratingRepo.FindBy(obj => obj.ID == userID).FirstOrDefault();
            ratingRepo.Delete(rev);
            return true;
        }
    }
    
}