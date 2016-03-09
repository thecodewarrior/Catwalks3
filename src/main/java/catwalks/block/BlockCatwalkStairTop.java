package catwalks.block;

import java.util.List;

import catwalks.block.extended.EnumCubeEdge;
import catwalks.block.extended.TileExtended;
import catwalks.util.GeneralUtil;
import catwalks.util.WrenchChecker;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

public class BlockCatwalkStairTop extends BlockBase implements ICatwalkConnect {

	public BlockCatwalkStairTop() {
		super(Material.iron, "catwalkStairTop");
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
		
		IExtendedBlockState state = getBelowState(worldIn, pos);
		
		if(side != EnumFacing.UP ) {
			side = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(BlockCatwalkBase.FACING)), side);
			setSide(worldIn, pos, side, !hasSide(worldIn, pos, side));
			return true;
		}
		
		return true;
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer playerIn, World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock().getPlayerRelativeBlockHardness(playerIn, worldIn, pos.offset(EnumFacing.DOWN));
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		super.breakBlock(worldIn, pos, state);
		worldIn.setBlockState(pos.offset(EnumFacing.DOWN), Blocks.air.getDefaultState());
	}
	
	private IExtendedBlockState getBelowState(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos.offset(EnumFacing.DOWN));
		return (IExtendedBlockState) state.getBlock().getExtendedState(state, world, pos.offset(EnumFacing.DOWN));
	}
	
	@Override
	public boolean hasEdge(World world, BlockPos pos, EnumCubeEdge edge) {
		IExtendedBlockState state = getBelowState(world, pos);
		if(state.getValue(BlockCatwalkBase.FACING) == edge.getDir1()) {
			EnumFacing actualDir = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(BlockCatwalkBase.FACING)), edge.getDir2());
			if(actualDir == EnumFacing.EAST && state.getValue(BlockCatwalkStair.EAST_TOP)) {
				return true;
			}
			if(actualDir == EnumFacing.WEST && state.getValue(BlockCatwalkStair.WEST_TOP)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean hasSide(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = getBelowState(world, pos);
		EnumFacing actualDir = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(BlockCatwalkBase.FACING)), side);
		if(actualDir == EnumFacing.EAST && state.getValue(BlockCatwalkStair.EAST_TOP)) {
			return true;
		}
		if(actualDir == EnumFacing.WEST && state.getValue(BlockCatwalkStair.WEST_TOP)) {
			return true;
		}
		if(side == state.getValue(BlockCatwalkBase.FACING)) {
			return state.getValue(BlockCatwalkBase.NORTH);
		}
		return false;
	}
	
	@Override
	public void setSide(World world, BlockPos pos, EnumFacing side, boolean value) {
		IExtendedBlockState state = getBelowState(world, pos);
		TileExtended tile = (TileExtended) world.getTileEntity(pos.offset(EnumFacing.DOWN));
		
		EnumFacing actualDir = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(BlockCatwalkBase.FACING)), side);
		if(actualDir == EnumFacing.EAST && state.getValue(BlockCatwalkStair.EAST_TOP)) {
			tile.setBoolean(BlockCatwalkStair.I_EAST_TOP, value);
		}
		if(actualDir == EnumFacing.WEST && state.getValue(BlockCatwalkStair.WEST_TOP)) {
			tile.setBoolean(BlockCatwalkStair.I_WEST_TOP, value);
		}
		if(side == state.getValue(BlockCatwalkBase.FACING)) {
			tile.setBoolean(BlockCatwalkBase.I_NORTH, value);
		}
	}
	
	@Override
	public Object sideData(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = getBelowState(world, pos);
		return state.getValue(BlockCatwalkBase.FACING);
	}
	
	@Override
	public EnumSideType sideType(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = getBelowState(world, pos);
		if(side == state.getValue(BlockCatwalkBase.FACING)) {
			return EnumSideType.FULL;
		}
		if(side == state.getValue(BlockCatwalkBase.FACING).getOpposite()) {
			return null;
		}
		return EnumSideType.SLOPE_TOP;
	}
	
	{ /* non-solid stuffs */ }
	
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

	public int getRenderType() {
        return -1;
	}
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    { /* forwarding */ }
    
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
    }
	
}
