package catwalks.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import catwalks.block.extended.TileExtended;
import catwalks.item.ItemBlockCatwalk;
import catwalks.register.BlockRegister;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.AABBUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

public class BlockCatwalkStair extends BlockCatwalkBase {

	public BlockCatwalkStair() {
		super(Material.iron, "catwalkStair", ItemBlockCatwalk.class);
		setHardness(1.5f);
	}

	@Override
	public void updateSide(World worldIn, BlockPos pos, EnumFacing side) {
		IExtendedBlockState ourState = (IExtendedBlockState) getExtendedState(worldIn.getBlockState(pos), worldIn, pos);
		IBlockState state = worldIn.getBlockState(pos.offset(side));
		boolean sideState = true;
		EnumFacing actualSide = side, facing = ourState.getValue(FACING);
		if(side == facing) {
			actualSide = EnumFacing.NORTH;
			if(state.getBlock() instanceof BlockCatwalk) {
				sideState = false;
			}
			if(state.getBlock() instanceof BlockCatwalkMultiblock) {
				IBlockState belowState = worldIn.getBlockState(pos.offset(side).offset(EnumFacing.DOWN));
				IExtendedBlockState extendedState = (IExtendedBlockState) belowState.getBlock().getExtendedState(belowState, worldIn, pos.offset(side).offset(EnumFacing.DOWN));
				if(extendedState.getValue(FACING) == side.getOpposite()) {
					sideState = false;
				}
			}
		}

		if(side.getOpposite() == facing) {
			actualSide = EnumFacing.SOUTH;
			if(state.getBlock() instanceof BlockCatwalk) {
				sideState = false;
			}
			if(state.getBlock() instanceof BlockCatwalkMultiblock) {
				IBlockState belowState = worldIn.getBlockState(pos.offset(side).offset(EnumFacing.DOWN));
				IExtendedBlockState extendedState = (IExtendedBlockState) belowState.getBlock().getExtendedState(belowState, worldIn, pos.offset(side).offset(EnumFacing.DOWN));
				if(extendedState.getValue(FACING) == side.getOpposite()) {
					sideState = false;
				}
			}
		}
		
		if(side.getAxis() != Axis.Y && side.getAxis() != facing.getAxis()) {
			if(facing == EnumFacing.NORTH) {
				// noop
			}
			if(facing == EnumFacing.SOUTH) {
				actualSide = side.getOpposite();
			}
			if(facing == EnumFacing.EAST || facing == EnumFacing.WEST) {
				if(side == EnumFacing.NORTH)
					actualSide = EnumFacing.WEST;
				if(side == EnumFacing.SOUTH)
					actualSide = EnumFacing.EAST;
				if(facing == EnumFacing.WEST)
					actualSide = actualSide.getOpposite();
			}
			
			if(state.getBlock() instanceof BlockCatwalkStair) {
				IExtendedBlockState extendedState = (IExtendedBlockState) state.getBlock().getExtendedState(state, worldIn, pos.offset(side).offset(EnumFacing.DOWN));
				if(extendedState.getValue(FACING) == facing) {
					sideState = false;
				}

			}
		}
		
		TileExtended tile = (TileExtended)worldIn.getTileEntity(pos);
		tile.setBoolean(sides.getC(side), sideState);
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos);// && worldIn.getBlockState(pos.offset(EnumFacing.UP)).getBlock().isReplaceable(worldIn, pos.offset(EnumFacing.UP));
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		worldIn.setBlockState(pos.offset(EnumFacing.UP), BlockRegister.multiblockPart.getDefaultState());
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		
		pos = pos.offset(EnumFacing.UP);
		
		for (EnumFacing direction : EnumFacing.HORIZONTALS) {
			if(worldIn.getBlockState(pos.offset(direction)).getBlock() instanceof BlockCatwalkBase) {
				(  (BlockCatwalkBase)worldIn.getBlockState(pos.offset(direction)).getBlock()  ).updateSide(worldIn, pos.offset(direction), direction.getOpposite());
				(  (BlockCatwalkBase)state.getBlock()  ).updateSide(worldIn, pos, direction);
			}
		}
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		worldIn.setBlockState(pos.offset(EnumFacing.UP), Blocks.air.getDefaultState()); 
		super.breakBlock(worldIn, pos, state);
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
		return collisionBoxes.get(state.getValue(FACING));
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
		side.mainSide  .apply(new Matrix4().translate(new Vector3(0, 1, -1)));
		side.wrenchSide.apply(new Matrix4().translate(new Vector3(0, 1, -1)));
		sides.add(side.copy());
		
		side.mainSide = new Quad(
			new Vector3(0, 0, 1),
			new Vector3(0, 1, 1),
			new Vector3(0, 2, 0),
			new Vector3(0, 1, 0)
		);
		
		side.wrenchSide = new Quad(
			new Vector3(0, 0,   1),
			new Vector3(0, h,   1),
			new Vector3(0, 1+h, 0),
			new Vector3(0, h,   0)
		);
		
		side.showProperty = EAST;
		side.side = EnumFacing.EAST;
		sides.add(side.copy());
		
		side.showProperty = WEST;
		side.side = EnumFacing.WEST;
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
				turnedSides.add(turnedSide);
			}
        	
        	sideLookBoxes.put(dir, turnedSides);
        	
        	matrix.translate(new Vector3(0.5, 0.5, 0.5)).rotate(-q, new Vector3(0, 1, 0)).translate(new Vector3(-0.5, -0.5, -0.5));
		}
	}

	@Override
	public List<LookSide> lookSides(IExtendedBlockState state) {
		initSides();
		return sideLookBoxes.get(state.getValue(FACING));
	}

}
