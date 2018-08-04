using InventoryManagement.Infrastructure;
using InventoryManagement.Models;
using InventoryManagement.Repositories;
using InventoryManagement.Views;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Description;
using InventoryManagement.jwt;
using System.Net.Http.Formatting;
using InventoryManagement.Services;

namespace InventoryManagement.Controllers
{
    [RoutePrefix("api/rating")]
    public class RatingController : ApiControllerBase
    {
        private IRatingService service;

        public RatingController(IRatingService serv, IEntityBaseRepository<Error> errorsRepository, IUnitOfWork unitOfWork) : base(errorsRepository, unitOfWork)
        {
            service = serv;
        }

        [HttpPost]
        [Route("GetRating")]
        [ResponseType(typeof(string))]
        [JwtAuthentication]
        public HttpResponseMessage GetAccount(HttpRequestMessage request, BookAndUserIDRating data)
        {
            return CreateHttpResponse(request, () =>
            {
                HttpResponseMessage response = null;
                RatingView rating = service.getRating(data.bookID,data.userID);
                if (rating != null)
                {
                    response = request.CreateResponse(HttpStatusCode.OK, rating, JsonMediaTypeFormatter.DefaultMediaType);
                }
                else
                    response = request.CreateResponse(HttpStatusCode.OK, false);
                return response;
            });
        }
    }
}