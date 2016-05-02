package catwalks.item;

import catwalks.block.EnumCatwalkMaterial;
import catwalks.util.ExtendUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockScaffold extends ItemBlock {

	public ItemBlockScaffold(Block block) {
		super(block);
	    this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(int damage)
    {
        return damage;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
    	return super.getUnlocalizedName(stack) + "." + EnumCatwalkMaterial.values()[stack.getItemDamage()].getName().toLowerCase();
    }
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		BlockPos oldPos = pos;
		if(playerIn.isSneaking()) {
			Tuple<BlockPos, EnumFacing> tuple = ExtendUtils.getExtendPos(stack, playerIn, worldIn, pos, side, this.block.getStateFromMeta(stack.getItemDamage()));
			pos  = tuple.getFirst();
			side = tuple.getSecond();
		}
		boolean success = onItemUseNoSound(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
		if(success) {
			worldIn.playSoundEffect((double)((float)oldPos.getX() + 0.5F), (double)((float)oldPos.getY() + 0.5F), (double)((float)oldPos.getZ() + 0.5F), this.block.stepSound.getPlaceSound(), (this.block.stepSound.getVolume() + 1.0F) / 2.0F, this.block.stepSound.getFrequency() * 0.8F);
		}
		return success;
	}

	public boolean onItemUseNoSound(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(side);
        }

        if (stack.stackSize == 0)
        {
            return false;
        }
        else if (!playerIn.canPlayerEdit(pos, side, stack))
        {
            return false;
        }
        else if (worldIn.canBlockBePlaced(this.block, pos, false, side, (Entity)null, stack))
        {
            int i = this.getMetadata(stack.getMetadata());
            IBlockState iblockstate1 = this.block.onBlockPlaced(worldIn, pos, side, hitX, hitY, hitZ, i, playerIn);

            if (placeBlockAt(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ, iblockstate1))
            {
                --stack.stackSize;
            }

            return true;
        }
        else
        {
            return false;
        }
    }
	
	@SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
		if(player.isSneaking()) {
			Tuple<BlockPos, EnumFacing> tuple = ExtendUtils.getExtendPos(stack, player, worldIn, pos, side, this.block.getStateFromMeta(stack.getItemDamage()));
			pos  = tuple.getFirst();
			side = tuple.getSecond();
		}
		
        return super.canPlaceBlockOnSide(worldIn, pos, side, player, stack);
    }
	
}
