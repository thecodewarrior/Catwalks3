package catwalks.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import catwalks.block.extended.BlockExtended;
import catwalks.block.extended.ExtendedData;
import catwalks.block.extended.TileExtended;
import catwalks.block.property.UPropertyBool;
import catwalks.register.ItemRegister;
import catwalks.util.AABBUtils;
import catwalks.util.ExtendedFlatHighlightMOP;
import codechicken.lib.raytracer.ExtendedMOP;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockCatwalk extends BlockExtended implements ICatwalkConnect {

	public static UPropertyBool BOTTOM = new UPropertyBool("bottom");
	public static UPropertyBool NORTH  = new UPropertyBool("north");
	public static UPropertyBool SOUTH  = new UPropertyBool("south");
	public static UPropertyBool EAST   = new UPropertyBool("east");
	public static UPropertyBool WEST   = new UPropertyBool("west");
	
	public static Map<EnumFacing, UPropertyBool> faceToProperty = new HashMap<>();
	public static Map<EnumFacing, Integer> faceToIndex = new HashMap<>();
	
	public static UPropertyBool TAPE   = new UPropertyBool("tape");
	public static UPropertyBool LIGHTS = new UPropertyBool("lights");
	
	public BlockCatwalk() {
		super(Material.iron, "catwalk");
		if(faceToProperty.isEmpty()) {
			faceToProperty.put(EnumFacing.DOWN,  BOTTOM);
			faceToProperty.put(EnumFacing.NORTH, NORTH);
			faceToProperty.put(EnumFacing.SOUTH, SOUTH);
			faceToProperty.put(EnumFacing.EAST,  EAST);
			faceToProperty.put(EnumFacing.WEST,  WEST);
		}
		
		if(faceToIndex.isEmpty()) {
			faceToIndex.put(EnumFacing.DOWN,  I_BOTTOM);
			faceToIndex.put(EnumFacing.NORTH, I_NORTH);
			faceToIndex.put(EnumFacing.SOUTH, I_SOUTH);
			faceToIndex.put(EnumFacing.EAST,  I_EAST);
			faceToIndex.put(EnumFacing.WEST,  I_WEST);
			faceToIndex.put(EnumFacing.UP,    -1);
		}
	}

	@Override
	public int getMetaFromState(IBlockState state) { return 0; }
	@Override
	public IBlockState getStateFromMeta(int meta) { return getDefaultState(); }
	
	@Override
	public ExtendedData getData(World world, IBlockState state) {
		return null;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected BlockState createBlockState() {
		IProperty[] listedProperties = new IProperty[0]; // no listed properties
        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { BOTTOM, NORTH, SOUTH, WEST, EAST, TAPE, LIGHTS };
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	}
	
	int I_BOTTOM=0, I_NORTH=1, I_SOUTH=2, I_EAST=3, I_WEST=4, I_TAPE=5, I_LIGHTS=6;
	
	@Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		
		boolean pass = tile != null;
		
		return ((IExtendedBlockState)state)
				.withProperty(BOTTOM, pass && tile.getBoolean(I_BOTTOM))
				.withProperty(NORTH,  pass && tile.getBoolean(I_NORTH) )
				.withProperty(SOUTH,  pass && tile.getBoolean(I_SOUTH) )
				.withProperty(EAST,   pass && tile.getBoolean(I_EAST)  )
				.withProperty(WEST,   pass && tile.getBoolean(I_WEST)  )
				.withProperty(TAPE,   pass && tile.getBoolean(I_TAPE)  )
				.withProperty(LIGHTS, pass && tile.getBoolean(I_LIGHTS))
		;
	}
	
	public static String makeTexturePostfix(boolean tape, boolean lights) {
		String str = "";
		if(tape)   str += 't';
		if(lights) str += 'l';
		return str;
	}
	
	public boolean putDecoration(World world, BlockPos pos, String name, boolean value) {
		TileExtended tile = (TileExtended) world.getTileEntity(pos);
		
		if("lights".equals(name)) {
			if(tile.getBoolean(I_LIGHTS) == value) {
				return false;
			}
			tile.setBoolean(I_LIGHTS, value);
			return true;
		}
		if("tape".equals(name)) {
			if(tile.getBoolean(I_TAPE) == value) {
				return false;
			}
			tile.setBoolean(I_TAPE, value);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	public boolean isSideOpen(World world, BlockPos pos, EnumFacing side) {
		TileExtended tile = (TileExtended) world.getTileEntity(pos);
		return tile.getBoolean(faceToIndex.get(side));
	}
	
	public boolean isWide(World world, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if(playerIn.inventory.getCurrentItem() != null &&
				(
						playerIn.inventory.getCurrentItem().getItem() == ItemRegister.lights ||
						playerIn.inventory.getCurrentItem().getItem() == ItemRegister.tape

				)
			) {
			return false;
		}
		
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		int id = 0;
		
		switch (side) {
		case DOWN:
			id = I_BOTTOM;
			break;
		case NORTH:
			id = I_NORTH;
			break;
		case SOUTH:
			id = I_SOUTH;
			break;
		case EAST:
			id = I_EAST;
			break;
		case WEST:
			id = I_WEST;
			break;
		default:
			break;
		}
		
		if(playerIn.inventory.getCurrentItem() != null && playerIn.inventory.getCurrentItem().getItem() instanceof ItemBlock) {
			return false;
		}
		
		if(side != EnumFacing.UP ) {
			tile.setBoolean(id, !tile.getBoolean(id));
			tile.markDirty();
			worldIn.markBlockForUpdate(pos);
			return true;
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileExtended ourTile = (TileExtended) worldIn.getTileEntity(pos);
		
		for (EnumFacing direction : EnumFacing.VALUES) {
			ourTile.setBoolean(faceToIndex.get(direction), true);
		}
		
		for (EnumFacing direction : EnumFacing.HORIZONTALS) {
			if(worldIn.getBlockState(pos.offset(direction)).getBlock() == this) {
				TileExtended tile = (TileExtended) worldIn.getTileEntity(pos.offset(direction));
				ourTile.setBoolean(faceToIndex.get(direction), false);
				   tile.setBoolean(faceToIndex.get(direction.getOpposite()), false);
			}
			
		}
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}
	
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT_MIPPED;
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, EntityPlayer player, Vec3 start, Vec3 end) {
        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
//        boolean hasWrench = true;
    	
        AxisAlignedBB bounds = new AxisAlignedBB(pos, pos.add(1, 1, 1));
        double thickness = Float.MIN_VALUE;
        
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
        	Cuboid6 cuboid = new Cuboid6(bounds);
        	AABBUtils.offsetSide(cuboid, facing.getOpposite(), -(1-thickness));
        	
        	if( !state.getValue(faceToProperty.get(facing)) ) {
        		cuboid.max.y -= 0.5;
        	}
        	
        	cuboids.add(
    			new IndexedCuboid6(
    				facing,
    				cuboid
    			)
    		);
		}
        
        Cuboid6 cuboid = new Cuboid6(bounds);
    	AABBUtils.offsetSide(cuboid, EnumFacing.UP, -(1-thickness));
    	if(!state.getValue(BOTTOM)) {
    		cuboid.max.x -= 0.25;
    		cuboid.max.z -= 0.25;
    		cuboid.min.x += 0.25;
    		cuboid.min.z += 0.25;
    	}
    	cuboids.add(
			new IndexedCuboid6(
				EnumFacing.DOWN,
				cuboid
			)
		);
        
        List<ExtendedMOP> hits = Lists.newArrayList();
        RayTracer.instance().rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(pos), this, hits);
        ExtendedMOP mop = null;
        
        for (ExtendedMOP hit : hits) {
			if(mop == null || hit.dist < mop.dist)
				mop = hit;
		}
        
        if(mop == null)
        	return null;
        
    	if(mop.sideHit == ((EnumFacing)mop.hitInfo).getOpposite() && mop.sideHit.getAxis() != Axis.Y) {
    		mop.sideHit = (EnumFacing)mop.hitInfo;
    	}
        
        ExtendedFlatHighlightMOP flatmop = new ExtendedFlatHighlightMOP(mop);
        EnumFacing sideHit = (EnumFacing)mop.hitInfo;
        flatmop.side = sideHit;
        if( sideHit.getAxis() != Axis.Y && !state.getValue(faceToProperty.get(sideHit)) ) {
        	flatmop.top = 0.5;
        }
        if( sideHit == EnumFacing.DOWN && !state.getValue(BOTTOM) ) {
        	flatmop.top = flatmop.bottom = flatmop.left = flatmop.right = 0.25;
        }
        
        if( world.isSideSolid(pos.offset(sideHit), sideHit.getOpposite()) )
        	flatmop.sideDistance = 0.006;
        
        return flatmop;
    }
	
	@Override
	public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState rawState, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		
		boolean eNull = collidingEntity == null;
		
        IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		
		List<Cuboid6> cuboids = new ArrayList<>();
		
		AxisAlignedBB bounds = new AxisAlignedBB(pos, pos.add(1, 1, 1));
        double thickness = Float.MIN_VALUE;
        
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
        	if( !state.getValue(faceToProperty.get(facing)) ) {
        		continue;
        	}
        	Cuboid6 cuboid = new Cuboid6(bounds);
        	AABBUtils.offsetSide(cuboid, facing.getOpposite(), -(1-thickness));
        	
        	if(eNull || !collidingEntity.isSneaking())
        		cuboid.max.y += 0.5;
        	
//        	AABBUtils.offsetSide(cuboid, EnumFacing.UP, 0.5);
        	cuboids.add(
    			new IndexedCuboid6(
    				facing,
    				cuboid
    			)
    		);
		}
        
        if(state.getValue(BOTTOM)) {
	        Cuboid6 bottomCuboid = new Cuboid6(bounds);
	    	AABBUtils.offsetSide(bottomCuboid, EnumFacing.UP, -(1-thickness));
	    	cuboids.add(
				new IndexedCuboid6(
					EnumFacing.DOWN,
					bottomCuboid
				)
			);
        }
		
    	for (Cuboid6 cuboid : cuboids) {
			AxisAlignedBB aabb = cuboid.aabb();
			
			if(aabb.intersectsWith(mask)) {
				list.add(aabb);
			}
		}
    	
//		super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
	}
}
