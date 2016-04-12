package catwalks.item;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityItemDecoration extends EntityItem {

	public EntityItemDecoration(World worldIn, double x, double y, double z, ItemStack stack) {
		super(worldIn, x, y, z, stack);
		// TODO Auto-generated constructor stub
	}
	
	public EntityItemDecoration(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
		// TODO Auto-generated constructor stub
	}
	
	public EntityItemDecoration(World worldIn) {
		super(worldIn);
		// TODO Auto-generated constructor stub
	}

	/**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
    	super.onUpdate();
    	
        ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
        if (stack != null && stack.getItem() != null && stack.getItem().onEntityItemUpdate(this)) return;
        if(this.getEntityItem() != null)
        {
            boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;

            if (flag || this.ticksExisted % 25 == 0)
            {
                if (!this.worldObj.isRemote)
                {
                    this.combineDecorationsNearby();
                }
            }
        }
    }
    
    public void combineDecorationsNearby()
    {
        for (EntityItem entityitem : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(0.5D, 0.0D, 0.5D)))
        {
            this.combineDecorations(entityitem);
        }
    }
    
    public boolean combineDecorations(EntityItem other)
    {
        if (other == this)
        {
            return false;
        }
        else if (other.isEntityAlive() && this.isEntityAlive())
        {
            ItemStack itemstack = this.getEntityItem();
            ItemStack itemstack1 = other.getEntityItem();
            
	        if (itemstack1.getItem() != itemstack.getItem())
	        {
	            return false;
	        }
	        else if (itemstack1.getItemDamage() - (itemstack.getMaxDamage()-itemstack.getItemDamage()) < 0) {
	        	return false;
	        }
	        else
	        {
	        	itemstack1.setItemDamage( itemstack1.getItemDamage() - (itemstack.getMaxDamage()-itemstack.getItemDamage()));
	            other.setEntityItemStack(itemstack1);
	            this.setDead();
	            return true;
	        }
        }
        else
        {
            return false;
        }
    }
	
}
