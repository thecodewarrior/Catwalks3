package catwalks.block;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import catwalks.Const;
import catwalks.block.extended.CubeEdge;
import catwalks.block.extended.TileExtended;
import catwalks.item.ItemBlockCatwalk;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.AABBUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

public class BlockCatwalk extends BlockCatwalkBase {
	
	public BlockCatwalk() {
		super(Material.IRON, "catwalk", (c) -> new ItemBlockCatwalk(c));
		setHardness(1.5f);
	}
	
	@Override
	public boolean hasEdge(World world, BlockPos pos, CubeEdge edge) {
		IExtendedBlockState state = getTileState(world.getBlockState(pos), world, pos);
		return 
				(   state.getProperties().containsKey(Const.sideProperties.get(edge.dir1)) && state.getValue(Const.sideProperties.get(edge.dir1))   )
						!=
				(   state.getProperties().containsKey(Const.sideProperties.get(edge.dir2)) && state.getValue(Const.sideProperties.get(edge.dir2))   )
		;
	}
	
	@Override
	public EnumEdgeType edgeType(World world, BlockPos pos, CubeEdge edge) {
		return EnumEdgeType.FULL;
	}
	
	@Override
	public boolean hasSide(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = getTileState(world.getBlockState(pos), world, pos);
		return state.getProperties().containsKey(Const.sideProperties.get(side)) && state.getValue(Const.sideProperties.get(side));
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
		return EnumSideType.FULL;
	}
	
	private List<LookSide> sideLookBoxes;
	
	@Override
	public void initSides() {
		Vector3 center    = new Vector3( 0.5,  0.5,  0.5),
				negCenter = new Vector3(-0.5, -0.5, -0.5);
		
		Matrix4 matrix = new Matrix4();
		matrix.translate(center);
		
		sideLookBoxes = new ArrayList<>();
		
		LookSide side = new LookSide();
		
		side.mainSide = new Quad(
			new Vector3(0, 0, 0),
			new Vector3(0, 1, 0),
			new Vector3(1, 1, 0),
			new Vector3(1, 0, 0)
		);
		
		double h = 0.5;
		side.wrenchSide = new Quad(
			new Vector3(0, 0, 0),
			new Vector3(0, h, 0),
			new Vector3(1, h, 0),
			new Vector3(1, 0, 0)
		);
		
		LookSide s;
		
		double q = Math.toRadians(90);
		Vector3 y = new Vector3(0, 1, 0);
		s = side.copy();
//		s.apply(new Matrix4().translate(center).translate(negCenter));
		s.showProperty = Const.NORTH; s.side = EnumFacing.NORTH;
		sideLookBoxes.add(s);
		
		s = side.copy();
		s.apply(new Matrix4().translate(center).rotate(-q, y).translate(negCenter));
		s.showProperty = Const.EAST; s.side = EnumFacing.EAST;
		sideLookBoxes.add(s);
		
		s = side.copy();
		s.apply(new Matrix4().translate(center).rotate(2*q, y).translate(negCenter));
		s.showProperty = Const.SOUTH; s.side = EnumFacing.SOUTH;
		sideLookBoxes.add(s);
		
		s = side.copy();
		s.apply(new Matrix4().translate(center).rotate(q, y).translate(negCenter));
		s.showProperty = Const.WEST; s.side = EnumFacing.WEST;
		sideLookBoxes.add(s);
		
		
		s = side.copy();
		s.apply(new Matrix4().translate(center).rotate(-q, Vector3.axis(Axis.X)).translate(negCenter));
		s.showProperty = Const.BOTTOM; s.side = EnumFacing.DOWN;
		s.showWithoutWrench = true;
		s.wrenchSide = s.mainSide.copy();
		s.wrenchSide.apply( new Matrix4().translate(new Vector3(0.5, 0, 0.5)).scale(new Vector3(0.5, 1, 0.5)).translate(new Vector3(-0.5, 0, -0.5)) );
		sideLookBoxes.add(s);
	}
	
	@Override
	public List<LookSide> lookSides(IBlockState state, World world, BlockPos pos) {
		return sideLookBoxes;
	}
	
	{ /* collision */ }
	
	private List<CollisionBox> collisionBoxes;
	
	@Override
	public void initColllisionBoxes() {
		Builder<CollisionBox> builder = ImmutableList.<CollisionBox>builder();
		
		AxisAlignedBB bounds = new AxisAlignedBB(0,0,0 , 1,1,1);
        double thickness = Float.MIN_VALUE;
        
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
        	CollisionBox box = new CollisionBox();
        	box.enableProperty = sides.getA(facing);
        	
        	Cuboid6 cuboid = new Cuboid6(bounds);
        	
        	AABBUtils.offsetSide(cuboid, facing.getOpposite(), -(1-thickness));

        	box.sneak = cuboid.copy();

        	cuboid.max.y += 0.5;
        	box.normal = cuboid.copy();
        	
        	builder.add(box);
		}
        
        CollisionBox box = new CollisionBox();
    	box.enableProperty = sides.getA(EnumFacing.DOWN);
    	
    	Cuboid6 cuboid = new Cuboid6(bounds);
    	
    	AABBUtils.offsetSide(cuboid, EnumFacing.UP, -(1-thickness));

    	box.sneak = cuboid.copy();

//    	cuboid.max.y += 0.5;
    	box.normal = cuboid.copy();
        
    	builder.add(box);
		
    	collisionBoxes = builder.build();
	}
	
	@Override
	public List<CollisionBox> getCollisionBoxes(IBlockState state, World world, BlockPos pos) {
		return collisionBoxes;
	}
}
