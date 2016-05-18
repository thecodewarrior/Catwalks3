package catwalks.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import catwalks.Const;
import catwalks.block.extended.CubeEdge;
import catwalks.block.extended.TileExtended;
import catwalks.item.ItemBlockCatwalk;
import catwalks.raytrace.RayTraceUtil.ITraceable;
import catwalks.raytrace.block.BlockTraceable;
import catwalks.raytrace.block.BlockTraceFactory;
import catwalks.raytrace.primitives.Quad;
import catwalks.raytrace.primitives.Tri;
import catwalks.register.BlockRegister;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.AABBUtils;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockCatwalkStair extends BlockCatwalkBase {

	public static final int I_EAST_TOP = I_BASE_LEN+1, I_WEST_TOP = I_BASE_LEN+2;
	public static final double STEP_COUNT = 4;
	public BlockCatwalkStair() {
		super(Material.IRON, "catwalkStair", (c) -> new ItemBlockCatwalk(c));
		setHardness(1.5f);
		setTickRandomly(true);
	}
	
	{ /* blockstate stuffs */ }
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addFunctionalProperties(List<IUnlistedProperty> list) {
		list.add(Const.EAST_TOP);
		list.add(Const.WEST_TOP);
	}

	@Override
	public IExtendedBlockState setFunctionalProperties(TileExtended tile, IExtendedBlockState state) {
		return state
				.withProperty(Const.EAST_TOP, tile.getBoolean(I_EAST_TOP))
				.withProperty(Const.WEST_TOP, tile.getBoolean(I_WEST_TOP))
		;
	}

	{ /* crazy special stair stuff */ }
	
	public boolean checkForValidity(World worldIn, BlockPos pos) {
		if(worldIn.getBlockState(pos.offset(EnumFacing.UP)).getBlock() != BlockRegister.stairTop) {
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			Logs.warn("Removed invalid CatwalkStair block at %s", GeneralUtil.getWorldPosLogInfo(worldIn, pos));
			return false;
		}
		return true;
	}
	
	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
		checkForValidity(worldIn, pos);
	}
	
	@Override
	public EnumFacing transformAffectedSide(World world, BlockPos pos, IBlockState state, EnumFacing side) {
		// I rotate here to turn the actual side into the "virtual" side
		IExtendedBlockState tstate = getTileState(state, world, pos);
		return GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, tstate.getValue(Const.FACING)), side);
	}
	
	{ /* multiblock stuffs */ }
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.getBlockState(pos.offset(EnumFacing.UP)).getBlock().isReplaceable(worldIn, pos.offset(EnumFacing.UP));
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		
		tile.setBoolean(I_EAST_TOP, true);
		tile.setBoolean(I_WEST_TOP, true);
		
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		
		IBlockState placeState = BlockRegister.stairTop.getDefaultState().withProperty(Const.MATERIAL, state.getValue(Const.MATERIAL)).withProperty(Const.LIGHTS, state.getValue(Const.LIGHTS));
		worldIn.setBlockState(pos.offset(EnumFacing.UP), placeState);
		GeneralUtil.updateSurroundingCatwalkBlocks(worldIn, pos.offset(EnumFacing.UP));
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.AIR.getDefaultState(), 6);
		super.breakBlock(worldIn, pos, state);
		GeneralUtil.updateSurroundingCatwalkBlocks(worldIn, pos);
	}
	
	{ /* collision */ }
	
	public Map<EnumFacing, List<CollisionBox>> collisionBoxes;
	
	@Override
	public void initColllisionBoxes() {
		Builder<EnumFacing, List<CollisionBox>> builder = ImmutableMap.<EnumFacing, List<CollisionBox>>builder();
		List<CollisionBox> boxes = new ArrayList<>();
		
		AxisAlignedBB bounds = new AxisAlignedBB(0,0,0 , 1,1,1);
        double thickness = Float.MIN_VALUE, stepLength = 1.0/STEP_COUNT;
        
        Cuboid6 cuboid = new Cuboid6(bounds);
        
        AABBUtils.offsetSide(cuboid, EnumFacing.UP, -(1-thickness));
        AABBUtils.offsetSide(cuboid, EnumFacing.NORTH, -(1-stepLength));
        
        cuboid.offset(new Vector3(0, stepLength/2.0, 0));
        
        for (int i = 0; i < STEP_COUNT; i++) {
        	CollisionBox box = new CollisionBox();
            
            box.enableProperty = Const.CONST_TRUE;
            
            box.normal = cuboid.copy();
            box.sneak  = cuboid.copy();
            
            boxes.add(box);
            
            cuboid.offset(new Vector3(0, stepLength, -stepLength));
		}
        
        cuboid = new Cuboid6(bounds);
        Cuboid6 cuboid2 = new Cuboid6(bounds);
        
        AABBUtils.offsetSide(cuboid,  EnumFacing.NORTH, -(1-stepLength));
        AABBUtils.offsetSide(cuboid2, EnumFacing.NORTH, -(1-stepLength));

        AABBUtils.offsetSide(cuboid,  EnumFacing.EAST,  -(1-thickness));
        AABBUtils.offsetSide(cuboid2, EnumFacing.WEST,  -(1-thickness));
        
        // West
        for (int i = 0; i < STEP_COUNT; i++) {
        	// top
        	CollisionBox box = new CollisionBox();
        	
            box.enableProperty = Const.WEST_TOP;
            
            box.normal = cuboid.copy();
            box.normal.max.y += 0.5;
            box.normal.min.y = 1;
            box.sneak  = cuboid.copy();
            box.sneak.min.y = 1;
            
            boxes.add(box);
            
            // bottom
            box = new CollisionBox();
            
            box.enableProperty = Const.WEST;
            
            box.normal = cuboid.copy();
            box.normal.max.y = 1;
            box.sneak  = cuboid.copy();
            box.sneak.max.y = 1;
            
            boxes.add(box);
            
            cuboid.offset(new Vector3(0, stepLength, -stepLength));
		}
        
        for (int i = 0; i < STEP_COUNT; i++) {
        	// top
        	CollisionBox box = new CollisionBox();
        	
            box.enableProperty = Const.EAST_TOP;
            
            box.normal = cuboid2.copy();
            box.normal.max.y += 0.5;
            box.normal.min.y = 1;
            box.sneak  = cuboid2.copy();
            box.sneak.min.y = 1;
            
            boxes.add(box);
            
            // bottom
            box = new CollisionBox();
            
            box.enableProperty = Const.EAST;
            
            box.normal = cuboid2.copy();
            box.normal.max.y = 1;
            box.sneak  = cuboid2.copy();
            box.sneak.max.y = 1;
            
            boxes.add(box);
            
            cuboid2.offset(new Vector3(0, stepLength, -stepLength));
		}
        
        cuboid = new Cuboid6(bounds);
        AABBUtils.offsetSide(cuboid, EnumFacing.NORTH, -(1-thickness));

        CollisionBox box = new CollisionBox();

        box.enableProperty = Const.SOUTH;
        
        box.normal = cuboid.copy();
        box.normal.max.y += 0.5;
        box.sneak  = cuboid.copy();
        
        boxes.add(box);
        
        cuboid.offset(new Vector3(0, 1, -1));
        
        box = new CollisionBox();

        box.enableProperty = Const.NORTH;
        
        box.normal = cuboid.copy();
        box.normal.max.y += 0.5;
        box.sneak  = cuboid.copy();
        
        boxes.add(box);
        
        double q = Math.toRadians(90);
        
        Matrix4 matrix = new Matrix4();
        
        for (EnumFacing dir : new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST}) {
			
        	List<CollisionBox> turnedBoxes = new ArrayList<>();
        	
        	for (CollisionBox rawBox : boxes) {
				CollisionBox turnedBox = rawBox.copy();
				turnedBox.apply(matrix);
				turnedBoxes.add(turnedBox);
			}
        	
        	builder.put(dir, turnedBoxes);
        	
        	matrix.translate(new Vector3(0.5, 0.5, 0.5)).rotate(-q, new Vector3(0, 1, 0)).translate(new Vector3(-0.5, -0.5, -0.5));
		}
        collisionBoxes = builder.build();
	}
	
	@Override
	public List<CollisionBox> getCollisionBoxes(IBlockState state, World world, BlockPos pos) {
		EnumFacing facing = getTileState(state, world, pos).getValue(Const.FACING);
		List<CollisionBox> list = collisionBoxes.get(facing);
		if(list == null) {
			Logs.warn("Tried to get collision boxes for invalid facing value! %s at %s",
					facing.toString().toUpperCase(), GeneralUtil.getWorldPosLogInfo(world, pos));
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			Logs.warn("Removed invalid CatwalkStair block at %s", GeneralUtil.getWorldPosLogInfo(world, pos));
			list = collisionBoxes.get(EnumFacing.NORTH);
		}
		return list;
	}
	
	{ /* hit boxes */ }
	
	private Map<EnumFacing, List<BlockTraceable>> sideLookBoxes;

	@Override
	public void initSides() {
		sideLookBoxes = new HashMap<>();
		
		BlockTraceFactory factory = new BlockTraceFactory();
		
		// bottom end
		factory.setShown(
			new Quad(
				new Vec3d(0, 0, 1),
				new Vec3d(0, 1, 1),
				new Vec3d(1, 1, 1),
				new Vec3d(1, 0, 1)
			)
		);
		
		double h = 0.5;
		factory.setHidden(
			new Quad(
				new Vec3d(0, 0, 1),
				new Vec3d(0, h, 1),
				new Vec3d(1, h, 1),
				new Vec3d(1, 0, 1)
			)
		);
		factory.setEnable(Const.SOUTH);
		factory.setSide(EnumFacing.SOUTH);
		factory.commit();
		
		// top end
		factory.translate(0, 1, -1); // up and toward the north
		factory.setEnable(Const.NORTH);
		factory.setSide(EnumFacing.NORTH);
		factory.setOffset(0, 1, 0);
		factory.commit();
		
		factory.setOffset(0, 0, 0);
		
		// bottom sides
		factory.setShown(
			new Tri(
				new Vec3d(0, 0, 1),
				new Vec3d(0, 1, 1),
				new Vec3d(0, 1, 0)
			)
		);
		
		factory.setHidden(
			new Quad(
				new Vec3d(0, 0, 1),
				new Vec3d(0, h, 1),
				new Vec3d(0, 1, h),
				new Vec3d(0, 1, 0)
			).noNet()
		);
		factory.setEnable(Const.WEST);
		factory.setSide(EnumFacing.WEST);
		factory.commit();
		
		factory.translate(1, 0, 0);
		factory.setEnable(Const.EAST);
		factory.setSide(EnumFacing.EAST);
		factory.commit();
		
		// top sides
		factory.setShown(
			new Tri(
				new Vec3d(0, 0, 1),
				new Vec3d(0, 1, 0),
				new Vec3d(0, 0, 0)
			)
		);
		
		factory.setHidden(
			new Tri(
				new Vec3d(0, 0, h),
				new Vec3d(0, h, 0),
				new Vec3d(0, 0, 0)
			)
		);
		factory.translate(0, 1, 0);
		
		factory.setEnable(Const.WEST_TOP);
		factory.setSide(EnumFacing.WEST);
		factory.setOffset(0, 1, 0);
		factory.commit();
		
		factory.setEnable(Const.EAST_TOP);
		factory.setSide(EnumFacing.EAST);
		factory.translate(1, 0, 0);
		factory.commit();
		
		// steps
		
		double stepLength = 1.0/STEP_COUNT;
		
		factory.setOffset(0, 0, 0);
        factory.setAlwaysShow(true);
        factory.setSide(EnumFacing.DOWN);
        factory.setShown(
    		new Quad(
        		new Vec3d(0, stepLength/2, 1),
        		new Vec3d(0, stepLength/2, 1-stepLength),
        		new Vec3d(1, stepLength/2, 1-stepLength),
        		new Vec3d(1, stepLength/2, 1)
    		)
    	);
        
		for (int i = 0; i < STEP_COUNT; i++) {
			factory.commit();
			factory.translate(0, stepLength, -stepLength);
		}
		
//		factory.setOffset(0, 0, 0);
//        factory.setAlwaysShow(true);
//        factory.setSide(EnumFacing.DOWN);
//		factory.setShown(
//			new Quad(
//				new Vec3d(0, 1, 0),
//				new Vec3d(1, 1, 0),
//				new Vec3d(1, 0, 1),
//				new Vec3d(0, 0, 1)
//			).noNet()
//		);     
//		factory.commit();
        
		for (EnumFacing dir : Const.HORIZONTALS_FROM_NORTH) {
        	sideLookBoxes.put(dir, factory.build());
        	factory.rotateAll(1);
		}
	}

	@Override
	public List<? extends ITraceable<BlockTraceParam, BlockTraceResult>> lookSides(IBlockState state, World world,
			BlockPos pos) {
		return sideLookBoxes.get(getTileState(state, world, pos).getValue(Const.FACING));
	}
	
	{ /* ICatwalkConnect */ }

	@Override
	public boolean hasEdge(World world, BlockPos pos, CubeEdge edge) {
		IExtendedBlockState state = getTileState(world.getBlockState(pos), world, pos);
		
		if(GeneralUtil.checkEdge(EnumFacing.DOWN, state.getValue(Const.FACING).getOpposite(), edge) && !state.getValue(Const.SOUTH))
			return true;
		
		if(edge.dir1 == state.getValue(Const.FACING) || edge.dir2 == state.getValue(Const.FACING)) {
			return false;
		}
		
		return super.hasEdge(world, pos, edge);
	}
	
	@Override
	public EnumEdgeType edgeType(World world, BlockPos pos, CubeEdge edge) {
		return EnumEdgeType.FULL;
	}
	
	@Override
	public boolean hasSide(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = getTileState(world.getBlockState(pos), world, pos);
		EnumFacing actualDir = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(Const.FACING)), side);
		if(actualDir == EnumFacing.EAST && state.getValue(Const.EAST_TOP)) {
			return true;
		}
		if(actualDir == EnumFacing.WEST && state.getValue(Const.WEST_TOP)) {
			return true;
		}
		if(side == state.getValue(Const.FACING).getOpposite()) {
			return state.getValue(Const.SOUTH);
		}
		return false;
	}
	
	@Override
	public void setSide(World world, BlockPos pos, EnumFacing side, boolean value) {
		IExtendedBlockState state = getTileState(world.getBlockState(pos), world, pos);
		TileExtended tile = (TileExtended) world.getTileEntity(pos);
		
		EnumFacing actualDir = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(Const.FACING)), side);
		if(actualDir == EnumFacing.EAST) {
			tile.setBoolean(BlockCatwalkBase.I_EAST, value);
		}
		if(actualDir == EnumFacing.WEST) {
			tile.setBoolean(BlockCatwalkBase.I_WEST, value);
		}
		if(side == state.getValue(Const.FACING).getOpposite()) {
			tile.setBoolean(BlockCatwalkBase.I_SOUTH, value);
		}
	}
	
	@Override
	public Object sideData(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = getTileState(world.getBlockState(pos), world, pos);
		if(side.getAxis() != state.getValue(Const.FACING).getAxis())
			return state.getValue(Const.FACING);
		return null;
	}
	
	@Override
	public EnumSideType sideType(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = getTileState(world.getBlockState(pos), world, pos);
		if(side == state.getValue(Const.FACING)) {
			return null;
		}
		if(side == state.getValue(Const.FACING).getOpposite()) {
			return EnumSideType.FULL;
		}
		return EnumSideType.SLOPE_BOTTOM;
	}
	
}
