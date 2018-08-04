using InventoryManagement.Models;
using System;
using System.Collections.Generic;
using System.Data.Entity.ModelConfiguration;
using System.Linq;
using System.Web;

namespace InventoryManagement.Data
{
    public class EntityBaseConfig<T> : EntityTypeConfiguration<T> where T : class, IEntityBase
    {
        public EntityBaseConfig()
        {
            HasKey(e => e.ID);
        }
    }
}