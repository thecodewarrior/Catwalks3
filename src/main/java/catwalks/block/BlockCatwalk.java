package catwalks.block;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import catwalks.CatwalksMod;
import catwalks.block.extended.BlockExtended;
import catwalks.block.extended.ExtendedData;
import catwalks.block.extended.TileExtended;
import catwalks.block.property.UPropertyBool;
import catwalks.item.ItemBlockCatwalk;
import catwalks.register.ItemRegister;
import catwalks.shade.ccl.raytracer.ExtendedMOP;
import catwalks.shade.ccl.raytracer.IndexedCuboid6;
import catwalks.shade.ccl.raytracer.RayTracer;
import catwalks.shade.ccl.vec.BlockCoord;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.AABBUtils;
import catwalks.util.ExtendedFlatHighlightMOP;
import catwalks.util.GeneralUtil;
import catwalks.util.Trimap;
import catwalks.util.WrenchChecker;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCatwalk extends BlockExtended implements ICatwalkConnect {

	public static UPropertyBool BOTTOM = new UPropertyBool("bottom");
	public static UPropertyBool TOP    = new UPropertyBool("top");
	public static UPropertyBool NORTH  = new UPropertyBool("north");
	public static UPropertyBool SOUTH  = new UPropertyBool("south");
	public static UPropertyBool EAST   = new UPropertyBool("east");
	public static UPropertyBool WEST   = new UPropertyBool("west");
	
	public static UPropertyBool TAPE   = new UPropertyBool("tape");
	public static UPropertyBool LIGHTS = new UPropertyBool("lights");
	
	int I_BOTTOM=0, I_TOP=1, I_NORTH=2, I_SOUTH=3, I_EAST=4, I_WEST=16+0, I_TAPE=16+1, I_LIGHTS=16+2;

	public static Trimap<UPropertyBool, EnumFacing, Integer> sides = new Trimap<>(UPropertyBool.class, EnumFacing.class, Integer.class);
	
//	public static Map<EnumFacing, UPropertyBool> faceToProperty = new HashMap<>();
//	public static Map<EnumFacing, Integer> faceToIndex = new HashMap<>();
	
	public static PropertyEnum<EnumCatwalkMaterial> MATERIAL = PropertyEnum.create("material", EnumCatwalkMaterial.class);
	
	public static enum EnumCatwalkMaterial implements IStringSerializable {
		STEEL, STONE, WOOD, CUSTOM1, CUSTOM2, CUSTOM3;

		@Override
		public String getName() {
			return this.name();
		}
	}
	
	public BlockCatwalk() {
		super(Material.iron, "catwalk", ItemBlockCatwalk.class);
		setHardness(1.5f);
		
		sides.put(BOTTOM, EnumFacing.DOWN,  I_BOTTOM);
		sides.put(TOP,    EnumFacing.UP,    I_TOP   );
		sides.put(NORTH,  EnumFacing.NORTH, I_NORTH );
		sides.put(SOUTH,  EnumFacing.SOUTH, I_SOUTH );
		sides.put(EAST,   EnumFacing.EAST,  I_EAST  );
		sides.put(WEST,   EnumFacing.WEST,  I_WEST  );
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

	public static String makeTextureGenName(String type, EnumCatwalkMaterial material, boolean tape, boolean lights) {
		String str = CatwalksMod.MODID + ":/gen/catwalk_" + type + "_";
		str += material.getName().toLowerCase() + "_";
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
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		
		for (EnumFacing direction : EnumFacing.HORIZONTALS) {
			if(worldIn.getBlockState(pos.offset(direction)).getBlock() == this) {
				TileExtended tile = (TileExtended) worldIn.getTileEntity(pos.offset(direction));
				if(tile.getBoolean(sides.getC(direction.getOpposite())) == false) {
					tile.setBoolean(sides.getC(direction.getOpposite()), true);
				}
			}
			
		}
		
		IExtendedBlockState estate = (IExtendedBlockState) getExtendedState(state, worldIn, pos);
		List<ItemStack> drops = new ArrayList<>();
		if(estate.getValue(TAPE)) {
			drops.add(new ItemStack(ItemRegister.tape, 1, ItemRegister.tape.getMaxDamage() - 1));
		}
		if(estate.getValue(LIGHTS)) {
			drops.add(new ItemStack(ItemRegister.lights, 1, ItemRegister.lights.getMaxDamage() - 1));
		}
		
		for (ItemStack stack : drops) {
			GeneralUtil.spawnItemStack(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ	()+0.5, stack);
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

	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, EntityPlayer player, Vec3 start, Vec3 end) {
        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
//        boolean hasWrench = true;
    	
        AxisAlignedBB bounds = new AxisAlignedBB(pos, pos.add(1, 1, 1));
        double thickness = Float.MIN_VALUE;
        
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
        	boolean exists = state.getValue(sides.getA(facing));
        	boolean nextBlockIsCatalk = world.getBlockState(pos.offset(facing)).getBlock() == this;
        	if(!exists && nextBlockIsCatalk) {
                IExtendedBlockState nextState = (IExtendedBlockState) getExtendedState(world.getBlockState(pos.offset(facing)), world, pos.offset(facing));
                if( nextState.getValue(sides.getA(facing.getOpposite())) ) {
                	continue;
                }
        	}
        	Cuboid6 cuboid = new Cuboid6(bounds);
        	AABBUtils.offsetSide(cuboid, facing.getOpposite(), -(1-thickness));
        	
        	if( !exists ) {
        		cuboid.max.y -= 0.5;
        	}
        	
        	if( exists || ( player.inventory.getCurrentItem() != null && WrenchChecker.isAWrench(player.inventory.getCurrentItem().getItem()) ))
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
        if( sideHit.getAxis() != Axis.Y && !state.getValue(sides.getA(sideHit)) ) {
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
	        	if( !state.getValue(sides.getA(facing)) ) {
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

	{ /* meta */ }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
	{
	    for (EnumCatwalkMaterial enumdyecolor : EnumCatwalkMaterial.values())
	    {
	        list.add(new ItemStack(itemIn, 1, enumdyecolor.ordinal()));
	    }
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
	    return this.getDefaultState().withProperty(MATERIAL, EnumCatwalkMaterial.values()[ meta ]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
	    return state.getValue(MATERIAL).ordinal();
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected BlockState createBlockState() {
		IProperty[] listedProperties = new IProperty[] { MATERIAL }; // no listed properties
	    IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { BOTTOM, NORTH, SOUTH, WEST, EAST, TAPE, LIGHTS };
	    return new ExtendedBlockState(this, listedProperties, unlistedProperties);
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
