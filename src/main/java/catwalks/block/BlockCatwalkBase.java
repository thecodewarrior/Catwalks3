package catwalks.block;

import java.util.*;
import java.util.function.Function;

import catwalks.block.extended.tileprops.ArrayProp;
import catwalks.block.extended.tileprops.BoolProp;
import catwalks.block.extended.tileprops.ExtendedTileProperties;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import catwalks.Const;
import catwalks.block.extended.BlockExtended;
import catwalks.block.extended.ITileStateProvider;
import catwalks.block.extended.tileprops.TileExtended;
import catwalks.block.property.UPropertyBool;
import catwalks.raytrace.RayTraceUtil;
import catwalks.raytrace.RayTraceUtil.IRenderableFace;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.RayTraceUtil.ITraceable;
import catwalks.raytrace.RayTraceUtil.VertexList;
import catwalks.register.ItemRegister;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.util.CustomFaceRayTraceResult;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import catwalks.util.Trimap;
import catwalks.util.WrenchChecker;

import static com.sun.tools.javac.jvm.ByteCodes.ret;

public abstract class BlockCatwalkBase extends BlockExtended implements ICatwalkConnect, IDecoratable, ITileStateProvider {
	
	public BiMap<EnumFacing, BoolProp> sideProps = HashBiMap.create();
	public BiMap<EnumFacing, UPropertyBool> sideState = HashBiMap.create();
	
	public BoolProp BOTTOM, TOP, NORTH, SOUTH, EAST, WEST;
	public BoolProp TAPE, SPEED;
	public BoolProp DECOR_1, DECOR_2, DECOR_3, DECOR_4, DECOR_5, DECOR_6, DECOR_7; // a max of 10 decorations
	public ArrayProp<EnumFacing> FACING;
	public ArrayProp<EnumCatwalkMaterial> MATERIAL;
	
	public BlockCatwalkBase(Material material, String name) {
		super(material, name);
		init();
	}
	
	public BlockCatwalkBase(Material materialIn, String name, Function<Block, ItemBlock> item) {
		super(materialIn, name, item);
		init();
	}
	
	public void init() {
//		IBlockState state = this.blockState.getBaseState();
//		this.setDefaultState(state.withProperty(Const.FACING, EnumFacing.NORTH));
		
		MATERIAL = allocator.allocateArray(EnumCatwalkMaterial.values(), 16);
		
		sideState.put(EnumFacing.DOWN, Const.BOTTOM);
		sideState.put(EnumFacing.UP, Const.TOP);
		sideState.put(EnumFacing.NORTH, Const.NORTH);
		sideState.put(EnumFacing.SOUTH, Const.SOUTH);
		sideState.put(EnumFacing.EAST, Const.EAST);
		sideState.put(EnumFacing.WEST, Const.WEST);
		
		sideProps.put(EnumFacing.DOWN, BOTTOM = allocator.allocateBool());
		sideProps.put(EnumFacing.UP, TOP = allocator.allocateBool());
		sideProps.put(EnumFacing.NORTH, NORTH = allocator.allocateBool());
		sideProps.put(EnumFacing.SOUTH, SOUTH = allocator.allocateBool());
		sideProps.put(EnumFacing.EAST, EAST = allocator.allocateBool());
		sideProps.put(EnumFacing.WEST, WEST = allocator.allocateBool());
		
		TAPE = allocator.allocateBool();
		SPEED = allocator.allocateBool();
		
		DECOR_1 = allocator.allocateBool();
		DECOR_2 = allocator.allocateBool();
		DECOR_3 = allocator.allocateBool();
		DECOR_4 = allocator.allocateBool();
		DECOR_5 = allocator.allocateBool();
		DECOR_6 = allocator.allocateBool();
		DECOR_7 = allocator.allocateBool();
		
		FACING = allocator.allocateArray(EnumFacing.VALUES);
		
		initSides();
		initColllisionBoxes();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		if( heldItem != null) {
			if(!WrenchChecker.isAWrench( heldItem.getItem() ))
				return false;
			if(playerIn.inventory.getCurrentItem().getItem() instanceof ItemBlock)
				return false;
		} else {
			return false;
		}
		
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		
		if(side != EnumFacing.UP ) {
			side = transformAffectedSide(worldIn, pos, state, side);
			BoolProp prop = sideProps.get(side);
			prop.set(tile, !prop.get(tile));
			tile.markDirty();
			GeneralUtil.markForUpdate(worldIn, pos);
			return true;
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}
	
	public EnumFacing transformAffectedSide(World world, BlockPos pos, IBlockState state, EnumFacing side) {	
		return side;
	}
	
	@Override
	public boolean putDecoration(World world, BlockPos pos, String name, boolean value) {
		TileExtended tile = (TileExtended) world.getTileEntity(pos);
		IBlockState state = world.getBlockState(pos);
		
		if("lights".equals(name)) {
			if(state.getValue(Const.LIGHTS) == value) {
				return false;
			}
			world.setBlockState(pos, state.withProperty(Const.LIGHTS, value));
			return true;
		}
		if("tape".equals(name)) {
			if(TAPE.get(tile) == value) {
				return false;
			}
			TAPE.set(tile, value);
			return true;
		}
		if("speed".equals(name)) {
			if(SPEED.get(tile) == value) {
				return false;
			}
			SPEED.set(tile, value);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasDecoration(World world, BlockPos pos, String name) {
		TileExtended tile = (TileExtended) world.getTileEntity(pos);
		IBlockState state = world.getBlockState(pos);
		
		if("lights".equals(name)) {
			return state.getValue(Const.LIGHTS);
		}
		if("tape".equals(name)) {
			return TAPE.get(tile);
		}
		if("speed".equals(name)) {
			return SPEED.get(tile);
		}
		return false;
	}
	
	{ /* state */ }
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		
		IExtendedBlockState estate = getTileState(state, worldIn, pos);
		
		if(tile != null) {
			estate = setRenderProperties(tile, estate);
		}
		
		return estate;
	}
	
	@Override
	public IExtendedBlockState getTileState(IBlockState rawstate, IBlockAccess worldIn, BlockPos pos) {
		
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		
		IExtendedBlockState state = (IExtendedBlockState)rawstate;
		
		if(tile == null)
			return state;
		
		state = state
				.withProperty(Const.MATERIAL, MATERIAL.get(tile))
				.withProperty(Const.BOTTOM, BOTTOM.get(tile))
				.withProperty(Const.NORTH,  NORTH.get(tile) )
				.withProperty(Const.SOUTH,  SOUTH.get(tile) )
				.withProperty(Const.EAST,   EAST.get(tile)  )
				.withProperty(Const.WEST,   WEST.get(tile)  )
				.withProperty(Const.TAPE,   TAPE.get(tile)  )
				.withProperty(Const.SPEED,  SPEED.get(tile) )
				.withProperty(Const.FACING, FACING.get(tile));
		
		state = setFunctionalProperties(tile, state);
		
		return state;
	}
	
	public IExtendedBlockState setRenderProperties(TileExtended tile, @Nonnull IExtendedBlockState state) {
		return state;
	}
	
	public IExtendedBlockState setFunctionalProperties(TileExtended tile, @Nonnull IExtendedBlockState state) {
		return state;
	}
	
	@SuppressWarnings("rawtypes")
	public void addFunctionalProperties(List<IUnlistedProperty> list) {}
	
	@SuppressWarnings("rawtypes")
	public void addRenderOnlyProperties(List<IUnlistedProperty> list) {};
	
	@Override
	@SuppressWarnings("rawtypes")
	protected BlockStateContainer createBlockState() {
		List<IUnlistedProperty> unlistedProperties = new ArrayList<>();
		unlistedProperties.addAll(Arrays.asList(
			Const.MATERIAL, Const.FACING, Const.TAPE, Const.SPEED,
			Const.BOTTOM, Const.TOP, Const.NORTH, Const.SOUTH, Const.WEST, Const.EAST
		));
		addFunctionalProperties(unlistedProperties);

		addRenderOnlyProperties(unlistedProperties);
		
	    ExtendedBlockState state = new ExtendedBlockState(this, new IProperty[] {Const.LIGHTS}, unlistedProperties.toArray(new IUnlistedProperty[0]));
	    
	    return state;
	}
	
	{ /* rendering */ }
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(Const.LIGHTS) ? 15 : 0;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	{ /* placing/breaking */ }

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState rawState) {
		
		IExtendedBlockState state = getTileState(rawState, worldIn, pos);
		List<ItemStack> drops = new ArrayList<>();
		if(state.getValue(Const.TAPE)) {
			drops.add(new ItemStack(ItemRegister.tape, 1, ItemRegister.tape.getMaxDamage() - 1));
		}
		if(state.getValue(Const.LIGHTS)) {
			drops.add(new ItemStack(ItemRegister.lights, 1, ItemRegister.lights.getMaxDamage() - 1));
		}
		if(state.getValue(Const.SPEED)) {
			drops.add(new ItemStack(ItemRegister.speed, 1, ItemRegister.speed.getMaxDamage() - 1));
		}
		
		for (ItemStack stack : drops) {
			GeneralUtil.spawnItemStack(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ	()+0.5, stack);
		}
		
		GeneralUtil.updateSurroundingCatwalkBlocks(worldIn, pos);
		
		super.breakBlock(worldIn, pos, rawState);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileExtended ourTile = (TileExtended) worldIn.getTileEntity(pos);
		
		FACING.set(ourTile, placer.getHorizontalFacing());
		
		for (BoolProp prop : sideProps.values()) {
			prop.set(ourTile, true);
		}
		
		GeneralUtil.updateSurroundingCatwalkBlocks(worldIn, pos);
		
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	{ /* meta */ }
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		IExtendedBlockState estate = (IExtendedBlockState) getExtendedState(state, world, pos);
		
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		
		Random rand = world instanceof World ? ((World)world).rand : RANDOM;
		
		int count = quantityDropped(state, fortune, rand);
		for(int i = 0; i < count; i++)
		{
			Item item = this.getItemDroppedExtended(estate, rand, fortune);
			if (item != null)
			{
				ret.add(new ItemStack(item, 1, this.damageDroppedExtended(estate)));
			}
		}
		return ret;
	}
	
	public int damageDroppedExtended(IExtendedBlockState state) {
	    return state.getValue(Const.MATERIAL).ordinal();
	}

	public Item getItemDroppedExtended(IExtendedBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}
	
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
	    return this.getDefaultState().withProperty(Const.LIGHTS, ( meta & 0b0001 ) == 1 );
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
	    return state.getValue(Const.LIGHTS) ? 1 : 0;
	}
	
	{ /* collision */ }
	
	public abstract void initColllisionBoxes();
	public abstract List<CollisionBox> getCollisionBoxes(IBlockState state, World world, BlockPos pos);
	
	@Override
	public void addCollisionBoxToList(IBlockState blockState, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if(collidingEntity == null)
			return;
		AxisAlignedBB originMask = mask.offset(-pos.getX(), -pos.getY(), -pos.getZ());
		
		boolean eNull = collidingEntity == null;
		
		IExtendedBlockState state = getTileState(world.getBlockState(pos), world, pos);
		
        if(Const.developmentEnvironment) initColllisionBoxes();
		List<CollisionBox> boxes = getCollisionBoxes(state, world, pos);
		
		if(boxes == null) {
			Logs.error("ERROR: Collision box list null!");
		}
		
    	for (CollisionBox box : boxes) {
    		boolean const_true  = box.enableProperty == Const.CONST_TRUE;
    		boolean const_false = box.enableProperty == Const.CONST_FALSE;
    		
    		if(!const_true && ( const_false || !state.getValue(box.enableProperty) ))
    			continue;
			Cuboid6 cuboid = (eNull || !collidingEntity.isSneaking()) ? box.normal : box.sneak;
			
			if(cuboid.aabb().intersectsWith(originMask)) {
				list.add(cuboid.aabb().offset(pos.getX(), pos.getY(), pos.getZ()));
			}
		}
	}
	
	{ /* raytracing */}
	
	public abstract void initSides();
	
	public abstract List<? extends ITraceable<BlockTraceParam, BlockTraceResult> > lookSides(IBlockState state, World world, BlockPos pos);
	

	public static class BlockTraceResult {
		public BlockPos offset;
		public EnumFacing side;
		public BlockTraceResult(BlockPos offset, EnumFacing side) {
			this.offset = offset; this.side = side;
		}
//		public IWireframe frame;
	}
	
	public static class BlockTraceParam {
		public EntityPlayer player;
		public IExtendedBlockState state;
		public boolean wrench;
		public BlockTraceParam(EntityPlayer player, IExtendedBlockState state, boolean wrench) {
			this.player = player; this.state = state; this.wrench = wrench;
		}
	}
	
	@Override
	public RayTraceResult collisionRayTrace(IBlockState baseState, World world, BlockPos pos, EntityPlayer player, Vec3d startRaw, Vec3d endRaw) {
		IExtendedBlockState state = getTileState(world.getBlockState(pos), world, pos);
		boolean hasWrench = player.inventory.getCurrentItem() != null && WrenchChecker.isAWrench(player.inventory.getCurrentItem().getItem());
		
		Vec3d posVec = new Vec3d(pos), start = startRaw.subtract(posVec), end = endRaw.subtract(posVec);
		
		if(Const.developmentEnvironment) initSides();
		List<? extends ITraceable<BlockTraceParam, BlockTraceResult> > sides = lookSides(state, world, pos);
		if(sides == null) {
			return null;
		}
		
		ITraceResult<BlockTraceResult> hitSide = RayTraceUtil.trace(start, end, sides, new BlockTraceParam(player, state, hasWrench));
		
		if(hitSide.hitDistance() == Double.POSITIVE_INFINITY)
			return null;
		
		IRenderableFace face = null;
		if(hitSide instanceof IRenderableFace) {
			face = (IRenderableFace) hitSide;
		} else {
			face = new IRenderableFace() {
				
				@Override
				public List<VertexList> getVertices() {
					return ImmutableList.of();
				}
			};
		}
		
		BlockPos actualPos = pos.add(hitSide.data().offset.getX(), hitSide.data().offset.getY(), hitSide.data().offset.getZ());
		
		CustomFaceRayTraceResult mop = new CustomFaceRayTraceResult(
					hitSide.hitPoint().add(posVec),
					hitSide.data().side,
					actualPos,
					hitSide.data().offset
				).face(face) ;
		
		return mop;
	}
	
	{ /* internal classes */ }
	
	public static class CollisionBox {
		
		public Cuboid6 normal, sneak;
		public UPropertyBool enableProperty;
		
		public void apply(Matrix4 matrix) {
			normal.apply(matrix);
			sneak.apply(matrix);
		}
		
		@Override
		public String toString() {
			return enableProperty.getName() + " => " + normal.toString() + " | " + sneak.toString();
		}
		
		public CollisionBox copy() {
			CollisionBox box = new CollisionBox();
			box.normal = normal.copy();
			box.sneak  = sneak.copy();
			box.enableProperty = enableProperty;
			return box;
		}
		
	}
}
