package catwalks.item;

import catwalks.block.EnumCatwalkMaterialOld;
import catwalks.movement.MovementHandler;
import catwalks.util.ExtendUtils;
import catwalks.util.GeneralUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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
    	return super.getUnlocalizedName(stack) + "." + EnumCatwalkMaterialOld.values()[stack.getItemDamage()].getName().toLowerCase();
    }
	
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
    		EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		BlockPos oldPos = pos;
		EnumFacing oldFacing = facing;
		if(playerIn.isSneaking()) {
			Tuple<BlockPos, EnumFacing> tuple = ExtendUtils.getExtendPos(stack, playerIn, worldIn, pos, facing, this.block.getStateFromMeta(stack.getItemDamage()));
			pos  = tuple.getFirst();
			facing = tuple.getSecond();
		}
		
		Entity checkPlayer = null;
		
		if(oldFacing == facing) { // we didn't extend
			if( stack.getMetadata() == EnumCatwalkMaterialOld.WOOD.ordinal()) {
				if( facing == EnumFacing.UP && playerIn.getEntityBoundingBox().intersectsWith(new AxisAlignedBB(pos.offset(EnumFacing.UP))) ) {
					double moveUpAmount = (pos.getY()+2) - playerIn.posY;
					MovementHandler.INSTANCE.setPlayerNerdPoleMove(playerIn, moveUpAmount, 2);
					checkPlayer = playerIn;
				}
			}
		}
		
		EnumActionResult result = onItemUseNoSound(stack, playerIn, checkPlayer, worldIn, pos, facing, hitX, hitY, hitZ);
		if(result == EnumActionResult.SUCCESS) {
          SoundType soundtype = this.block.getSoundType();
          worldIn.playSound(playerIn, oldPos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
		}
		return result;
	}

	public EnumActionResult onItemUseNoSound(ItemStack stack, EntityPlayer playerIn, Entity checkPlayer, World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(facing);
        }

        if (stack.stackSize != 0 && playerIn.canPlayerEdit(pos, facing, stack) && worldIn.canBlockBePlaced(this.block, pos, false, facing, checkPlayer, stack))
        {
            int i = this.getMetadata(stack.getMetadata());
            IBlockState iblockstate1 = this.block.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, i, playerIn);

            if (placeBlockAt(stack, playerIn, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
            {
//                SoundType soundtype = this.block.getSoundType();
//                worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                --stack.stackSize;
            }

            return EnumActionResult.SUCCESS;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }
	
	@SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
		EnumFacing oldSide = side; // used to detect if we should extend
		if(player.isSneaking()) {
			Tuple<BlockPos, EnumFacing> tuple = ExtendUtils.getExtendPos(stack, player, worldIn, pos, side, this.block.getStateFromMeta(stack.getItemDamage()));
			pos  = tuple.getFirst();
			side = tuple.getSecond();
		}
		
		if(oldSide == side) { // we aren't extending
			
			if( stack.getMetadata() == EnumCatwalkMaterialOld.WOOD.ordinal()) {
				if( side == EnumFacing.UP && player.getEntityBoundingBox().intersectsWith(new AxisAlignedBB(pos.offset(EnumFacing.UP))) ) {
					Vec3d moveUpAmount = new Vec3d( 0, (pos.getY()+2) - player.posY, 0 );
					if(moveUpAmount.equals( GeneralUtil.simulateEntityMove(player, moveUpAmount)) ) {
						return canPlaceBlockOnSideIgnorePlayer(worldIn, pos, side, player, stack);
					}
				}
			}
		}
		
        return super.canPlaceBlockOnSide(worldIn, pos, side, player, stack);
    }
	
	@SideOnly(Side.CLIENT)
    public boolean canPlaceBlockOnSideIgnorePlayer(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
    {
        Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos))
        {
            side = EnumFacing.UP;
        }
        else if (!block.isReplaceable(worldIn, pos))
        {
            pos = pos.offset(side);
        }

        return worldIn.canBlockBePlaced(this.block, pos, false, side, player, stack);
    }
	
}
