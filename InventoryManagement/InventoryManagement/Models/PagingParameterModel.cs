using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace InventoryManagement.Models
{
    public class PagingParameterModel
    {
        public int pageNumber { get; set; }

        public int _pageSize { get; set; }

        public int pageSize { get; set; }

    }
}