package catwalks.block;

import java.util.List;

import catwalks.Conf;
import catwalks.Const;
import catwalks.item.ItemBlockScaffold;
import catwalks.register.ItemRegister;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScaffolding extends BlockBase {
		
	public BlockScaffolding() {
		super(Material.iron, "scaffold", ItemBlockScaffold.class);
		setHardness(0.04f);
	}
	
	@Override
	public float getBlockHardness(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		return state.getValue(Const.MATERIAL) == EnumCatwalkMaterial.WOOD ? 0 : this.blockHardness;
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer playerIn, World worldIn, BlockPos pos) {
		if(playerIn.getCurrentEquippedItem() != null && playerIn.getCurrentEquippedItem().getItem() == ItemRegister.tool)
			return 1f;
		return super.getPlayerRelativeBlockHardness(playerIn, worldIn, pos);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
	
	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}
	
	{ /* state stuff */ }
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, Const.MATERIAL);
	}
	
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, 0));
		list.add(new ItemStack(itemIn, 1, 1));
		list.add(new ItemStack(itemIn, 1, 2));
		list.add(new ItemStack(itemIn, 1, 3));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(Const.MATERIAL, EnumCatwalkMaterial.values()[meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(Const.MATERIAL).ordinal();
	}
	
	{ /* rendering stuff */ }
	
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT_MIPPED;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
		if(Conf.showScaffoldInsideFaces)
			return super.shouldSideBeRendered(worldIn, pos, side);
        return worldIn.getBlockState(pos).getBlock() == this ? false : super.shouldSideBeRendered(worldIn, pos, side);
    }
}
