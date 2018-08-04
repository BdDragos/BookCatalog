using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Http.Controllers;

namespace InventoryManagement.Areas.HelpPage
{
    public interface IResponseDocumentationProvider
    {
        string GetResponseDocumentation(HttpActionDescriptor actionDescriptor);
    }
}