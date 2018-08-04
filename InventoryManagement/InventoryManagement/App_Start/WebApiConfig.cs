using InventoryManagement.App_Start;
using InventoryManagement.Infrastructure;
using InventoryManagement.Models;
using InventoryManagement.Repositories;
using InventoryManagement.Services;
using InventoryManagement.Views;
using Microsoft.Practices.Unity;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Cors;
using System.Web.Http.Routing;

namespace InventoryManagement
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            // Web API configuration and services
            AutofacWebapiConfig.Initialize(GlobalConfiguration.Configuration);
            // Web API routes
            //var cors = new EnableCorsAttribute("http://IP:PORT", "*", "*") { SupportsCredentials = true };
            //config.EnableCors(cors);
            config.Filters.Add(new AuthorizeAttribute());

            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute(name: "DefaultApi", routeTemplate: "api/{controller}/{id}", defaults: new { id = RouteParameter.Optional });

            config.Routes.MapHttpRoute("DefaultApiWithId", "Api/{controller}/{id}", new { id = RouteParameter.Optional }, new { id = @"\d+" });
            config.Routes.MapHttpRoute("DefaultApiWithAction", "Api/{controller}/{action}");
            config.Routes.MapHttpRoute("DefaultApiGet", "Api/{controller}", new { action = "Get" }, new { httpMethod = new HttpMethodConstraint(HttpMethod.Get) });
            config.Routes.MapHttpRoute("DefaultApiPost", "Api/{controller}", new { action = "Post" }, new { httpMethod = new HttpMethodConstraint(HttpMethod.Post) });
        }
    }
}
