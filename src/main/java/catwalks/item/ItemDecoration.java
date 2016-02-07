package catwalks.item;

import catwalks.block.BlockCatwalk;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemDecoration extends ItemBase {

	private int maxDamage = 256;
	
	public ItemDecoration(String name) {
		super(name);
		setMaxStackSize(1);
		setMaxDamage(maxDamage);
		setUnlocalizedName("decoration."+name);
	}
	
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		
		if( (  stack.getItemDamage() == this.getMaxDamage(stack) && !playerIn.isSneaking()  ) || (  stack.getItemDamage() == 0 && playerIn.isSneaking()  ) ) {
			return false;
		}
		
		IBlockState state = worldIn.getBlockState(pos);
		
		if(state.getBlock() instanceof BlockCatwalk) {
			if( ((BlockCatwalk) state.getBlock()).putDecoration(worldIn, pos, name, !playerIn.isSneaking()) ) {
				stack.damageItem(playerIn.isSneaking() ? -1 : 1, playerIn);
			}
		}
		
		return false;
	}
	
	public void damageItem(ItemStack stack, int amount, EntityLivingBase entityIn) {
		if (entityIn instanceof EntityPlayer && ((EntityPlayer)entityIn).capabilities.isCreativeMode) {
			return;
		}
		stack.setItemDamage(stack.getItemDamage()-amount);
    }
	
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		Entity e = new EntityItemDecoration(world, location.posX, location.posY, location.posZ, itemstack);
		NBTTagCompound tag = new NBTTagCompound();
		location.writeToNBT(tag);
		e.readFromNBT(tag);
		return e;
	}
	
}