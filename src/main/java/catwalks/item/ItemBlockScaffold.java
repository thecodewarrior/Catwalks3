package catwalks.item;

import catwalks.block.EnumCatwalkMaterial;
import catwalks.util.ExtendUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
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
		if(playerIn.isSneaking()) {
			Tuple<BlockPos, EnumFacing> tuple = ExtendUtils.getExtendPos(stack, playerIn, worldIn, pos, side, this.block.getStateFromMeta(stack.getItemDamage()));
			pos  = tuple.getFirst();
			side = tuple.getSecond();
		}
		return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
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
