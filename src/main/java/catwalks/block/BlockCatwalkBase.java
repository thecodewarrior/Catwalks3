package catwalks.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import catwalks.CatwalksMod;
import catwalks.Const;
import catwalks.block.extended.BlockExtended;
import catwalks.block.extended.TileExtended;
import catwalks.block.property.UPropertyBool;
import catwalks.proxy.ClientProxy;
import catwalks.register.ItemRegister;
import catwalks.shade.ccl.util.Copyable;
import catwalks.shade.ccl.vec.BlockCoord;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import catwalks.util.ExtendedFlatHighlightMOP;
import catwalks.util.GeneralUtil;
import catwalks.util.Logs;
import catwalks.util.Trimap;
import catwalks.util.WrenchChecker;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockCatwalkBase extends BlockExtended implements ICatwalkConnect, IDecoratable {
	
	public BlockCatwalkBase(Material material, String name) {
		super(material, name);
		init();
	}
	
	public BlockCatwalkBase(Material materialIn, String name, Class<?> clazz) {
		super(materialIn, name, clazz);
		init();
	}
	
	public void init() {
		IExtendedBlockState state = (IExtendedBlockState) this.blockState.getBaseState();
		this.setDefaultState(state.withProperty(Const.FACING, EnumFacing.NORTH));
		initSides();
		initColllisionBoxes();
	}
	
	public static Trimap<UPropertyBool, EnumFacing, Integer> sides = new Trimap<>(UPropertyBool.class, EnumFacing.class, Integer.class);
	static int I_BOTTOM=0, I_TOP=1, I_NORTH=2, I_SOUTH=3, I_EAST=4, I_WEST=5,  I_FACING_ID=6, I_FACING_LEN=3, I_TAPE=10, I_SPEED=12; // lights was 11

	static {
		sides.put(Const.BOTTOM, EnumFacing.DOWN,  I_BOTTOM);
		sides.put(Const.TOP,    EnumFacing.UP,    I_TOP   );
		sides.put(Const.NORTH,  EnumFacing.NORTH, I_NORTH );
		sides.put(Const.SOUTH,  EnumFacing.SOUTH, I_SOUTH );
		sides.put(Const.EAST,   EnumFacing.EAST,  I_EAST  );
		sides.put(Const.WEST,   EnumFacing.WEST,  I_WEST  );
	}
	
	protected static int I_BASE_LEN=24;
	
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
		
		if(side != EnumFacing.UP ) {
			side = transformAffectedSide(worldIn, pos, state, side);
			int id = sides.getC(side);
			tile.setBoolean(id, !tile.getBoolean(id));
			tile.markDirty();
			worldIn.markBlockForUpdate(pos);
			return true;
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
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
			if(tile.getBoolean(I_TAPE) == value) {
				return false;
			}
			tile.setBoolean(I_TAPE, value);
			return true;
		}
		if("speed".equals(name)) {
			if(tile.getBoolean(I_SPEED) == value) {
				return false;
			}
			tile.setBoolean(I_SPEED, value);
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
			return tile.getBoolean(I_TAPE);
		}
		if("speed".equals(name)) {
			return tile.getBoolean(I_SPEED);
		}
		return false;
	}
	
	{ /* state */ }
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		
		boolean pass = tile != null;
		
		IExtendedBlockState estate = ((IExtendedBlockState)state)
				.withProperty(Const.BOTTOM, pass && tile.getBoolean(I_BOTTOM))
				.withProperty(Const.NORTH,  pass && tile.getBoolean(I_NORTH) )
				.withProperty(Const.SOUTH,  pass && tile.getBoolean(I_SOUTH) )
				.withProperty(Const.EAST,   pass && tile.getBoolean(I_EAST)  )
				.withProperty(Const.WEST,   pass && tile.getBoolean(I_WEST)  )
				.withProperty(Const.TAPE,   pass && tile.getBoolean(I_TAPE)  )
				.withProperty(Const.SPEED,  pass && tile.getBoolean(I_SPEED) )
				.withProperty(Const.FACING, pass ? EnumFacing.VALUES[tile.getNumber(I_FACING_ID, I_FACING_LEN)] : EnumFacing.UP);
		
		if(tile != null) {
			estate = addProperties(tile, estate);
		}
		
		return estate;
	}
	
	public IExtendedBlockState getBasicExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		
		boolean pass = tile != null;
		
		IExtendedBlockState estate = ((IExtendedBlockState)state)
				.withProperty(Const.BOTTOM, pass && tile.getBoolean(I_BOTTOM))
				.withProperty(Const.NORTH,  pass && tile.getBoolean(I_NORTH) )
				.withProperty(Const.SOUTH,  pass && tile.getBoolean(I_SOUTH) )
				.withProperty(Const.EAST,   pass && tile.getBoolean(I_EAST)  )
				.withProperty(Const.WEST,   pass && tile.getBoolean(I_WEST)  )
				.withProperty(Const.TAPE,   pass && tile.getBoolean(I_TAPE)  )
				.withProperty(Const.SPEED,  pass && tile.getBoolean(I_SPEED) )
				.withProperty(Const.FACING, pass ? EnumFacing.VALUES[tile.getNumber(I_FACING_ID, I_FACING_LEN)] : EnumFacing.UP);
		
		return estate;
	}
	
	public IExtendedBlockState addProperties(TileExtended tile, @Nonnull IExtendedBlockState state) {
		return state;
	}
	
	@SuppressWarnings("rawtypes")
	public void addAdditionalProperties(List<IUnlistedProperty> list) {
		
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected BlockState createBlockState() {
		IProperty[] listedProperties = new IProperty[] { Const.MATERIAL, Const.LIGHTS };
	    List<IUnlistedProperty> unlistedProperties = new ArrayList<IUnlistedProperty>();
	    unlistedProperties.addAll(Lists.asList(Const.BOTTOM, new IUnlistedProperty[] { Const.NORTH, Const.SOUTH, Const.WEST, Const.EAST, Const.FACING, Const.TAPE, Const.SPEED }));
	    addAdditionalProperties(unlistedProperties);
	    
	    return new ExtendedBlockState(this, listedProperties, unlistedProperties.toArray(new IUnlistedProperty[0]));
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
		// TODO Auto-generated method stub
		return super.getPickBlock(target, world, pos, player);
	}
	
	{ /* rendering */ }
	
	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos) {
		return world.getBlockState(pos).getValue(Const.LIGHTS) ? 15 : 0;
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
		return EnumWorldBlockLayer.CUTOUT;
	}
	
	{ /* placing/breaking */ }

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		
		IExtendedBlockState estate = (IExtendedBlockState) getExtendedState(state, worldIn, pos);
		List<ItemStack> drops = new ArrayList<>();
		if(estate.getValue(Const.TAPE)) {
			drops.add(new ItemStack(ItemRegister.tape, 1, ItemRegister.tape.getMaxDamage() - 1));
		}
		if(estate.getValue(Const.LIGHTS)) {
			drops.add(new ItemStack(ItemRegister.lights, 1, ItemRegister.lights.getMaxDamage() - 1));
		}
		if(estate.getValue(Const.SPEED)) {
			drops.add(new ItemStack(ItemRegister.speed, 1, ItemRegister.speed.getMaxDamage() - 1));
		}
		
		for (ItemStack stack : drops) {
			GeneralUtil.spawnItemStack(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ	()+0.5, stack);
		}
		
		GeneralUtil.updateSurroundingCatwalkBlocks(worldIn, pos);
		
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileExtended ourTile = (TileExtended) worldIn.getTileEntity(pos);
		
		ourTile.setNumber(I_FACING_ID, I_FACING_LEN, placer.getHorizontalFacing().ordinal());
		
		for (EnumFacing direction : EnumFacing.VALUES) {
			ourTile.setBoolean(sides.getC(direction), true);
		}
		
		GeneralUtil.updateSurroundingCatwalkBlocks(worldIn, pos);
		
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	{ /* meta */ }
	
	@Override
	public int damageDropped(IBlockState state) {
	    return state.getValue(Const.MATERIAL).ordinal();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
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
	    return this.getDefaultState().withProperty(Const.MATERIAL, EnumCatwalkMaterial.values()[ meta & 0b0111 ]).withProperty(Const.LIGHTS, ( meta & 0b1000 ) == 1 );
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
	    return state.getValue(Const.MATERIAL).ordinal() | ( state.getValue(Const.LIGHTS) ? 8 : 0 );
	}
	
	{ /* collision */ }
	
	public abstract void initColllisionBoxes();
	public abstract List<CollisionBox> getCollisionBoxes(IExtendedBlockState state, World world, BlockPos pos);
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		return new AxisAlignedBB(pos, pos);
	}
	
	@Override
	public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState rawState, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		
		AxisAlignedBB originMask = mask.offset(-pos.getX(), -pos.getY(), -pos.getZ());
		
		boolean eNull = collidingEntity == null;
		
		
		IBlockState plainState = world.getBlockState(pos);
        IExtendedBlockState state = (IExtendedBlockState) getExtendedState(plainState, world, pos);
		
        if(CatwalksMod.developmentEnvironment) initColllisionBoxes();
		List<CollisionBox> boxes = getCollisionBoxes(state, world, pos);
		
		if(boxes == null) {
			Logs.error("ERROR: Collision box list null!");
		}
		
    	for (CollisionBox box : boxes) {
    		if(!state.getValue(box.enableProperty))
    			continue;
			Cuboid6 cuboid = (eNull || !collidingEntity.isSneaking()) ? box.normal : box.sneak;
			
			if(cuboid.aabb().intersectsWith(originMask)) {
				list.add(cuboid.aabb().offset(pos.getX(), pos.getY(), pos.getZ()));
			}
		}
	}
	
	{ /* raytracing */}
	
	public abstract void initSides();
	
	public abstract List<LookSide> lookSides(IExtendedBlockState state, World world, BlockPos pos);
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, EntityPlayer player, Vec3 startRaw, Vec3 endRaw) {
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		boolean hasWrench = player.inventory.getCurrentItem() != null && WrenchChecker.isAWrench(player.inventory.getCurrentItem().getItem());
		
		BlockCoord hitPos = new BlockCoord(pos);
		
		Vector3
			blockPosVec = new Vector3(pos),
			start = new Vector3(startRaw).sub(blockPosVec),
			end   = new Vector3(  endRaw).sub(blockPosVec);
		
		if(CatwalksMod.developmentEnvironment) initSides();
		List<LookSide> sides = lookSides(state, world, pos);
		if(sides == null) {
			return null;
		}
		
		LookSide hitSide = null;
		Vector3 hitVector = null;
		double smallestDistanceSq = Double.POSITIVE_INFINITY;
		ClientProxy.hits.clear();
		for (LookSide side : sides) {
			Face quad = null;
			if(side.showProperty == null || side.showProperty == Const.CONST_TRUE || (side.showProperty != Const.CONST_FALSE && state.getValue(side.showProperty))) {
				quad = side.mainSide;
			} else if(side.showWithoutWrench || hasWrench) {
				quad = side.wrenchSide;
			}
			if(quad == null)
				continue;
			
			start.x -= side.offset.getX();
			start.y -= side.offset.getY();
			start.z -= side.offset.getZ();
			
			end.x   -= side.offset.getX();
			end.y   -= side.offset.getY();
			end.z   -= side.offset.getZ();
			
			Tri[] tris = quad.tris();
			for (Tri tri : tris) {
				Vector3 vec = GeneralUtil.intersectRayTri(start.copy(), end.copy(), tri);
				if(vec != null) {
					Vector3 sub = vec.copy().sub(start);
					double distanceSq = sub.magSquared();
					if(distanceSq < 4.1 && distanceSq > 3.9) {
						distanceSq = distanceSq -1 +1;
					}
					ClientProxy.hits.add(new Tuple<Vector3, Double>(vec.copy().add(blockPosVec).add(new Vector3(side.offset)), Math.sqrt( distanceSq )));
					if(distanceSq < smallestDistanceSq) {
						hitSide = side;
						hitVector = vec.copy().add(blockPosVec);
						smallestDistanceSq = distanceSq;
					}
				}
			}
			
			start.x += side.offset.getX();
			start.y += side.offset.getY();
			start.z += side.offset.getZ();
			
			end.x   += side.offset.getX();
			end.y   += side.offset.getY();
			end.z   += side.offset.getZ();
		}
		
		if(hitSide == null)
			return null;
		
		Face quad = null;
		
		if(hitSide.showProperty == null || hitSide.showProperty == Const.CONST_TRUE || (hitSide.showProperty != Const.CONST_FALSE && state.getValue(hitSide.showProperty))) {
			quad = hitSide.mainSide;
		} else if(hitSide.showWithoutWrench || hasWrench) {
			quad = hitSide.wrenchSide;
		}
		
		hitPos.x += hitSide.offset.getX();
		hitPos.y += hitSide.offset.getY();
		hitPos.z += hitSide.offset.getZ();
		
		ExtendedFlatHighlightMOP mop = new ExtendedFlatHighlightMOP(quad, hitVector,
						( hitSide.side == null ? EnumFacing.UP : hitSide.side ).ordinal(),
						hitPos, null,
						Math.sqrt(smallestDistanceSq)
				) ;

		
		
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
	
	public static class LookSide {
		public Face mainSide, wrenchSide;
		public UPropertyBool showProperty;
		public boolean showWithoutWrench;
		public EnumFacing side;
		public BlockPos offset;
		
		public LookSide() {
			this.offset = new BlockPos(0,0,0);
		}
		
		public LookSide(Quad mainSide, Quad wrenchSide, EnumFacing side, UPropertyBool showProperty, boolean showWithoutWrench) {
			this.mainSide = mainSide;
			this.wrenchSide = wrenchSide;
			this.showProperty = showProperty;
			this.showWithoutWrench = showWithoutWrench;
			this.side = side;
			this.offset = new BlockPos(0,0,0);
		}
		
		public void apply(Matrix4 matrix) {
			mainSide.apply(matrix);
			wrenchSide.apply(matrix);
		}
		
		public LookSide copy() {
			LookSide side = new LookSide();
			
			side.mainSide   = mainSide.copy();
			side.wrenchSide = wrenchSide.copy();
			side.showProperty = showProperty;
			side.showWithoutWrench = showWithoutWrench;
			side.side = this.side;
			side.offset = this.offset;
			return side;
		}
		
		@Override
		public String toString() {
			return showProperty.getName() + " => " + mainSide.toString() + " | " + wrenchSide.toString() + " @ " + side.getName();
		}
		
	}
	
	public abstract static class Face implements Copyable<Face> {
		public abstract void apply(Matrix4 matrix);
		public abstract void rotate(int rotation);
		public abstract void rotateCenter(int rotation);
		public abstract Tri[] tris();
		public abstract Vector3[] points();
	}
	
	public static class Quad extends Face {
		public Vector3 v1, v2, v3, v4;
		
		public Quad(Vector3 v1, Vector3 v2, Vector3 v3, Vector3 v4) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
			this.v4 = v4;
		}
		
		public void apply(Matrix4 matix) {
			v1.apply(matix);
			v2.apply(matix);
			v3.apply(matix);
			v4.apply(matix);
		}
		
		public void rotate(int rotation) {
			GeneralUtil.rotateVector(rotation, v1);
			GeneralUtil.rotateVector(rotation, v2);
			GeneralUtil.rotateVector(rotation, v3);
			GeneralUtil.rotateVector(rotation, v4);
		}
		
		public void rotateCenter(int rotation) {
			GeneralUtil.rotateVectorCenter(rotation, v1);
			GeneralUtil.rotateVectorCenter(rotation, v2);
			GeneralUtil.rotateVectorCenter(rotation, v3);
			GeneralUtil.rotateVectorCenter(rotation, v4);
		}
		
		public Quad copy() {
			return new Quad(v1.copy(), v2.copy(), v3.copy(), v4.copy());
		}
		
		public Tri[] tris() {
			Tri[] tris = new Tri[2];
			tris[0] = new Tri(v1, v2, v3);
			tris[1] = new Tri(v1, v3, v4);
			return tris;
		}
		
		@Override
		public Vector3[] points() {
			return new Vector3[] {
				v1, v2, v3, v4
			};
		}
		
		@Override
		public String toString() {
			return "(" + v1.toString() + ", " + v2.toString() + ", " + v3.toString() + ", " + v4.toString() + ")";
		}
	}
	
	public static class Tri extends Face {
		public Vector3 v1, v2, v3;
		public Tri(Vector3 v1, Vector3 v2, Vector3 v3) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}
		
		public void apply(Matrix4 matix) {
			v1.apply(matix);
			v2.apply(matix);
			v3.apply(matix);
		}
		
		public void rotate(int rotation) {
			GeneralUtil.rotateVector(rotation, v1);
			GeneralUtil.rotateVector(rotation, v2);
			GeneralUtil.rotateVector(rotation, v3);
		}
		
		public void rotateCenter(int rotation) {
			GeneralUtil.rotateVectorCenter(rotation, v1);
			GeneralUtil.rotateVectorCenter(rotation, v2);
			GeneralUtil.rotateVectorCenter(rotation, v3);
		}
		
		public Tri copy() {
			return new Tri(v1.copy(), v2.copy(), v3.copy());
		}
		
		@Override
		public Tri[] tris() {
			return new Tri[] { this };
		}
		
		@Override
		public Vector3[] points() {
			return new Vector3[] {
				v1, v2, v3
			};
		}
		
		@Override
		public String toString() {
			return "(" + v1.toString() + ", " + v2.toString() + ", " + v3.toString() + ")";
		}

		
	}
}
