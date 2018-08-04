using InventoryManagement.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Infrastructure
{
    public interface IDbFactory : IDisposable
    {
        InventoryManagementContext Init();
    }

}