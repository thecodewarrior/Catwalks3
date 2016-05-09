package catwalks.block.extended;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import catwalks.Conf;
import catwalks.Const;
import catwalks.block.BlockCatwalkBase;
import catwalks.block.ICatwalkConnect;
import catwalks.block.property.UPropertyBool;
import catwalks.item.ItemBlockCatwalk;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockCagedLadder extends BlockCatwalkBase implements ICustomLadder {

	public BlockCagedLadder() {
		super(Material.IRON, "cagedLadder", (c) -> new ItemBlockCatwalk(c));
		setHardness(1.5f);
	}
	
	@Override
	public double climbSpeed(World world, BlockPos pos, EntityLivingBase entity) {
		IExtendedBlockState state = GeneralUtil.getTileState(world, pos);
		
		return state.getValue(Const.SPEED) ? Conf.ladderSpeed : 1;
	}

	@Override
	public double fallSpeed(World world, BlockPos pos, EntityLivingBase entity) {
		IExtendedBlockState state = GeneralUtil.getTileState(world, pos);

		return state.getValue(Const.SPEED) ? Conf.ladderSpeed : 1;
	}
	
	@Override
	public boolean shouldApplyFalling(World world, BlockPos pos, EntityLivingBase entity) {
		return  Math.floor(entity.posX) == pos.getX() &&
				Math.floor(entity.posY) == pos.getY() &&
				Math.floor(entity.posZ) == pos.getZ();
	}

	@Override
	public boolean shouldApplyClimbing(World world, BlockPos pos, EntityLivingBase entity) {
		
		if(entity.moveForward == 0 && entity.moveStrafing == 0)
			return false;
		
		AxisAlignedBB playerAABB = entity.getEntityBoundingBox();
		float p = 1/16f, P = 1-p;
		AxisAlignedBB blockAABB  = new AxisAlignedBB(p, 0, p, P, 1, P)
				.offset(pos.getX(), pos.getY(), pos.getZ());
		
		if(	!playerAABB.intersectsWith(blockAABB)) // if the player isn't even in the block entirely, don't bother with all the complex slow stuff
			return false;
		
		IExtendedBlockState state = GeneralUtil.getTileState(world, pos);
		
		int rot = GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(Const.FACING));
		UPropertyBool northProp = Const.sideProperties.get(GeneralUtil.derotateFacing(rot, EnumFacing.NORTH));
		UPropertyBool southProp = Const.sideProperties.get(GeneralUtil.derotateFacing(rot, EnumFacing.SOUTH));
		UPropertyBool  eastProp = Const.sideProperties.get(GeneralUtil.derotateFacing(rot, EnumFacing.EAST ));
		UPropertyBool  westProp = Const.sideProperties.get(GeneralUtil.derotateFacing(rot, EnumFacing.WEST ));
		
		
		boolean // check which sides are equal, meaning that the player is pusing up against that side, and we might should apply our values based on that
			north = GeneralUtil.approxEq(playerAABB.minZ, blockAABB.minZ),
			south = GeneralUtil.approxEq(playerAABB.maxZ, blockAABB.maxZ),
			east  = GeneralUtil.approxEq(playerAABB.maxX, blockAABB.maxX),
			west  = GeneralUtil.approxEq(playerAABB.minX, blockAABB.minX);
		
		if(!state.getValue(Const.IS_BOTTOM)) { // if there is a ladder below us
			BlockPos downPos = pos.offset(EnumFacing.DOWN);
			IExtendedBlockState below = GeneralUtil.getTileState(world, downPos);
			
			// for each side, if this is the bottom side before an opening, don't consider it so the player can get out without climbing back up
			if(north && state.getValue(northProp) && !below.getValue(northProp))
				north = false;
			if(south && state.getValue(southProp) && !below.getValue(southProp))
				south = false;
			if(east  && state.getValue(eastProp ) && !below.getValue(eastProp ))
				east  = false;
			if(west  && state.getValue(westProp ) && !below.getValue(westProp ))
				west  = false;
		}
		
		// for each side that should still be considered, if the side isn't open the player is climbing some other object and we shouldn't consider it
		if(north && !state.getValue(northProp))
			north = false;
		if(south && !state.getValue(southProp))
			south = false;
		if(east  && !state.getValue(eastProp ))
			east  = false;
		if(west  && !state.getValue(westProp ))
			west  = false;
		
		Vec3d desiredMoveVec = GeneralUtil.getDesiredMoveVector(entity);
		double deadAngle = 10; // the player has to be pushing on the side by more than 10º in order for the ladder to effect the player
		
		if(north && isSidePushingInDeadZone(desiredMoveVec, EnumFacing.NORTH, deadAngle))
			north = false;
		if(south && isSidePushingInDeadZone(desiredMoveVec, EnumFacing.SOUTH, deadAngle))
			south = false;
		if(east && isSidePushingInDeadZone(desiredMoveVec, EnumFacing.EAST, deadAngle))
			east = false;
		if(west && isSidePushingInDeadZone(desiredMoveVec, EnumFacing.WEST, deadAngle))
			west = false;
		
		if( !(north || south || east || west) ) // if we shouldn't consider any side, don't consider this block
			return false;
		
		return true;
	}
	
	public static boolean isSidePushingInDeadZone(Vec3d desiredMoveVec, EnumFacing side, double deadAngle) {
		Vec3d inwardVec = new Vec3d(-side.getFrontOffsetX(), 0, -side.getFrontOffsetZ());
		// minAngle is from 0 (directly away from face) to 90 (parallel to face) to 180 (directly into the face)
		double minAngle = Math.toDegrees( Math.acos(inwardVec.dotProduct(desiredMoveVec) / (inwardVec.lengthVector() * desiredMoveVec.lengthVector())) );
		
		if(minAngle > 90 && minAngle < 90+deadAngle)
			return true;
		return false;
	}
	
	@Override
	public double horizontalSpeed(World world, BlockPos pos, EntityLivingBase entity) {
		return 1;
	}
	

	@Override
	public boolean canGiveSpeedBoost(World world, BlockPos pos) {
		return false;
	}
	
	
//	@Override
//	public boolean isLadder(IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
//		return true;
//	}
	
	@Override
	public EnumFacing transformAffectedSide(World world, BlockPos pos, IBlockState state, EnumFacing side) {
		// I rotate here so the actual side is turned into the "virtual" side
		IExtendedBlockState estate = getTileState(state, world, pos);
		return GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, estate.getValue(Const.FACING)), side);
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addFunctionalProperties(List<IUnlistedProperty> list) {
		list.add(Const.IS_TOP);
		list.add(Const.IS_BOTTOM);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addRenderOnlyProperties(List<IUnlistedProperty> list) {
		list.add(Const.NORTH_LADDER_EXT);
		list.add(Const.SOUTH_LADDER_EXT);
		list.add(Const.EAST_LADDER_EXT);
		list.add(Const.WEST_LADDER_EXT);
		
		list.add(Const.NORTH_LADDER_EXT_TOP);
		list.add(Const.SOUTH_LADDER_EXT_TOP);
		list.add(Const.EAST_LADDER_EXT_TOP);
		list.add(Const.WEST_LADDER_EXT_TOP);
		
		list.add(Const.NE_LADDER_EXT);
		list.add(Const.NW_LADDER_EXT);
		list.add(Const.SE_LADDER_EXT);
		list.add(Const.SW_LADDER_EXT);
	}

	@Override
	public IExtendedBlockState setFunctionalProperties(TileExtended tile, IExtendedBlockState state) {
		BlockPos pos = tile.getPos();
		World world = tile.getWorld();
		
		return state
			.withProperty(Const.IS_TOP,    world.getBlockState(pos.offset(EnumFacing.UP  )).getBlock() != this)
			.withProperty(Const.IS_BOTTOM, world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() != this);
	}
	
	@Override
	public IExtendedBlockState setRenderProperties(TileExtended tile, IExtendedBlockState state) {
		boolean north = false, south = false, east = false, west = false;
		boolean north_top = false, south_top = false, east_top = false, west_top = false;
		boolean ne = false, nw = false, se = false, sw = false;
		
		BlockPos pos = tile.getPos();
		World world = tile.getWorld();
		
		north = testExt(world, pos, EnumFacing.NORTH, state);
		south = testExt(world, pos, EnumFacing.SOUTH, state);
		east  = testExt(world, pos, EnumFacing.EAST, state);
		west  = testExt(world, pos, EnumFacing.WEST, state);
		
		if(state.getValue(Const.IS_TOP)) {
			north_top = testExtTop(world, pos, EnumFacing.NORTH, state);
			south_top = testExtTop(world, pos, EnumFacing.SOUTH, state);
			east_top  = testExtTop(world, pos, EnumFacing.EAST, state);
			west_top  = testExtTop(world, pos, EnumFacing.WEST, state);
		}
	
		ne = testCorner(world, pos, EnumFacing.NORTH, EnumFacing.EAST, state);
		nw = testCorner(world, pos, EnumFacing.NORTH, EnumFacing.WEST, state);
		se = testCorner(world, pos, EnumFacing.SOUTH, EnumFacing.EAST, state);
		sw = testCorner(world, pos, EnumFacing.SOUTH, EnumFacing.WEST, state);
		
		return state
				.withProperty(Const.NORTH_LADDER_EXT, north)
				.withProperty(Const.SOUTH_LADDER_EXT, south)
				.withProperty(Const.EAST_LADDER_EXT, east)
				.withProperty(Const.WEST_LADDER_EXT, west)
				.withProperty(Const.NORTH_LADDER_EXT_TOP, north_top)
				.withProperty(Const.SOUTH_LADDER_EXT_TOP, south_top)
				.withProperty(Const.EAST_LADDER_EXT_TOP, east_top)
				.withProperty(Const.WEST_LADDER_EXT_TOP, west_top)
				.withProperty(Const.NE_LADDER_EXT, ne)
				.withProperty(Const.NW_LADDER_EXT, nw)
				.withProperty(Const.SE_LADDER_EXT, se)
				.withProperty(Const.SW_LADDER_EXT, sw)
		;
	}
	
	public boolean testExt(World world, BlockPos pos, EnumFacing virtualSide, IExtendedBlockState ladderState) {
		EnumFacing facing = ladderState.getValue(Const.FACING);
		EnumFacing offsetSide = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, facing), virtualSide);
		
		BlockPos down = pos.offset(EnumFacing.DOWN);
		BlockPos next = pos.offset(offsetSide);
		BlockPos nextdown = next.offset(EnumFacing.DOWN);
		
		if(ladderState.getValue(Const.sideProperties.get(virtualSide)) ) {
			if(world.getBlockState(down).getBlock() instanceof ICatwalkConnect && world.getBlockState(down).getBlock() != this) {
					// if we have the side and there's a connectable block below us and...
				ICatwalkConnect icc = (ICatwalkConnect) world.getBlockState(down).getBlock();
				CubeEdge edge = new CubeEdge(offsetSide, EnumFacing.UP);
				if( icc.edgeType(world, down, edge) == EnumEdgeType.FULL && // if there is a full edge
					icc.hasEdge(world, down, edge) && // and it exists (duh)
					!icc.hasSide(world, down, EnumFacing.UP) // and there isn't a top to block it
				  ) {
					return true;
				}
			}
			return false;
		}
		
		if(!ladderState.getValue(Const.BOTTOM)) {
			
			if(world.getBlockState(down).getBlock() == this) {
				BlockPos below = pos.offset(EnumFacing.DOWN);
				IExtendedBlockState state = GeneralUtil.getTileState(world, below);
				
				EnumFacing belowFacing = state.getValue(Const.FACING);
				EnumFacing belowVirtualSide = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, belowFacing), offsetSide);
				
				if(!state.getValue(Const.sideProperties.get(belowVirtualSide)))
					return false; // if we don't have a bottom and the ladder below us doesn't have a supporting side
			} else {
				return false; // we don't have a bottom and there isn't a ladder below us.
			}
		}
		
		if(isTransp(world, next) && isSideSolid(world, nextdown, EnumFacing.UP)) {
			return true;
		}
		
		if(world.getBlockState(next).getBlock() instanceof ICatwalkConnect) {
			ICatwalkConnect icc = (ICatwalkConnect) world.getBlockState(next).getBlock();
			CubeEdge edge = new CubeEdge(offsetSide.getOpposite(), EnumFacing.DOWN);
			
			if( !icc.hasSide(world, next, offsetSide.getOpposite()) && // it doesn't have a side facing us
				 icc.hasEdge(world, next, edge) && // it has an edge closest to us on the bottom
				 icc.edgeType(world, next, edge) == EnumEdgeType.FULL // its' edge is a full block across
			  ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean testExtTop(World world, BlockPos pos, EnumFacing virtualSide, IExtendedBlockState ladderState) {
		EnumFacing facing = ladderState.getValue(Const.FACING);
		EnumFacing offsetSide = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, facing), virtualSide);
		
		if(!ladderState.getValue(Const.sideProperties.get(virtualSide))) // if we don't have a side we shouldn't have a landing
			return false;
		
		BlockPos up = pos.offset(EnumFacing.UP);
		BlockPos next = pos.offset(offsetSide);
		BlockPos nextup = next.offset(EnumFacing.UP);
		
		if(isTransp(world, nextup) && isSideSolid(world, next, EnumFacing.UP))
			return true;
		
		if(world.getBlockState(up).getBlock() instanceof ICatwalkConnect) {
				// if we have the side and there's a connectable block above us and...
			ICatwalkConnect icc = (ICatwalkConnect) world.getBlockState(up).getBlock();
			if(icc.hasSide(world, up, EnumFacing.DOWN)) // if the block above has a side above, don't put them.
				return false; 
			CubeEdge edge = new CubeEdge(offsetSide, EnumFacing.DOWN);
			if(  icc.edgeType(world, up, edge) == EnumEdgeType.FULL && // if there is a full edge
				 icc.hasEdge(world, up, edge)// and it exists (duh)
			  ) {
				return true;
			}
		}
		
		if(world.getBlockState(nextup).getBlock() instanceof ICatwalkConnect) {
			ICatwalkConnect icc = (ICatwalkConnect) world.getBlockState(nextup).getBlock();
			CubeEdge edge = new CubeEdge(offsetSide.getOpposite(), EnumFacing.DOWN);
			
			if( !icc.hasSide(world, nextup, offsetSide.getOpposite()) && // it doesn't have a side facing us
				 icc.hasEdge(world, nextup, edge) && // it has an edge closest to us
				 icc.edgeType(world, nextup, edge) == EnumEdgeType.FULL // it's edge is a full block across
			  ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean testCorner(World world, BlockPos pos, EnumFacing virtualSide1, EnumFacing virtualSide2, IExtendedBlockState ladderState) {
		return ladderState.getValue(Const.sideProperties.get(virtualSide1)) != ladderState.getValue(Const.sideProperties.get(virtualSide2)) && ( // one open one not
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
			if(icc.sideType(world, next, offsetMain.getOpposite()) == EnumSideType.FULL &&
					icc.hasEdge(world, next, new CubeEdge(offsetMain.getOpposite(), offsetSecondary)))
				return true;
		}
		
		if(world.getBlockState(corner).getBlock() instanceof ICatwalkConnect) {
			ICatwalkConnect icc = (ICatwalkConnect) world.getBlockState(corner).getBlock();
			if(icc.sideType(world, corner, offsetSecondary.getOpposite()) == EnumSideType.FULL &&
					icc.hasEdge(world, corner, new CubeEdge(offsetMain.getOpposite(), offsetSecondary.getOpposite())))
				return true;
		}
		
		if(world.getBlockState(side).getBlock() instanceof ICatwalkConnect) {
			ICatwalkConnect icc = (ICatwalkConnect) world.getBlockState(side).getBlock();
			if(icc.sideType(world, next, offsetMain) == EnumSideType.FULL &&
					icc.hasEdge(world, side, new CubeEdge(offsetMain, offsetSecondary.getOpposite())))
				return true;
		}
		
		return false;
	}
	
	private boolean isTransp(World world, BlockPos pos) {
		return !world.getBlockState(pos).getBlock().isFullBlock(world.getBlockState(pos));
	}
	
	private boolean isSideSolid(World world, BlockPos pos, EnumFacing side) {
		return world.getBlockState(pos).getBlock().isSideSolid(world.getBlockState(pos), world, pos, side);
	}
	
	{ /* ICatwalkConnect */ }
	
	@Override
	public boolean hasEdge(World world, BlockPos pos, CubeEdge edge) {
		IExtendedBlockState state = GeneralUtil.getTileState(world, pos);
		edge.dir1 = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(Const.FACING)), edge.dir1);
		edge.dir2 = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(Const.FACING)), edge.dir2);
		return state.getValue(sides.getA(edge.dir1)) || (edge.dir2.getAxis() != Axis.Y && state.getValue(sides.getA(edge.dir2)) );
	}
	
	@Override
	public EnumEdgeType edgeType(World world, BlockPos pos, CubeEdge edge) {
		return EnumEdgeType.LADDER;
	}
	
	@Override
	public boolean hasSide(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = GeneralUtil.getTileState(world, pos);
		side = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(Const.FACING)), side);
		return (side.getAxis() != Axis.Y && state.getValue(sides.getA(side)) );
	}
	
	@Override
	public void setSide(World world, BlockPos pos, EnumFacing side, boolean value) {
		if(side == EnumFacing.UP)
			return;
		IExtendedBlockState state = GeneralUtil.getTileState(world, pos);
		side = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(Const.FACING)), side);
		
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
	public List<CollisionBox> getCollisionBoxes(IBlockState rawstate, World world, BlockPos pos) {
		IExtendedBlockState state = getTileState(rawstate, world, pos);
		EnumFacing facing = state.getValue(Const.FACING);
		List<CollisionBox> list = collisionBoxes.get(facing);
		if(list == null) {
			Logs.warn("Tried to get collision boxes for invalid facing value! %s at %s", GeneralUtil.getWorldPosLogInfo(world, pos));
			Logs.warn("Removed invalid CatwalkStair block at %s", GeneralUtil.getWorldPosLogInfo(world, pos));
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
		double sm = 3*p; // the distance for the small hit boxes
		
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
			new Vector3(m-sm, 0, p),
			new Vector3(m-sm, 1, p),
			new Vector3(m+sm, 1, p),
			new Vector3(m+sm, 0, p)
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
			new Vector3(m-sm, 0, P),
			new Vector3(m-sm, 1, P),
			new Vector3(m+sm, 1, P),
			new Vector3(m+sm, 0, P)
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
		
		// Top
		side.mainSide = new Quad(
			new Vector3(m-sm, 1, m-sm),
			new Vector3(m-sm, 1, m+sm),
			new Vector3(m+sm, 1, m+sm),
			new Vector3(m+sm, 1, m-sm)
		);
		
		side.wrenchSide = null;
		
		side.showProperty = Const.IS_TOP;
		side.side = EnumFacing.UP;
		sides.add(side.copy());
	
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
			
        	List<LookSide> turnedSides = new ArrayList<>();
        	int rot = GeneralUtil.getRotation(EnumFacing.NORTH, dir);
        	
        	for (LookSide rawSide : sides) {
        		LookSide turnedSide = rawSide.copy();
        		if(turnedSide.mainSide != null)
        			turnedSide.mainSide.rotateCenter(rot);
        		if(turnedSide.wrenchSide != null)
        			turnedSide.wrenchSide.rotateCenter(rot);
        		turnedSide.side = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, dir), turnedSide.side);
				turnedSides.add(turnedSide);
			}
        	
        	sideLookBoxes.put(dir, turnedSides);
		}
	}

	@Override
	public List<LookSide> lookSides(IBlockState state, World world, BlockPos pos) {
		return sideLookBoxes.get(getTileState(state, world, pos).getValue(Const.FACING));
	}
	
}
