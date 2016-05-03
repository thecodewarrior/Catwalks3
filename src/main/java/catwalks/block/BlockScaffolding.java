package catwalks.block;

import java.util.List;

import catwalks.Conf;
import catwalks.Const;
import catwalks.item.ItemBlockScaffold;
import catwalks.register.ItemRegister;
import catwalks.shade.ccl.raytracer.RayTracer;
import catwalks.util.ExtendUtils;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScaffolding extends BlockBase {
		
	public BlockScaffolding() {
		super(Material.IRON, "scaffold", (c) -> new ItemBlockScaffold(c));
		setHardness(0.1f);
	}
	
	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		RayTraceResult hit = RayTracer.retrace(playerIn);
		if(hit == null) { // don't want to NPE
			Logs.error("Hit was null in Scaffold onBlockClicked at (%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
			return;
		}
		
		if(playerIn.isSneaking() && playerIn.inventory.getCurrentItem() != null && playerIn.inventory.getCurrentItem().getItem() == Item.getItemFromBlock(this)) {
			BlockPos retractPos = ExtendUtils.getRetractPos(worldIn, pos, hit.sideHit, worldIn.getBlockState(pos));
			if(retractPos != null) {
				ItemStack stack = getDrops(worldIn, retractPos, worldIn.getBlockState(retractPos), 0).get(0);
				worldIn.playAuxSFX(2001, pos, Block.getStateId(worldIn.getBlockState(retractPos)));
				worldIn.setBlockToAir(retractPos);
				playerIn.inventory.addItemStackToInventory(stack);
				if(stack.stackSize > 0) {
					BlockPos spawnPos = retractPos.offset(hit.sideHit);
					GeneralUtil.spawnItemStack(worldIn, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), stack);
				}
			}
		}
	}
	
	@Override
	public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
		return state.getValue(Const.MATERIAL) == EnumCatwalkMaterial.WOOD ? 0 : this.blockHardness;
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer playerIn, World worldIn, BlockPos pos) {
		if(playerIn.inventory.getCurrentItem() != null && playerIn.inventory.getCurrentItem().getItem() == ItemRegister.tool)
			return 1f;
		return super.getPlayerRelativeBlockHardness(state, playerIn, worldIn, pos);
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, Const.MATERIAL);
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
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
		if(Conf.showScaffoldInsideFaces)
			return super.shouldSideBeRendered(state, worldIn, pos, side);
        return worldIn.getBlockState(pos).getBlock() == this ? false : super.shouldSideBeRendered(state, worldIn, pos, side);
    }
}
