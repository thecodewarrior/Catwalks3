package catwalks.block.extended;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import catwalks.Const;
import catwalks.block.BlockCatwalkBase;
import catwalks.block.ICatwalkConnect;
import catwalks.item.ItemBlockCatwalk;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockCagedLadder extends BlockCatwalkBase {

	public BlockCagedLadder() {
		super(Material.iron, "cagedLadder", ItemBlockCatwalk.class);
		setHardness(1.5f);
	}

	@Override
	public boolean isLadder(IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}
	
	@Override
	public EnumFacing transformAffectedSide(World world, BlockPos pos, IBlockState state, EnumFacing side) {
		// I rotate here so that the side that's passed will be north if it's the 
		IExtendedBlockState estate = (IExtendedBlockState)getExtendedState(state, world, pos);
		return GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, estate.getValue(Const.FACING)), side);
	}
	
	@Override
	public void addAdditionalProperties(List<IUnlistedProperty> list) {
		list.add(Const.NORTH_LADDER_EXT);
		list.add(Const.SOUTH_LADDER_EXT);
		list.add(Const.EAST_LADDER_EXT);
		list.add(Const.WEST_LADDER_EXT);
		list.add(Const.NE_LADDER_EXT);
		list.add(Const.NW_LADDER_EXT);
		list.add(Const.SE_LADDER_EXT);
		list.add(Const.SW_LADDER_EXT);
		list.add(Const.IS_TOP);
	}

	@Override
	public IExtendedBlockState addProperties(TileExtended tile, IExtendedBlockState state) {
		boolean north = false, south = false, east = false, west = false, ne = false, nw = false, se = false, sw = false, istop = true;
		BlockPos pos = tile.getPos();
		World world = tile.getWorld();
		
		if(world.getBlockState(pos.offset(EnumFacing.UP)).getBlock() == this) {
			istop = false;
		}
		
		north = testExt(world, pos, EnumFacing.NORTH, state);
		south = testExt(world, pos, EnumFacing.SOUTH, state);
		east  = testExt(world, pos, EnumFacing.EAST, state);
		west  = testExt(world, pos, EnumFacing.WEST, state);
		
		ne = testCorner(world, pos, EnumFacing.NORTH, EnumFacing.EAST, state);
		nw = testCorner(world, pos, EnumFacing.NORTH, EnumFacing.WEST, state);
		se = testCorner(world, pos, EnumFacing.SOUTH, EnumFacing.EAST, state);
		sw = testCorner(world, pos, EnumFacing.SOUTH, EnumFacing.WEST, state);
		
		return state
				.withProperty(Const.NORTH_LADDER_EXT, north)
				.withProperty(Const.SOUTH_LADDER_EXT, south)
				.withProperty(Const.EAST_LADDER_EXT, east)
				.withProperty(Const.WEST_LADDER_EXT, west)
				.withProperty(Const.NE_LADDER_EXT, ne)
				.withProperty(Const.NW_LADDER_EXT, nw)
				.withProperty(Const.SE_LADDER_EXT, se)
				.withProperty(Const.SW_LADDER_EXT, sw)
				.withProperty(Const.IS_TOP, istop)
		;
	}
	
	public boolean testExt(World world, BlockPos pos, EnumFacing virtualSide, IExtendedBlockState ladderState) {
		EnumFacing facing = ladderState.getValue(Const.FACING);
		EnumFacing offsetSide = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, facing), virtualSide);
		
		if(!ladderState.getValue(Const.BOTTOM) || ladderState.getValue(Const.sideProperties.get(virtualSide)))
			return false;
		
		BlockPos next = pos.offset(offsetSide);
		BlockPos nextdown = next.offset(EnumFacing.DOWN);
		
		if(isTransp(world, next) && isSideSolid(world, nextdown, EnumFacing.UP)) {
			return true;
		}
		if(world.getBlockState(next).getBlock() instanceof ICatwalkConnect) {
			ICatwalkConnect icc = (ICatwalkConnect) world.getBlockState(next).getBlock();
			if(!icc.hasSide(world, next, offsetSide.getOpposite()) && icc.hasEdge(world, next, new CubeEdge(offsetSide.getOpposite(), EnumFacing.DOWN))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean testCorner(World world, BlockPos pos, EnumFacing virtualSide1, EnumFacing virtualSide2, IExtendedBlockState ladderState) {
		return ladderState.getValue(Const.sideProperties.get(virtualSide1)) != ladderState.getValue(Const.sideProperties.get(virtualSide2)) && (
				testCornerFromSide(world, pos, virtualSide1, virtualSide2, ladderState) || testCornerFromSide(world, pos, virtualSide2, virtualSide1, ladderState)
			);
	}
	
	public boolean testCornerFromSide(World world, BlockPos pos, EnumFacing virtualMainSide, EnumFacing virtualSecondarySide, IExtendedBlockState ladderState) {
		EnumFacing facing = ladderState.getValue(Const.FACING);
		EnumFacing offsetMain = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, facing), virtualMainSide);
		EnumFacing offsetSecondary = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, facing), virtualSecondarySide);
		
		BlockPos next = pos.offset(offsetMain);
		BlockPos side = pos.offset(offsetSecondary);
		BlockPos corner = pos.offset(offsetMain).offset(offsetSecondary);
		
		if(isTransp(world, next) && isSideSolid(world, corner, offsetSecondary.getOpposite())) {
			return true;
		}
		
		if(isTransp(world, next) && isSideSolid(world, side, offsetMain) && isSideSolid(world, side, offsetSecondary.getOpposite())) {
			return true;
		}
		
		if(world.getBlockState(next).getBlock() instanceof ICatwalkConnect) {
			ICatwalkConnect icc = (ICatwalkConnect) world.getBlockState(next).getBlock();
			if(icc.hasEdge(world, next, new CubeEdge(offsetMain.getOpposite(), offsetSecondary)))
				return true;
		}
		
		if(world.getBlockState(corner).getBlock() instanceof ICatwalkConnect) {
			ICatwalkConnect icc = (ICatwalkConnect) world.getBlockState(corner).getBlock();
			if(icc.hasEdge(world, corner, new CubeEdge(offsetMain.getOpposite(), offsetSecondary.getOpposite())))
				return true;
		}
		
		if(world.getBlockState(side).getBlock() instanceof ICatwalkConnect) {
			ICatwalkConnect icc = (ICatwalkConnect) world.getBlockState(side).getBlock();
			if(icc.hasEdge(world, side, new CubeEdge(offsetMain, offsetSecondary.getOpposite())))
				return true;
		}
		
		return false;
	}
	
	private boolean isTransp(World world, BlockPos pos) {
		return world.getBlockState(pos).getBlock().isFullBlock();
	}
	
	private boolean isSideSolid(World world, BlockPos pos, EnumFacing side) {
		return world.getBlockState(pos).getBlock().isSideSolid(world, pos, side);
	}
	
	{ /* ICatwalkConnect */ }
	
	@Override
	public boolean hasEdge(World world, BlockPos pos, CubeEdge edge) {
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		return state.getValue(sides.getA(edge.dir1)) || (edge.dir2.getAxis() != Axis.Y && state.getValue(sides.getA(edge.dir2)) );
	}
	
	@Override
	public boolean hasSide(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		return (side.getAxis() != Axis.Y && state.getValue(sides.getA(side)) );
	}
	
	@Override
	public void setSide(World world, BlockPos pos, EnumFacing side, boolean value) {
		if(side == EnumFacing.UP)
			return;
		TileExtended tile = (TileExtended) world.getTileEntity(pos);
		tile.setBoolean(sides.getC(side), value);
	}
	
	@Override
	public Object sideData(World world, BlockPos pos, EnumFacing side) {
		return null;
	}
	
	@Override
	public EnumSideType sideType(World world, BlockPos pos, EnumFacing side) {
		return EnumSideType.LADDER;
	}

	
	public Map<EnumFacing, List<CollisionBox>> collisionBoxes;

	@Override
	public void initColllisionBoxes() {
		Builder<EnumFacing, List<CollisionBox>> builder = ImmutableMap.<EnumFacing, List<CollisionBox>>builder();
		List<CollisionBox> boxes = new ArrayList<>();
		
        double thickness = Float.MIN_VALUE, p = 1/16f;
        
        Cuboid6 base = new Cuboid6(p,0,p , 1-p,1,1-p);
        Cuboid6 cuboid = new Cuboid6();
        
        // Bottom
        
        cuboid.set(base);
        cuboid.max.y = thickness;
        
        CollisionBox box = new CollisionBox();
        box.enableProperty = Const.BOTTOM;
        box.normal = box.sneak = cuboid.copy();
        
        boxes.add(box.copy());
        
        // North
        
        cuboid.set(base);
        cuboid.max.z = cuboid.min.z + thickness;
        
        box.enableProperty = Const.NORTH;
        box.normal = box.sneak = cuboid.copy();

        boxes.add(box.copy());
        
        // South
        
        cuboid.set(base);
        cuboid.min.z = cuboid.max.z - thickness;
        
        box.enableProperty = Const.SOUTH;
        box.normal = box.sneak = cuboid.copy();

        boxes.add(box.copy());
        
        // East
        
        cuboid.set(base);
        cuboid.min.x = cuboid.max.x - thickness;
        
        box.enableProperty = Const.EAST;
        box.normal = box.sneak = cuboid.copy();

        boxes.add(box.copy());
        
        // West
        
        cuboid.set(base);
        cuboid.max.x = cuboid.min.x + thickness;
        
        box.enableProperty = Const.WEST;
        box.normal = box.sneak = cuboid.copy();

        boxes.add(box.copy());
        
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
			
        	List<CollisionBox> turnedBoxes = new ArrayList<>();
        	int rot = GeneralUtil.getRotation(EnumFacing.NORTH, dir);
        	
        	for (CollisionBox rawBox : boxes) {
				CollisionBox turnedBox = rawBox.copy();
				GeneralUtil.rotateCuboidCenter(rot, turnedBox.normal);
				GeneralUtil.rotateCuboidCenter(rot, turnedBox.sneak);
				turnedBoxes.add(turnedBox);
			}
        	
        	builder.put(dir, turnedBoxes);
        }
        collisionBoxes = builder.build();
	}

	@Override
	public List<CollisionBox> getCollisionBoxes(IExtendedBlockState state, World world, BlockPos pos) {
		EnumFacing facing = state.getValue(Const.FACING);
		List<CollisionBox> list = collisionBoxes.get(facing);
		if(list == null) {
			Logs.warn("Tried to get collision boxes for invalid facing value! %s at (%d, %d, %d) in dim %s (%d)",
					facing.toString().toUpperCase(), pos.getX(), pos.getY(), pos.getZ(), world.provider.getDimensionName(), world.provider.getDimensionId());
			world.setBlockState(pos, Blocks.air.getDefaultState());
			Logs.warn("Removed invalid CatwalkStair block at (%d, %d, %d) in dim %s (%d)",
					pos.getX(), pos.getY(), pos.getZ(), world.provider.getDimensionName(), world.provider.getDimensionId());
			list = collisionBoxes.get(EnumFacing.NORTH);
		}
		return list;
	}

	private Map<EnumFacing, List<LookSide>> sideLookBoxes;

	@Override
	public void initSides() {
		sideLookBoxes = new HashMap<>();
		
		List<LookSide> sides = new ArrayList<>();
		
		double p = 1/16f, P = 1-p, m = 0.5;
		
		LookSide side = new LookSide();
		
		// Bottom
		side.mainSide = new Quad(
			new Vector3(p, 0, p),
			new Vector3(p, 0, P),
			new Vector3(P, 0, P),
			new Vector3(P, 0, p)
		);
		
		side.wrenchSide = new Quad(
			new Vector3(p, 0, p),
			new Vector3(p, 0, m),
			new Vector3(P, 0, m),
			new Vector3(P, 0, p)
		);
		
		side.showProperty = Const.BOTTOM;
		side.side = EnumFacing.DOWN;
		sides.add(side.copy());
		
		// North (ladder)
		side.mainSide = new Quad(
			new Vector3(p, 0, p),
			new Vector3(p, 1, p),
			new Vector3(P, 1, p),
			new Vector3(P, 0, p)
		);
		
		side.wrenchSide = new Quad(
			new Vector3(m-p, 0, p),
			new Vector3(m-p, 1, p),
			new Vector3(m+p, 1, p),
			new Vector3(m+p, 0, p)
		);
		
		side.showProperty = Const.NORTH;
		side.side = EnumFacing.NORTH;
		sides.add(side.copy());
		
		
		// South
		side.mainSide = new Quad(
			new Vector3(p, 0, P),
			new Vector3(p, 1, P),
			new Vector3(P, 1, P),
			new Vector3(P, 0, P)
		);
		
		side.wrenchSide = new Quad(
			new Vector3(m-p, 0, P),
			new Vector3(m-p, 1, P),
			new Vector3(m+p, 1, P),
			new Vector3(m+p, 0, P)
		);
		
		side.showProperty = Const.SOUTH;
		side.side = EnumFacing.SOUTH;
		sides.add(side.copy());
		
		// West
		side.mainSide = new Quad(
			new Vector3(p, 0, p),
			new Vector3(p, 1, p),
			new Vector3(p, 1, P),
			new Vector3(p, 0, P)
		);
		
		side.wrenchSide = new Quad(
			new Vector3(p, 0, p),
			new Vector3(p, 1, p),
			new Vector3(p, 1, m),
			new Vector3(p, 0, m)
		);
		
		side.showProperty = Const.WEST;
		side.side = EnumFacing.WEST;
		sides.add(side.copy());
		
		// East
		side.mainSide = new Quad(
			new Vector3(P, 0, p),
			new Vector3(P, 1, p),
			new Vector3(P, 1, P),
			new Vector3(P, 0, P)
		);
		
		side.wrenchSide = new Quad(
			new Vector3(P, 0, p),
			new Vector3(P, 1, p),
			new Vector3(P, 1, m),
			new Vector3(P, 0, m)
		);
		
		side.showProperty = Const.EAST;
		side.side = EnumFacing.EAST;
		sides.add(side.copy());
		
		// Bottom
		side.mainSide = new Quad(
			new Vector3(m-2*p, 1, m-2*p),
			new Vector3(m-2*p, 1, m+2*p),
			new Vector3(m+2*p, 1, m+2*p),
			new Vector3(m+2*p, 1, m-2*p)
		);
		
		side.wrenchSide = new Quad(
			new Vector3(0,0,0),
			new Vector3(0,0,0),
			new Vector3(0,0,0),
			new Vector3(0,0,0)
		);
		
		side.showProperty = Const.IS_TOP;
		side.side = EnumFacing.UP;
		sides.add(side.copy());
	
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
			
        	List<LookSide> turnedSides = new ArrayList<>();
        	int rot = GeneralUtil.getRotation(EnumFacing.NORTH, dir);
        	
        	for (LookSide rawSide : sides) {
        		LookSide turnedSide = rawSide.copy();
        		turnedSide.mainSide.rotateCenter(rot);
        		turnedSide.wrenchSide.rotateCenter(rot);
        		turnedSide.side = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, dir), turnedSide.side);
				turnedSides.add(turnedSide);
			}
        	
        	sideLookBoxes.put(dir, turnedSides);
		}
	}

	@Override
	public List<LookSide> lookSides(IExtendedBlockState state, World world, BlockPos pos) {
		return sideLookBoxes.get(state.getValue(Const.FACING));
	}
	
}
