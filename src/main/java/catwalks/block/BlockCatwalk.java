package catwalks.block;

import catwalks.Const;
import catwalks.block.extended.CubeEdge;
import catwalks.block.extended.tileprops.TileExtended;
import catwalks.item.ItemBlockCatwalk;
import catwalks.raytrace.RayTraceUtil.ITraceable;
import catwalks.raytrace.block.BlockTraceFactory;
import catwalks.raytrace.block.BlockTraceable;
import catwalks.raytrace.primitives.Quad;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.util.AABBUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.List;

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
		sideProps.get(side).set(tile, value);
	}
	
	@Override
	public Object sideData(World world, BlockPos pos, EnumFacing side) {
		return null;
	}
	
	@Override
	public EnumSideType sideType(World world, BlockPos pos, EnumFacing side) {
		return EnumSideType.FULL;
	}
	
	private List<BlockTraceable> sideLookBoxes;
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		MATERIAL.set(tile, EnumCatwalkMaterial.values()[stack.getItemDamage()]);
	}
	
	@Override
	public void initSides() {
		BlockTraceFactory factory = new BlockTraceFactory();
		double h = 0.5;
		
		factory.setShown(
			new Quad(
				new Vec3d(0, 0, 0),
				new Vec3d(0, 1, 0),
				new Vec3d(1, 1, 0),
				new Vec3d(1, 0, 0)
			)
		);
		factory.setHidden(
			new Quad(
				new Vec3d(0, 0, 0),
				new Vec3d(0, h, 0),
				new Vec3d(1, h, 0),
				new Vec3d(1, 0, 0)
			)
		);
		
		factory.setEnable(Const.NORTH);
		factory.setSide(EnumFacing.NORTH);
		factory.commit();
		
		factory.rotate(1);
		factory.setEnable(Const.EAST);
		factory.setSide(EnumFacing.EAST);
		factory.commit();
		
		factory.rotate(1);
		factory.setEnable(Const.SOUTH);
		factory.setSide(EnumFacing.SOUTH);
		factory.commit();
		
		factory.rotate(1);
		factory.setEnable(Const.WEST);
		factory.setSide(EnumFacing.WEST);
		factory.commit();
		
		factory.setShown(
			new Quad(
				new Vec3d(0, 0, 0),
				new Vec3d(1, 0, 0),
				new Vec3d(1, 0, 1),
				new Vec3d(0, 0, 1)
			)
		);
		
		double d = 0.25, D = 1-d;
		factory.setHidden(
			new Quad(
				new Vec3d(d, 0, d),
				new Vec3d(D, 0, d),
				new Vec3d(D, 0, D),
				new Vec3d(d, 0, D)
			)
		);
		
		factory.setEnable(Const.BOTTOM);
		factory.setSide(EnumFacing.DOWN);
		factory.commit();
		
		sideLookBoxes = factory.build();
	}
	
	@Override
	public List<? extends ITraceable<BlockTraceParam, BlockTraceResult>> lookSides(IBlockState state, World world, BlockPos pos) {
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
        	box.enableProperty = sideState.get(facing);
        	
        	Cuboid6 cuboid = new Cuboid6(bounds);
        	
        	AABBUtils.offsetSide(cuboid, facing.getOpposite(), -(1-thickness));

        	box.sneak = cuboid.copy();

        	cuboid.max.y += 0.5;
        	box.normal = cuboid.copy();
        	
        	builder.add(box);
		}
        
        CollisionBox box = new CollisionBox();
    	box.enableProperty = sideState.get(EnumFacing.DOWN);
    	
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
