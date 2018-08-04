using InventoryManagement.Infrastructure.Mappers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Http;
using System.Web.Mvc;
using System.Web.Routing;

namespace InventoryManagement
{
    public class WebApiApplication : System.Web.HttpApplication
    {
        protected void Application_Start()
        {
            var config = GlobalConfiguration.Configuration;

            config.Formatters.JsonFormatter
            .SerializerSettings
            .ReferenceLoopHandling = Newtonsoft.Json.ReferenceLoopHandling.Ignore;

            AutoMapperConfiguration.Initialize();
            AreaRegistration.RegisterAllAreas();
            WebApiConfig.Register(config);
            GlobalConfiguration.Configuration.EnsureInitialized();
        }

        public void Application_BeginRequest(object sender, EventArgs e)
        {
            string httpOrigin = Request.Headers["Origin"];
            if (httpOrigin != null)
            {
                HttpContext.Current.Response.AddHeader("Access-Control-Allow-Origin", httpOrigin);
            }

            string method = Request.Headers["Access-Control-Request-Method"];
            if (method != null)
            {
                HttpContext.Current.Response.AddHeader("Access-Control-Allow-Methods", method);
            }

            string headers = Request.Headers["Access-Control-Request-Headers"];
            if (headers != null)
            {
                HttpContext.Current.Response.AddHeader("Access-Control-Allow-Headers", headers);
            }

            HttpContext.Current.Response.AddHeader("Access-Control-Allow-Credentials", "true");

            if (Request.HttpMethod == "OPTIONS")
            {
                HttpContext.Current.Response.StatusCode = 200;
                var httpApplication = sender as HttpApplication;
                httpApplication.CompleteRequest();
            }
        }
    }


}
