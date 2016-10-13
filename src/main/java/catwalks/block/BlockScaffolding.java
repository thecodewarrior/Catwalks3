package catwalks.block;

import catwalks.Conf;
import catwalks.Const;
import catwalks.EnumCatwalkMaterial;
import catwalks.register.ItemRegister;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScaffolding extends BlockBase {
		
	public final int page;
	
	public BlockScaffolding(int page) {
		super(Material.IRON, "scaffold" + page);
		this.page = page;
		setHardness(0.1f);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(ItemRegister.scaffold, 1, state.getValue(Const.MATERIAL).ordinal());
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer playerIn, World worldIn, BlockPos pos) {
		
		if(playerIn.inventory.getCurrentItem() != null){
			Item item = playerIn.inventory.getCurrentItem().getItem();
			
			if( item == Item.getItemFromBlock(this) && playerIn.isSneaking()) {
				return super.getPlayerRelativeBlockHardness(state, playerIn, worldIn, pos);
			}
			
			if( item == ItemRegister.tool) {
				return 1;
			}
		}
//		if(state.getValue(Const.MATERIAL_META) == EnumCatwalkMaterial.WOOD) {
//			return 1;
//		}
		return super.getPlayerRelativeBlockHardness(state, playerIn, worldIn, pos);
	}
	
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
		if(this.harvesters.get() == null) {
			super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
			return;
		}
		
        if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
        {
            java.util.List<ItemStack> items = getDrops(worldIn, pos, state, fortune);
            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, fortune, chance, false, harvesters.get());

            for (ItemStack item : items)
            {
                if (worldIn.rand.nextFloat() <= chance)
                {
                    spawnAsEntityNoPickupDelayExactPos(worldIn, this.harvesters.get().getPositionVector().addVector(0, 0.25, 0), item);
                }
            }
        }
    }
	
	public static void spawnAsEntityNoPickupDelayExactPos(World worldIn, Vec3d pos, ItemStack stack)
    {
        if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
        {
            if (captureDrops.get())
            {
                capturedDrops.get().add(stack);
                return;
            }
            EntityItem entityitem = new EntityItem(worldIn, pos.xCoord, pos.yCoord, pos.zCoord, stack);
            entityitem.setPickupDelay(0);
            worldIn.spawnEntityInWorld(entityitem);
        }
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
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(Const.MATERIAL, EnumCatwalkMaterial.values()[page << 4 & meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(Const.MATERIAL).ordinal() & 0b1111;
	}
	
	{ /* rendering stuff */ }
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
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
        return worldIn.getBlockState(pos.offset(side)).getBlock() == this ? false : super.shouldSideBeRendered(state, worldIn, pos, side);
    }

	{ /* ladder */ }
	
//	@Override
//	public boolean shouldApplyClimbing(World world, BlockPos pos, EntityLivingBase entity) {
//		return ( entity.moveForward != 0 || entity.moveStrafing != 0 );// && world.getBlockState(pos).getValue(Const.MATERIAL_META) == EnumCatwalkMaterial.WOOD;
//	}
//
//	@Override
//	public boolean shouldApplyFalling(World world, BlockPos pos, EntityLivingBase entity) {
//		return true;//world.getBlockState(pos).getValue(Const.MATERIAL_META) == EnumCatwalkMaterial.WOOD;
//	}
//
//	@Override
//	public double climbSpeed(World world, BlockPos pos, EntityLivingBase entity) {
//		return 1;
//	}
//
//	@Override
//	public double fallSpeed(World world, BlockPos pos, EntityLivingBase entity) {
//		return 1;
//	}
//
//	@Override
//	public double horizontalSpeed(World world, BlockPos pos, EntityLivingBase entity) {
//		return Double.POSITIVE_INFINITY;
//	}
}
