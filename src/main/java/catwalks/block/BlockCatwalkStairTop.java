package catwalks.block;

import java.util.List;
import java.util.Random;

import catwalks.block.extended.BlockExtended;
import catwalks.block.extended.tileprops.ArrayProp;
import catwalks.block.extended.tileprops.BoolProp;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import catwalks.Const;
import catwalks.block.extended.CubeEdge;
import catwalks.block.extended.tileprops.TileExtended;
import catwalks.register.BlockRegister;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import catwalks.util.WrenchChecker;

import static catwalks.Const.EAST_TOP;

public class BlockCatwalkStairTop extends BlockExtended implements ICatwalkConnect, IDecoratable {

	public ArrayProp<EnumCatwalkMaterial> MATERIAL;
	
	public BlockCatwalkStairTop() {
		super(Material.IRON, "catwalkStairTop");
		this.setTickRandomly(true);
		setDefaultState(this.blockState.getBaseState());
		
		MATERIAL = allocator.allocateArray(EnumCatwalkMaterial.values(), 16);
	}
	
	
	
	public void initPreRegister() {
		setCreativeTab(null);
	};
	
	{ /* state stuffz */ }
	
	@Override
	protected BlockStateContainer createBlockState() {
	    return new ExtendedBlockState(this,
	    		new IProperty[]{ Const.LIGHTS},
	    		new IUnlistedProperty[] { Const.MATERIAL, Const.FACING, Const.TAPE, Const.SPEED, Const.NORTH, Const.WEST_TOP, EAST_TOP }
	    );
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
	    return this.getDefaultState().withProperty(Const.LIGHTS, ( meta & 0b0001 ) == 1 );
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
	    return state.getValue(Const.LIGHTS) ? 1 : 0;
	}
	
	@Override
	public IExtendedBlockState getExtendedState(IBlockState rawstate, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState)rawstate;
		boolean westTop = false, eastTop = false, north = false, tape = false, speed = false, lights = false;
		EnumFacing facing = EnumFacing.NORTH;
		
		if(worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == BlockRegister.catwalkStair) {
			IExtendedBlockState below = getBelowState(worldIn, pos);
			 facing = below.getValue(Const.FACING);
			 
			  north = below.getValue(Const.NORTH);
			westTop = below.getValue(Const.WEST_TOP);
			eastTop = below.getValue(EAST_TOP);
			
			   tape = below.getValue(Const.TAPE);
			  speed = below.getValue(Const.SPEED);
			 lights = below.getValue(Const.LIGHTS);
		}
		
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		
		return (IExtendedBlockState) state
				.withProperty(Const.MATERIAL, MATERIAL.get(tile))
				.withProperty(Const.WEST_TOP, westTop)
				.withProperty(EAST_TOP, eastTop)
				.withProperty(Const.NORTH, north)
				.withProperty(Const.TAPE, tape)
				.withProperty(Const.SPEED, speed)
				.withProperty(Const.FACING, facing)
				.withProperty(Const.LIGHTS, lights);
	}
	
	private IExtendedBlockState getBelowState(IBlockAccess world, BlockPos pos) {
		return GeneralUtil.getTileState(world, pos.offset(EnumFacing.DOWN));
	}

	{ /* super special stair-top stuffs */ }
	
	public boolean checkForValidity(World worldIn, BlockPos pos) {
		if(worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() != BlockRegister.catwalkStair) {
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			Logs.warn("Removed invalid CatwalkStairTop block at %s", GeneralUtil.getWorldPosLogInfo(worldIn, pos));
			return false;
		}
		return true;
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
		checkForValidity(worldIn, pos);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		checkForValidity(worldIn, pos);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if( heldItem != null && WrenchChecker.isAWrench( heldItem.getItem() )) {
			if(side != EnumFacing.UP ) {
				boolean hasSide = hasSide(worldIn, pos, side);
				setSide(worldIn, pos, side, !hasSide);
				return true;
			}
		}
		
		return false;
	}
	
	{ /* forwarding */ }
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		IBlockState statebelow = world.getBlockState(pos.offset(EnumFacing.DOWN));
		return state.getBlock().getPickBlock(statebelow, target, world, pos.offset(EnumFacing.DOWN), player);
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
	public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer playerIn, World worldIn, BlockPos pos) {
		IBlockState statebelow = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
		return statebelow.getBlock().getPlayerRelativeBlockHardness(statebelow, playerIn, worldIn, pos.offset(EnumFacing.DOWN));
	}
	
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start,
			Vec3d end) {
		IBlockState statebelow = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
		
		if(statebelow.getBlock() instanceof BlockBase) {
			RayTraceResult mop = statebelow.getBlock().collisionRayTrace(statebelow, worldIn, pos.offset(EnumFacing.DOWN), start, end);
			return mop;
		}
		return null;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn) {
		IBlockState below = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
		
		below.getBlock().addCollisionBoxToList(below, worldIn, pos.offset(EnumFacing.DOWN), entityBox, collidingBoxes, entityIn);
	}

	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		int meta = MATERIAL.get(tile).ordinal();
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

            worldIn.setBlockState(pos.offset(EnumFacing.DOWN), Blocks.AIR.getDefaultState());
//        }
        
		GeneralUtil.updateSurroundingCatwalkBlocks(worldIn, pos);
	}
	
	{ /* ICatwalkConnect */ }
	
	@Override
	public boolean hasEdge(World world, BlockPos pos, CubeEdge edge) {
		if(!checkForValidity(world, pos))
			return false;
		
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		
		if(GeneralUtil.checkEdge(EnumFacing.DOWN, state.getValue(Const.FACING), edge) && !state.getValue(Const.NORTH))
			return true;
		if(GeneralUtil.checkEdge(EnumFacing.UP, state.getValue(Const.FACING), edge) && state.getValue(Const.NORTH))
			return true;
		
		if(edge.dir1 == EnumFacing.UP || edge.dir2 == EnumFacing.UP) {
			return false;
		}
		
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
		if(actualDir == EnumFacing.EAST && state.getValue(EAST_TOP)) {
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
		BlockCatwalkStair block = (BlockCatwalkStair) state.getBlock();
		TileExtended tile = (TileExtended) world.getTileEntity(pos.offset(EnumFacing.DOWN));
		
		EnumFacing actualDir = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(Const.FACING)), side);
		if(actualDir == EnumFacing.EAST) {
			block.EAST_TOP.set(tile, value);
		}
		if(actualDir == EnumFacing.WEST) {
			block.WEST_TOP.set(tile, value);
		}
		if(side == state.getValue(Const.FACING)) {
			block.NORTH.set(tile, value);
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
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isVisuallyOpaque() {
		return false;
	}
	
	@Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
	
}
