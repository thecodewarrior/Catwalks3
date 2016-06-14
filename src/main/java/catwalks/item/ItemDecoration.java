package catwalks.item;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import catwalks.block.IDecoratable;

public class ItemDecoration extends ItemBase {

	private int maxDamage = 256;
	
	public ItemDecoration(String name) {
		super(name);
		setMaxStackSize(1);
		setMaxDamage(maxDamage);
		setUnlocalizedName("decoration."+name);
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add( I18n.format("item.decoration.uses", stack.getMaxDamage() - stack.getItemDamage()) );
		tooltip.add( I18n.format("item.decoration.combine") );
		tooltip.add( I18n.format("item.decoration.split") );
		super.addInformation(stack, playerIn, tooltip, advanced);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		if( (  stack.getItemDamage() == this.getMaxDamage(stack) && !playerIn.isSneaking()  ) || (  stack.getItemDamage() == 0 && playerIn.isSneaking()  )
				&& !playerIn.capabilities.isCreativeMode) {
			return EnumActionResult.FAIL;
		}
		
		IBlockState state = worldIn.getBlockState(pos);
		
		if(state.getBlock() instanceof IDecoratable) {
			if( ((IDecoratable) state.getBlock()).putDecoration(worldIn, pos, name, !playerIn.isSneaking()) ) {
				stack.damageItem(playerIn.isSneaking() ? -1 : 1, playerIn);
				return EnumActionResult.SUCCESS;
			}
		}
		
		return EnumActionResult.PASS;
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
