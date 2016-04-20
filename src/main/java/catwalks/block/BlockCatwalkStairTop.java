package catwalks.block;

import java.util.List;
import java.util.Random;

import catwalks.Const;
import catwalks.block.extended.CubeEdge;
import catwalks.block.extended.TileExtended;
import catwalks.register.BlockRegister;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import catwalks.util.WrenchChecker;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockCatwalkStairTop extends BlockBase implements ICatwalkConnect, IDecoratable {

	public BlockCatwalkStairTop() {
		super(Material.iron, "catwalkStairTop");
		this.setTickRandomly(true);
		setDefaultState(this.blockState.getBaseState().withProperty(Const.MATERIAL, EnumCatwalkMaterial.STEEL));
	}
	
	public void initPreRegister() {
		setCreativeTab(null);
	};
	
	{ /* state stuffz */ }
	
	@Override
	protected BlockState createBlockState() {
	    return new ExtendedBlockState(this,
	    		new IProperty[]{ Const.MATERIAL, Const.LIGHTS },
	    		new IUnlistedProperty[]{ Const.FACING, Const.TAPE, Const.SPEED, Const.NORTH, Const.WEST_TOP, Const.EAST_TOP}
	    );
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
	    return this.getDefaultState().withProperty(Const.MATERIAL, EnumCatwalkMaterial.values()[ meta & 0b0111 ]).withProperty(Const.LIGHTS, ( meta & 0b1000 ) == 1 );
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
	    return state.getValue(Const.MATERIAL).ordinal() | ( state.getValue(Const.LIGHTS) ? 8 : 0 );
	}
	
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		IBlockState below = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
		boolean westTop = false, eastTop = false, north = false, tape = false, speed = false, lights = false;
		EnumFacing facing = EnumFacing.NORTH;
		if(below.getBlock() == BlockRegister.catwalkStair) {
			IExtendedBlockState ebelow = null;
			try {
				ebelow = (IExtendedBlockState)below.getBlock().getExtendedState(below, worldIn, pos.offset(EnumFacing.DOWN));
			
				if(ebelow != null) {
					  facing = ebelow.getValue(Const.FACING);
					  
					   north = ebelow.getValue(Const.NORTH);
					 westTop = ebelow.getValue(Const.WEST_TOP);
					 eastTop = ebelow.getValue(Const.EAST_TOP);
					 
					    tape = ebelow.getValue(Const.TAPE);
					   speed = ebelow.getValue(Const.SPEED);
				}
			} catch(NullPointerException e) {
				Logs.error(e, "Edge case NPE, likely a freak race condition... *shrugs*");
			}
			lights = below.getValue(Const.LIGHTS);
		}
		
		IExtendedBlockState estate = ((IExtendedBlockState)state
				.withProperty(Const.LIGHTS, lights))
				.withProperty(Const.WEST_TOP, westTop)
				.withProperty(Const.EAST_TOP, eastTop)
				.withProperty(Const.NORTH, north)
				.withProperty(Const.TAPE, tape)
				.withProperty(Const.SPEED, speed)
				.withProperty(Const.FACING, facing);
		
		return estate;
	}
	
	private IExtendedBlockState getBelowState(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos.offset(EnumFacing.DOWN));
		return (IExtendedBlockState) state.getBlock().getExtendedState(state, world, pos.offset(EnumFacing.DOWN));
	}

	{ /* super special stair-top stuffs */ }
	
	public boolean checkForValidity(World worldIn, BlockPos pos) {
		if(worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() != BlockRegister.catwalkStair) {
			worldIn.setBlockState(pos, Blocks.air.getDefaultState());
			Logs.warn("Removed invalid CatwalkStairTop block at (%d, %d, %d) in dim %s (%d)", pos.getX(), pos.getY(), pos.getZ(), worldIn.provider.getDimensionName(), worldIn.provider.getDimensionId());
			return false;
		}
		return true;
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
		checkForValidity(worldIn, pos);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		checkForValidity(worldIn, pos);
	}
	
	

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState ourState, EntityPlayer playerIn,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if( playerIn.inventory.getCurrentItem() != null) {
			if(!WrenchChecker.isAWrench( playerIn.inventory.getCurrentItem().getItem() ))
				return false;
			if(playerIn.inventory.getCurrentItem().getItem() instanceof ItemBlock)
				return false;
		} else {
			return false;
		}
		
		if(side != EnumFacing.UP ) {
//			side = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(BlockCatwalkBase.FACING)), side);
			boolean hasSide = hasSide(worldIn, pos, side);
			setSide(worldIn, pos, side, !hasSide);
			return true;
		}
		
		return true;
	}
	
	{ /* forwarding */ }
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		IBlockState state = world.getBlockState(pos.offset(EnumFacing.DOWN));
		return state.getBlock().getPickBlock(target, world, pos.offset(EnumFacing.DOWN), player);
	}
	
	@Override
	public boolean putDecoration(World world, BlockPos pos, String name, boolean value) {
		pos = pos.offset(EnumFacing.DOWN);
		return ( (IDecoratable)world.getBlockState(pos).getBlock() ).putDecoration(world, pos, name, value);
	}
	
	@Override
	public boolean hasDecoration(World world, BlockPos pos, String name) {
		pos = pos.offset(EnumFacing.DOWN);
		return ( (IDecoratable)world.getBlockState(pos).getBlock() ).hasDecoration(world, pos, name);
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer playerIn, World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock().getPlayerRelativeBlockHardness(playerIn, worldIn, pos.offset(EnumFacing.DOWN));
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, EntityPlayer player, Vec3 start,
			Vec3 end) {
		IBlockState state = world.getBlockState(pos.offset(EnumFacing.DOWN));
		
		if(state.getBlock() instanceof BlockBase) {
			MovingObjectPosition mop = state.getBlock().collisionRayTrace(world, pos.offset(EnumFacing.DOWN), start, end);
			return mop;
		}
		return null;
	}

	@Override
	public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		IBlockState below = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
		
		below.getBlock().addCollisionBoxesToList(worldIn, pos.offset(EnumFacing.DOWN), below, mask, list, collidingEntity);
	}

	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		int meta = state.getValue(Const.MATERIAL).ordinal();
		GeneralUtil.spawnItemStack(worldIn, pos.getX()+0.5, pos.getY()-0.5, pos.getZ()+0.5, new ItemStack(BlockRegister.catwalkStair, 1, meta));
	}
	
	{ /* super-special multiblock magic */ }

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		
//		IBlockState iblockstate = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
//        Block block = iblockstate.getBlock();
//
//        if (block.getMaterial() != Material.air){
//            block.dropBlockAsItem(worldIn, pos.offset(EnumFacing.DOWN), iblockstate, 0);

            worldIn.setBlockState(pos.offset(EnumFacing.DOWN), Blocks.air.getDefaultState());
//        }
        
		GeneralUtil.updateSurroundingCatwalkBlocks(worldIn, pos);
	}
	
	{ /* ICatwalkConnect */ }
	
	@Override
	public boolean hasEdge(World world, BlockPos pos, CubeEdge edge) {
		if(!checkForValidity(world, pos))
			return false;
		if(edge.dir1 == EnumFacing.UP || edge.dir2 == EnumFacing.UP) {
			return false;
		}
		
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		
		if(GeneralUtil.checkEdge(EnumFacing.DOWN, state.getValue(Const.FACING), edge) && !state.getValue(Const.NORTH))
			return true;
		if(GeneralUtil.checkEdge(EnumFacing.UP, state.getValue(Const.FACING), edge) && state.getValue(Const.NORTH))
			return true;
		
		if(edge.dir1 == state.getValue(Const.FACING).getOpposite() || edge.dir2 == state.getValue(Const.FACING).getOpposite()) {
			return false;
		}
		
		return ICatwalkConnect.super.hasEdge(world, pos, edge);
	}
	
	@Override
	public EnumEdgeType edgeType(World world, BlockPos pos, CubeEdge edge) {
		return EnumEdgeType.FULL;
	}
	
	@Override
	public boolean hasSide(World world, BlockPos pos, EnumFacing side) {
		if(!checkForValidity(world, pos))
			return false;
		IExtendedBlockState state = getBelowState(world, pos);
		
		EnumFacing actualDir = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(Const.FACING)), side);
		if(actualDir == EnumFacing.EAST && state.getValue(Const.EAST_TOP)) {
			return true;
		}
		if(actualDir == EnumFacing.WEST && state.getValue(Const.WEST_TOP)) {
			return true;
		}
		if(side == state.getValue(Const.FACING)) {
			return state.getValue(Const.NORTH);
		}
		return false;
	}
	
	@Override
	public void setSide(World world, BlockPos pos, EnumFacing side, boolean value) {
		if(!checkForValidity(world, pos))
			return;
		IExtendedBlockState state = getBelowState(world, pos);
		
		TileExtended tile = (TileExtended) world.getTileEntity(pos.offset(EnumFacing.DOWN));
		
		EnumFacing actualDir = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(Const.FACING)), side);
		if(actualDir == EnumFacing.EAST) {
			tile.setBoolean(BlockCatwalkStair.I_EAST_TOP, value);
		}
		if(actualDir == EnumFacing.WEST) {
			tile.setBoolean(BlockCatwalkStair.I_WEST_TOP, value);
		}
		if(side == state.getValue(Const.FACING)) {
			tile.setBoolean(BlockCatwalkBase.I_NORTH, value);
		}
	}
	
	@Override
	public Object sideData(World world, BlockPos pos, EnumFacing side) {
		if(!checkForValidity(world, pos))
			return null;
		IExtendedBlockState state = getBelowState(world, pos);
		if(side.getAxis() != state.getValue(Const.FACING).getAxis())
			return state.getValue(Const.FACING);
		return null;
	}
	
	@Override
	public EnumSideType sideType(World world, BlockPos pos, EnumFacing side) {
		if(!checkForValidity(world, pos))
			return EnumSideType.FULL;
		IExtendedBlockState state = getBelowState(world, pos);
		
		if(side == state.getValue(Const.FACING)) {
			return EnumSideType.FULL;
		}
		if(side == state.getValue(Const.FACING).getOpposite()) {
			return null;
		}
		return EnumSideType.SLOPE_TOP;
	}
	
	{ /* plain old boring block stuff */ }
	
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}
	
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT_MIPPED;
	}
	
}
