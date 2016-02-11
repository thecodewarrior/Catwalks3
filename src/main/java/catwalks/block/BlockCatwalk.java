package catwalks.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import catwalks.block.extended.ExtendedData;
import catwalks.block.extended.TileExtended;
import catwalks.item.ItemBlockCatwalk;
import catwalks.shade.ccl.raytracer.IndexedCuboid6;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.AABBUtils;
import catwalks.util.WrenchChecker;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;

public class BlockCatwalk extends BlockCatwalkBase implements ICatwalkConnect {
	
	
	public BlockCatwalk() {
		super(Material.iron, "catwalk", ItemBlockCatwalk.class);
		setHardness(1.5f);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if( playerIn.inventory.getCurrentItem() != null) {
			if(!WrenchChecker.isAWrench( playerIn.inventory.getCurrentItem().getItem() ))
				return false;
			if(playerIn.inventory.getCurrentItem().getItem() instanceof ItemBlock)
				return false;
		} else {
			return false;
		}
		
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		int id = sides.getC(side);
		
		if(side != EnumFacing.UP ) {
			tile.setBoolean(id, !tile.getBoolean(id));
			tile.markDirty();
			worldIn.markBlockForUpdate(pos);
			return true;
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		
		for (EnumFacing direction : EnumFacing.HORIZONTALS) {
			if(worldIn.getBlockState(pos.offset(direction)).getBlock() == this) {
				TileExtended tile = (TileExtended) worldIn.getTileEntity(pos.offset(direction));
				if(tile.getBoolean(sides.getC(direction.getOpposite())) == false) {
					tile.setBoolean(sides.getC(direction.getOpposite()), true);
				}
			}
			
		}
		
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileExtended ourTile = (TileExtended) worldIn.getTileEntity(pos);
		
		for (EnumFacing direction : EnumFacing.VALUES) {
			ourTile.setBoolean(sides.getC(direction), true);
		}
		
		for (EnumFacing direction : EnumFacing.HORIZONTALS) {
			if(worldIn.getBlockState(pos.offset(direction)).getBlock() == this) {
				TileExtended tile = (TileExtended) worldIn.getTileEntity(pos.offset(direction));
				ourTile.setBoolean(sides.getC(direction), false);
				   tile.setBoolean(sides.getC(direction.getOpposite()), false);
			}
			
		}
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
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
		s.apply(new Matrix4().translate(center).translate(negCenter));
		s.showProperty = NORTH; s.side = EnumFacing.NORTH;
		sideLookBoxes.add(s);
		
		s = side.copy();
		s.apply(new Matrix4().translate(center).rotate(-q, y).translate(negCenter));
		s.showProperty = EAST; s.side = EnumFacing.EAST;
		sideLookBoxes.add(s);
		
		s = side.copy();
		s.apply(new Matrix4().translate(center).rotate(2*q, y).translate(negCenter));
		s.showProperty = SOUTH; s.side = EnumFacing.SOUTH;
		sideLookBoxes.add(s);
		
		s = side.copy();
		s.apply(new Matrix4().translate(center).rotate(q, y).translate(negCenter));
		s.showProperty = WEST; s.side = EnumFacing.WEST;
		sideLookBoxes.add(s);
		
		
		s = side.copy();
		s.apply(new Matrix4().translate(center).rotate(-q, Vector3.axis(Axis.X)).translate(negCenter));
		s.showProperty = BOTTOM; s.side = EnumFacing.DOWN;
		s.showWithoutWrench = true;
		s.wrenchSide = s.mainSide.copy();
		s.wrenchSide.apply( new Matrix4().translate(new Vector3(0.5, 0, 0.5)).scale(new Vector3(0.5, 1, 0.5)).translate(new Vector3(-0.5, 0, -0.5)) );
		sideLookBoxes.add(s);
	}
	
	@Override
	public List<LookSide> lookSides(IExtendedBlockState state) {
		return sideLookBoxes;
	}
	
	{ /* collision */ }
	
	public List<CollisionBox> collisionBoxes;
	
	@Override
	public void initColllisionBoxes() {
		collisionBoxes = new ArrayList<>();
		
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
        	
        	collisionBoxes.add(box);
		}
        
        CollisionBox box = new CollisionBox();
    	box.enableProperty = sides.getA(EnumFacing.DOWN);
    	
    	Cuboid6 cuboid = new Cuboid6(bounds);
    	
    	AABBUtils.offsetSide(cuboid, EnumFacing.UP, -(1-thickness));

    	box.sneak = cuboid.copy();

//    	cuboid.max.y += 0.5;
    	box.normal = cuboid.copy();
        
    	collisionBoxes.add(box);
		
	}
	
	@Override
	public List<CollisionBox> getCollisionBoxes(IExtendedBlockState state) {
		return collisionBoxes;
	}
	

	@Override
	public int damageDropped(IBlockState state) {
	    return state.getValue(MATERIAL).ordinal();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	{/* ICatwalkConnectable */}
	
	public boolean isSideOpen(World world, BlockPos pos, EnumFacing side) {
		TileExtended tile = (TileExtended) world.getTileEntity(pos);
		return tile.getBoolean(sides.getC(side));
	}

	public boolean isWide(World world, BlockPos pos, EnumFacing side) {
		return true;
	}

	{ /* rendering */}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		return state.getValue(LIGHTS) ? 15 : 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT_MIPPED;
	}
	
	{/* accessors */}

	@Override
	public ExtendedData getData(World world, IBlockState state) {
		return null;
	}
}
