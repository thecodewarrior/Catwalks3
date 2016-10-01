package catwalks.part;

import catwalks.Const;
import catwalks.block.EnumCatwalkMaterial;
import catwalks.block.EnumDecoration;
import catwalks.part.data.CatwalkRenderData;
import catwalks.register.ItemRegister;
import catwalks.util.GeneralUtil;
import catwalks.util.NeighborCache;
import catwalks.util.meta.ArrayProp;
import catwalks.util.meta.BoolMapProp;
import catwalks.util.meta.IDirtyable;
import catwalks.util.meta.MetaStorage;
import mcmultipart.MCMultiPartMod;
import mcmultipart.client.multipart.IFastMSRPart;
import mcmultipart.multipart.*;
import mcmultipart.raytrace.PartMOP;
import mcmultipart.raytrace.RayTraceUtils;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static catwalks.util.meta.MetaStorage.bits;

/**
 * Created by TheCodeWarrior
 */
public class PartCatwalk extends Multipart implements ISlottedPart, INormallyOccludingPart, ISolidPart, IDirtyable, IFastMSRPart {
	public static final String ID = Const.MODID + ":catwalk";
	public static final ThreadLocal<NeighborCache<PartCatwalk>> caches = new ThreadLocal<NeighborCache<PartCatwalk>>() {
		@Override
		protected NeighborCache<PartCatwalk> initialValue() {
			return new NeighborCache<>();
		}
	};
	
	@SuppressWarnings("unchecked")
	public static List<AxisAlignedBB> sideBoxes = Arrays.asList(
		new AxisAlignedBB(0, 0, 0, /**/ 1, 0, 1), // down
		new AxisAlignedBB(0, 0, 0, /**/ 0, 0, 0), // up
		new AxisAlignedBB(0, 0, 0, /**/ 1, 1, 0), // north
		new AxisAlignedBB(0, 0, 1, /**/ 1, 1, 1), // south
		new AxisAlignedBB(0, 0, 0, /**/ 0, 1, 1), // west
		new AxisAlignedBB(1, 0, 0, /**/ 1, 1, 1)  // east
	);
	
	public static EnumFacing[] SIDE_LIST = new EnumFacing[]{EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
	
	protected static MetaStorage.Allocator allocator = new MetaStorage.Allocator();
	public static ArrayProp<EnumCatwalkMaterial> MATERIAL = allocator.allocateArray("material", EnumCatwalkMaterial.values(), bits(128));
	public static BoolMapProp<EnumFacing> SIDES = allocator.allocateBoolMap("sides", EnumFacing.values(), 6);
	public static BoolMapProp<EnumDecoration> DECOR = allocator.allocateBoolMap("decor", EnumDecoration.values(), 15);
	
	protected MetaStorage storage = new MetaStorage(allocator, this);
	
	//region click stuff
	
	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		if (heldItem != null && heldItem.getItem() == ItemRegister.tool) {
			setSide(hit.sideHit, !getSide(hit.sideHit));
			return true;
		}
		return false;
	}
	
	
	//endregion
	
	//region api stuff
	public void setCatwalkMaterial(EnumCatwalkMaterial value) {
		MATERIAL.set(storage, value);
	}
	
	public EnumCatwalkMaterial getCatwalkMaterial() {
		return MATERIAL.get(storage);
	}
	
	public boolean getSide(EnumFacing side) {
		return SIDES.get(storage, side);
	}
	
	public void setSide(EnumFacing side, boolean value) {
		SIDES.set(storage, side, value);
	}
	//endregion
	
	//region collision and raytrace stuff
	
	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		for (EnumFacing facing : EnumFacing.values()) {
			if (facing == EnumFacing.UP)
				continue;
			if (SIDES.get(storage, facing)) {
				AxisAlignedBB box = sideBoxes.get(facing.ordinal());
				if (mask.intersectsWith(box))
					list.add(box);
			}
		}
	}
	
	@Override
	public RayTraceUtils.AdvancedRayTraceResultPart collisionRayTrace(Vec3d start, Vec3d end) {
		List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		addSelectionBoxes(list);
		RayTraceUtils.AdvancedRayTraceResult result = RayTraceUtils.collisionRayTrace(getWorld(), getPos(), start, end, list);
		
		if (result == null)
			return null;
		
		Vec3d hit = result.hit.hitVec.subtract(new Vec3d(getPos()));
		EnumFacing sideHit = result.hit.sideHit;
		if (hit.yCoord == 0) {
			sideHit = EnumFacing.DOWN;
		} else if (hit.yCoord == 1) {
			sideHit = EnumFacing.UP;
		} else if (hit.zCoord == 0) {
			sideHit = EnumFacing.NORTH;
		} else if (hit.zCoord == 1) {
			sideHit = EnumFacing.SOUTH;
		} else if (hit.xCoord == 0) {
			sideHit = EnumFacing.WEST;
		} else if (hit.xCoord == 1) {
			sideHit = EnumFacing.EAST;
		}
		result.hit.sideHit = sideHit;
		
		return new RayTraceUtils.AdvancedRayTraceResultPart(result, this);
	}
	
	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.addAll(sideBoxes);
	}
	
	//endregion
	
	//region mcmultipart stuff
	
	@Override
	public void onAdded() {
		super.onAdded();
		IMultipartContainer container = getContainer();
		for (EnumFacing facing : EnumFacing.values()) {
			if (facing == EnumFacing.UP)
				continue;
			if (OcclusionHelper.slotOcclusionTest(PartSlot.getFaceSlot(facing), container))
				SIDES.set(storage, facing, true);
		}
		onPlaceUpdateSides();
	}
	
	@Override
	public void onRemoved() {
		onBreakUpdateSides();
	}
	
	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		for (EnumFacing facing : EnumFacing.values()) {
			if (facing == EnumFacing.UP)
				continue;
			if (SIDES.get(storage, facing))
				list.add(sideBoxes.get(facing.ordinal()));
		}
	}
	
	@Override
	public EnumSet<PartSlot> getSlotMask() {
		EnumSet<PartSlot> slots = EnumSet.noneOf(PartSlot.class);
		for (EnumFacing facing : EnumFacing.values()) {
			if (facing == EnumFacing.UP)
				continue;
			if (SIDES.get(storage, facing))
				slots.add(PartSlot.getFaceSlot(facing));
		}
		return slots;
	}
	
	@Override
	public boolean isSideSolid(EnumFacing side) {
		return SIDES.get(storage, side);
	}
	
	@Override
	public boolean occlusionTest(IMultipart part) {
		return super.occlusionTest(part) && !(part instanceof PartCatwalk);
	}
	
	// endregion
	
	//region nbt and packet stuff
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagCompound sup = super.writeToNBT(tag);
		sup.setByteArray("m", storage.toByteArray());
		return sup;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		storage.fromByteArray(tag.getByteArray("m"));
	}
	
	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		super.writeUpdatePacket(buf);
		buf.writeByteArray(storage.toByteArray());
	}
	
	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		super.readUpdatePacket(buf);
		storage.fromByteArray(buf.readByteArray());
	}
	//endregion
	
	//region model
	
	@Override
	public BlockStateContainer createBlockState() {
		return new ExtendedBlockState(MCMultiPartMod.multipart, new IProperty[] {
			Const.MATERIAL_META
		}, new IUnlistedProperty[]{
			Const.CATWALK_RENDER_DATA
		});
	}
	
	@Override
	public IBlockState getActualState(IBlockState state) {
		return state.withProperty(Const.MATERIAL_META, MATERIAL.get(storage));
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state) {
		IExtendedBlockState estate = (IExtendedBlockState) state;
		CatwalkRenderData renderData = new CatwalkRenderData();
		
		NeighborCache<PartCatwalk> cache = caches.get();
		cache.init(getPos(), (pos) -> GeneralUtil.getPart(PartCatwalk.class, getWorld(), pos));
		
		for (EnumFacing d : EnumFacing.HORIZONTALS) {
			if (!getSide(d))
				continue;
			
			EnumFacing right = d.rotateY();
			EnumFacing left = d.rotateYCCW();
			
			CatwalkRenderData.CatwalkSideRenderData data = new CatwalkRenderData.CatwalkSideRenderData();
			renderData.sides.put(d, data);
			
			data.right = sideLogic(cache, d, right);
			data.left = sideLogic(cache, d, left);
		}
		
		renderData.corner_ne = cornerLogic(cache, EnumFacing.NORTH, EnumFacing.EAST);
		renderData.corner_nw = cornerLogic(cache, EnumFacing.NORTH, EnumFacing.WEST);
		renderData.corner_se = cornerLogic(cache, EnumFacing.SOUTH, EnumFacing.EAST);
		renderData.corner_sw = cornerLogic(cache, EnumFacing.SOUTH, EnumFacing.WEST);
		
		renderData.bottom = getSide(EnumFacing.DOWN);
		
		cache.clear();
		return estate.withProperty(Const.CATWALK_RENDER_DATA, renderData);
	}
	
	private CatwalkRenderData.EnumCatwalkCornerType cornerLogic(NeighborCache<PartCatwalk> cache, EnumFacing front, EnumFacing side) {
		
		if (getSide(front) || getSide(side))
			return null;
		
		PartCatwalk ahead = cache.get(front);
		PartCatwalk adjacent = cache.get(side);
		
		if (ahead == null || adjacent == null)
			return null;
		
		if (adjacent.getSide(front) && !adjacent.getSide(side.getOpposite()) &&
			!ahead.getSide(front.getOpposite()) && ahead.getSide(side)
			) {
			return CatwalkRenderData.EnumCatwalkCornerType.CORNER;
		}
		
		PartCatwalk diagonal = cache.get(side, front);
		
		if(diagonal != null) {
			if (!adjacent.getSide(side.getOpposite()) && !adjacent.getSide(front) &&
				!diagonal.getSide(front.getOpposite()) && diagonal.getSide(side.getOpposite()) &&
				!ahead.getSide(front.getOpposite()) && ahead.getSide(side)
				) {
				return CatwalkRenderData.EnumCatwalkCornerType.CORNER_180;
			}
			
			if (!adjacent.getSide(side.getOpposite()) && adjacent.getSide(front) &&
				!ahead.getSide(side) && !ahead.getSide(front.getOpposite()) &&
				diagonal.getSide(front.getOpposite()) && !diagonal.getSide(side.getOpposite())
				) {
				return CatwalkRenderData.EnumCatwalkCornerType.CORNER_180;
			}
		}
		return null;
	}
	
	private CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType sideLogic(NeighborCache<PartCatwalk> cache, EnumFacing front, EnumFacing side) {
		if (getSide(side)) {
			return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.INNER_CORNER;
		}
		
		PartCatwalk adjacent = cache.get(side);
		PartCatwalk diagonal = null;
		
		if (adjacent != null) {
			if (
				adjacent.getSide(front) && !adjacent.getSide(side.getOpposite())
				) {
				return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.MERGE;
			}
			diagonal = cache.get(front, side); // moved here for efficiency
			// corner end logic
			if (diagonal != null) {
				if (
					!adjacent.getSide(front) && !adjacent.getSide(side.getOpposite()) &&
						diagonal.getSide(side.getOpposite()) && !diagonal.getSide(front.getOpposite())
					) {
					return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.OUTER_CORNER;
				}
			}
		}
		
		PartCatwalk ahead = cache.get(front);
		
		// 180° wrap around end logic
		if(adjacent != null && diagonal != null && ahead != null) {
			if(ahead.getSide(front.getOpposite()) && !ahead.getSide(side) &&
				!adjacent.getSide(side.getOpposite()) && !adjacent.getSide(front) &&
				!diagonal.getSide(front.getOpposite()) && !diagonal.getSide(side.getOpposite())
				) {
				return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.OUTER_CORNER_180;
			}
		}
		return CatwalkRenderData.CatwalkSideRenderData.EnumCatwalkEndRenderType.END;
	}
	
	//endregion
	
	//region auto-open stuff
	
	public void onPlaceUpdateSides() {
		for (EnumFacing f : EnumFacing.HORIZONTALS) {
			PartCatwalk part = GeneralUtil.getPart(PartCatwalk.class, getWorld(), getPos().offset(f));
			
			setSide(f, part == null);
			if (part != null)
				part.setSide(f.getOpposite(), false);
		}
	}
	
	public void onBreakUpdateSides() {
		for (EnumFacing f : EnumFacing.HORIZONTALS) {
			PartCatwalk part = GeneralUtil.getPart(PartCatwalk.class, getWorld(), getPos().offset(f));
			if (part != null)
				part.setSide(f.getOpposite(), true);
		}
	}
	
	//endregion
	
	@Override
	public void markDirty() {
		super.markDirty();
		sendUpdatePacket(true);
	}
	
	@Override
	public boolean hasFastRenderer() {
		return true;
	}
}
