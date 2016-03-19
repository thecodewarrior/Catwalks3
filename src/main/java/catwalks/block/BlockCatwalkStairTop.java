package catwalks.block;

import java.util.List;
import java.util.Random;

import catwalks.block.BlockCatwalkBase.EnumCatwalkMaterial;
import catwalks.block.extended.EnumCubeEdge;
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
		setDefaultState(this.blockState.getBaseState().withProperty(BlockCatwalkBase.MATERIAL, EnumCatwalkMaterial.STEEL));
	}
	
	public void initPreRegister() {
		setCreativeTab(null);
	};
	
	{ /* state stuffz */ }
	
	@Override
	protected BlockState createBlockState() {
	    return new ExtendedBlockState(this,
	    		new IProperty[]{ BlockCatwalkBase.MATERIAL },
	    		new IUnlistedProperty[]{ BlockCatwalkBase.FACING, BlockCatwalkBase.TAPE, BlockCatwalkBase.LIGHTS, BlockCatwalkBase.SPEED, BlockCatwalkBase.NORTH, BlockCatwalkStair.WEST_TOP, BlockCatwalkStair.EAST_TOP}
	    );
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
	    return this.getDefaultState().withProperty(BlockCatwalkBase.MATERIAL, EnumCatwalkMaterial.values()[ meta ]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
	    return state.getValue(BlockCatwalkBase.MATERIAL).ordinal();
	}
	
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		IBlockState below = worldIn.getBlockState(pos.offset(EnumFacing.DOWN));
		boolean westTop = false, eastTop = false, north = false, lights = false, tape = false, speed = false;
		EnumFacing facing = EnumFacing.NORTH;
		if(below.getBlock() == BlockRegister.catwalkStair) {
			IExtendedBlockState ebelow = null;
			try {
				ebelow = (IExtendedBlockState)below.getBlock().getExtendedState(below, worldIn, pos.offset(EnumFacing.DOWN));
			
				if(ebelow != null) {
					  facing = ebelow.getValue(BlockCatwalkBase.FACING);
					  
					   north = ebelow.getValue(BlockCatwalkBase.NORTH);
					 westTop = ebelow.getValue(BlockCatwalkStair.WEST_TOP);
					 eastTop = ebelow.getValue(BlockCatwalkStair.EAST_TOP);
					 
					    tape = ebelow.getValue(BlockCatwalkBase.TAPE);
					  lights = ebelow.getValue(BlockCatwalkBase.LIGHTS);
					   speed = ebelow.getValue(BlockCatwalkBase.SPEED);
				}
			} catch(NullPointerException e) {
				Logs.error(e, "Edge case NPE, likely a freak race condition... *shrugs*");
			}
		}
		
		IExtendedBlockState estate = ((IExtendedBlockState)state)
				.withProperty(BlockCatwalkStair.WEST_TOP, westTop)
				.withProperty(BlockCatwalkStair.EAST_TOP, eastTop)
				.withProperty(BlockCatwalkBase.NORTH, north)
				.withProperty(BlockCatwalkBase.TAPE, tape)
				.withProperty(BlockCatwalkBase.LIGHTS, lights)
				.withProperty(BlockCatwalkBase.SPEED, speed)
				.withProperty(BlockCatwalkBase.FACING, facing);
		
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
		int meta = state.getValue(BlockCatwalkBase.MATERIAL).ordinal();
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
		if(actualDir == EnumFacing.EAST) {
			tile.setBoolean(BlockCatwalkStair.I_EAST_TOP, value);
		}
		if(actualDir == EnumFacing.WEST) {
			tile.setBoolean(BlockCatwalkStair.I_WEST_TOP, value);
		}
		if(side == state.getValue(BlockCatwalkBase.FACING)) {
			tile.setBoolean(BlockCatwalkBase.I_NORTH, value);
		}
	}
	
	@Override
	public Object sideData(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = getBelowState(world, pos);
		if(side.getAxis() != state.getValue(BlockCatwalkBase.FACING).getAxis())
			return state.getValue(BlockCatwalkBase.FACING);
		return null;
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
