package catwalks.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import catwalks.block.extended.EnumCubeEdge;
import catwalks.block.extended.TileExtended;
import catwalks.block.property.UPropertyBool;
import catwalks.item.ItemBlockCatwalk;
import catwalks.register.BlockRegister;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.AABBUtils;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockCatwalkStair extends BlockCatwalkBase {

	public static final UPropertyBool EAST_TOP = new UPropertyBool("EASTTOP");
	public static final UPropertyBool WEST_TOP = new UPropertyBool("WESTTOP");
	
	public static final int I_EAST_TOP = I_BASE_LEN+1, I_WEST_TOP = I_BASE_LEN+2;
	
	public BlockCatwalkStair() {
		super(Material.iron, "catwalkStair", ItemBlockCatwalk.class);
		setHardness(1.5f);
	}
	
	@Override
	public void addAdditionalProperties(List<IUnlistedProperty> list) {
		list.add(EAST_TOP);
		list.add(WEST_TOP);
	}
	
	@Override
	public IExtendedBlockState addProperties(TileExtended tile, IExtendedBlockState state) {
		return state
				.withProperty(EAST_TOP, tile.getBoolean(I_EAST_TOP))
				.withProperty(WEST_TOP, tile.getBoolean(I_WEST_TOP))
		;
	}
	
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
		
		worldIn.setBlockState(pos.offset(EnumFacing.UP), BlockRegister.multiblockPart.getDefaultState());
		GeneralUtil.updateSurroundingCatwalkBlocks(worldIn, pos.offset(EnumFacing.UP));
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.air.getDefaultState()); 
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public EnumFacing transformAffectedSide(World world, BlockPos pos, IBlockState state, EnumFacing side) {
		IExtendedBlockState estate = (IExtendedBlockState)getExtendedState(state, world, pos);
		return GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, estate.getValue(BlockCatwalkBase.FACING)), side);
	}
	
	public Map<EnumFacing, List<CollisionBox>> collisionBoxes;
	
	@Override
	public void initColllisionBoxes() {
		collisionBoxes = new HashMap<>();
		List<CollisionBox> boxes = new ArrayList<>();
		
		AxisAlignedBB bounds = new AxisAlignedBB(0,0,0 , 1,1,1);
        double thickness = Float.MIN_VALUE, stepCount = 4, stepLength = 1.0/stepCount;
        
        Cuboid6 cuboid = new Cuboid6(bounds);
        
        AABBUtils.offsetSide(cuboid, EnumFacing.UP, -(1-thickness));
        AABBUtils.offsetSide(cuboid, EnumFacing.NORTH, -(1-stepLength));
        
        cuboid.offset(new Vector3(0, stepLength/2.0, 0));
        
        for (int i = 0; i < stepCount; i++) {
        	CollisionBox box = new CollisionBox();
            
            box.enableProperty = BOTTOM;
            
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
        
        for (int i = 0; i < stepCount; i++) {
        	CollisionBox box = new CollisionBox();
            
            box.enableProperty = EAST;
            
            box.normal = cuboid.copy();
            box.normal.max.y += 0.5;
            box.sneak  = cuboid.copy();
            
            boxes.add(box);
            
            cuboid.offset(new Vector3(0, stepLength, -stepLength));
		}
        
        for (int i = 0; i < stepCount; i++) {
        	CollisionBox box = new CollisionBox();
            
            box.enableProperty = WEST;
            
            box.normal = cuboid2.copy();
            box.normal.max.y += 0.5;
            box.sneak  = cuboid2.copy();
            
            boxes.add(box);
            
            cuboid2.offset(new Vector3(0, stepLength, -stepLength));
		}
        
        cuboid = new Cuboid6(bounds);
        AABBUtils.offsetSide(cuboid, EnumFacing.NORTH, -(1-thickness));

        CollisionBox box = new CollisionBox();

        box.enableProperty = SOUTH;
        
        box.normal = cuboid.copy();
        box.normal.max.y += 0.5;
        box.sneak  = cuboid.copy();
        
        boxes.add(box);
        
        cuboid.offset(new Vector3(0, 1, -1));
        
        box = new CollisionBox();

        box.enableProperty = NORTH;
        
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
        	
        	collisionBoxes.put(dir, turnedBoxes);
        	
        	matrix.translate(new Vector3(0.5, 0.5, 0.5)).rotate(-q, new Vector3(0, 1, 0)).translate(new Vector3(-0.5, -0.5, -0.5));
		}
	}
	
	@Override
	public List<CollisionBox> getCollisionBoxes(IExtendedBlockState state) {
		EnumFacing facing = state.getValue(FACING);
		List<CollisionBox> list = collisionBoxes.get(facing);
		if(list == null) {
			Logs.error("ERROR: tried to get collision boxes for invalid facing value! %s", facing.toString());
		}
		return list;
	}
	
	private Map<EnumFacing, List<LookSide>> sideLookBoxes;

	@Override
	public void initSides() {
		sideLookBoxes = new HashMap<>();
		
		List<LookSide> sides = new ArrayList<>();
		
		LookSide side = new LookSide();
		
		side.mainSide = new Quad(
			new Vector3(0, 0, 1),
			new Vector3(0, 1, 1),
			new Vector3(1, 1, 1),
			new Vector3(1, 0, 1)
		);
		
		double h = 0.5;
		side.wrenchSide = new Quad(
			new Vector3(0, 0, 1),
			new Vector3(0, h, 1),
			new Vector3(1, h, 1),
			new Vector3(1, 0, 1)
		);
		
		side.showProperty = SOUTH;
		side.side = EnumFacing.SOUTH;
		sides.add(side.copy());
		
		side.showProperty = NORTH;
		side.side = EnumFacing.NORTH;
		side.mainSide  .apply(new Matrix4().translate(new Vector3(0, 0, -1)));
		side.wrenchSide.apply(new Matrix4().translate(new Vector3(0, 0, -1)));
		side.offset = new BlockPos(0, 1, 0);
		sides.add(side.copy());
		side.offset = new BlockPos(0, 0, 0);
		
		// bottom sides
		side.mainSide = new Tri(
			new Vector3(0, 0, 1),
			new Vector3(0, 1, 1),
			new Vector3(0, 1, 0)
		);
		
		side.wrenchSide = new Quad(
			new Vector3(0, 0, 1),
			new Vector3(0, h, 1),
			new Vector3(0, 1, h),
			new Vector3(0, 1, 0)
		);
		
		side.showProperty = WEST;
		side.side = EnumFacing.WEST;
		sides.add(side.copy());
		
		side.showProperty = EAST;
		side.side = EnumFacing.EAST;
		side.mainSide  .apply(new Matrix4().translate(new Vector3(1, 0, 0)));
		side.wrenchSide.apply(new Matrix4().translate(new Vector3(1, 0, 0)));
		sides.add(side.copy());
		
		
		// top sides
		side.mainSide = new Tri(
			new Vector3(0, 0, 1),
			new Vector3(0, 1, 0),
			new Vector3(0, 0, 0)
		);
		
		side.wrenchSide = new Quad(
			new Vector3(0, 0, h),
			new Vector3(0, h, 0),
			new Vector3(0, 0, 0),
			new Vector3(0, 0, 0)
		);
		
		side.offset = new BlockPos(0, 1, 0);
		
		side.showProperty = WEST_TOP;
		side.side = EnumFacing.WEST;
		sides.add(side.copy());
		
		side.showProperty = EAST_TOP;
		side.side = EnumFacing.EAST;
		side.mainSide  .apply(new Matrix4().translate(new Vector3(1, 0, 0)));
		side.wrenchSide.apply(new Matrix4().translate(new Vector3(1, 0, 0)));
		sides.add(side.copy());
		
		double q = Math.toRadians(90);
		Matrix4 matrix = new Matrix4();
		
        for (EnumFacing dir : new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST}) {
			
        	List<LookSide> turnedSides = new ArrayList<>();
        	
        	for (LookSide rawSide : sides) {
        		LookSide turnedSide = rawSide.copy();
        		turnedSide.apply(matrix);
        		turnedSide.side = GeneralUtil.rotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, dir), turnedSide.side);
				turnedSides.add(turnedSide);
			}
        	
        	sideLookBoxes.put(dir, turnedSides);
        	
        	matrix.translate(new Vector3(0.5, 0.5, 0.5)).rotate(-q, new Vector3(0, 1, 0)).translate(new Vector3(-0.5, -0.5, -0.5));
		}
	}

	@Override
	public List<LookSide> lookSides(IExtendedBlockState state) {
		return sideLookBoxes.get(state.getValue(FACING));
	}
	
	{ /* ICatwalkConnect */ }

	@Override
	public boolean hasEdge(World world, BlockPos pos, EnumCubeEdge edge) {
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
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
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		EnumFacing actualDir = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(BlockCatwalkBase.FACING)), side);
		if(actualDir == EnumFacing.EAST && state.getValue(BlockCatwalkStair.EAST_TOP)) {
			return true;
		}
		if(actualDir == EnumFacing.WEST && state.getValue(BlockCatwalkStair.WEST_TOP)) {
			return true;
		}
		if(side == state.getValue(BlockCatwalkBase.FACING).getOpposite()) {
			return state.getValue(BlockCatwalkBase.SOUTH);
		}
		return false;
	}
	
	@Override
	public void setSide(World world, BlockPos pos, EnumFacing side, boolean value) {
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		TileExtended tile = (TileExtended) world.getTileEntity(pos);
		
		EnumFacing actualDir = GeneralUtil.derotateFacing(GeneralUtil.getRotation(EnumFacing.NORTH, state.getValue(BlockCatwalkBase.FACING)), side);
		if(actualDir == EnumFacing.EAST && state.getValue(BlockCatwalkBase.EAST)) {
			tile.setBoolean(BlockCatwalkBase.I_EAST, value);
		}
		if(actualDir == EnumFacing.WEST && state.getValue(BlockCatwalkBase.WEST)) {
			tile.setBoolean(BlockCatwalkBase.I_WEST, value);
		}
		if(side == state.getValue(BlockCatwalkBase.FACING).getOpposite()) {
			tile.setBoolean(BlockCatwalkBase.I_SOUTH, value);
		}
	}
	
	@Override
	public Object sideData(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		return state.getValue(BlockCatwalkBase.FACING);
	}
	
	@Override
	public EnumSideType sideType(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		if(side == state.getValue(BlockCatwalkBase.FACING)) {
			return null;
		}
		if(side == state.getValue(BlockCatwalkBase.FACING).getOpposite()) {
			return EnumSideType.FULL;
		}
		return EnumSideType.SLOPE_BOTTOM;
	}
	
}
