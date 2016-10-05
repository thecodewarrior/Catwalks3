package catwalks.part;

import catwalks.CatwalksMod;
import catwalks.Const;
import catwalks.EnumCatwalkMaterial;
import catwalks.block.EnumCatwalkMaterialOld;
import catwalks.block.EnumDecoration;
import catwalks.part.data.StairSides;
import catwalks.util.meta.*;
import mcmultipart.multipart.Multipart;
import net.minecraft.util.EnumFacing;

/**
 * Created by TheCodeWarrior
 */
public class PartStair extends Multipart implements IDirtyable, IDecoratable {
	public static final String ID = Const.MODID + ":stair";
	
	protected static MetaStorage.Allocator allocator = new MetaStorage.Allocator();
	
	public static ArrayProp<EnumCatwalkMaterial> MATERIAL = CatwalksMod.allocate_material(allocator);
	public static BoolArrayProp DECOR = CatwalksMod.allocate_decor(allocator);
	
	public static ArrayProp<EnumFacing> FACING = allocator.allocateArray("facing", EnumFacing.HORIZONTALS, 2);
	public static BoolMapProp<StairSides> SIDES = allocator.allocateBoolMap("sides", StairSides.values(), StairSides.values().length);
	
	protected MetaStorage storage = new MetaStorage(allocator, this);
	
	public PartStair() {
		super();
	}
	
	//region api stuff
	
	public EnumCatwalkMaterial getCatwalkMaterial() {
		return MATERIAL.get(storage);
	}
	public void setCatwalkMaterial(EnumCatwalkMaterial value) {
		MATERIAL.set(storage, value);
	}
	
	@Override
	public boolean addDecoration(EnumDecoration decor) {
		EnumCatwalkMaterial mat = getCatwalkMaterial();
		int id = mat.getID(decor);
		if(id < 0)
			return false;
		if(DECOR.get(storage, id))
			return false;
		DECOR.set(storage, id, true);
		return true;
	}
	
	@Override
	public boolean removeDecoration(EnumDecoration decor) {
		EnumCatwalkMaterial mat = getCatwalkMaterial();
		int id = mat.getID(decor);
		if(id < 0)
			return false;
		if(DECOR.get(storage, id))
			return false;
		DECOR.set(storage, id, true);
		return true;
	}
	
	@Override
	public boolean hasDecoration(EnumDecoration decor) {
		EnumCatwalkMaterial mat = getCatwalkMaterial();
		int id = mat.getID(decor);
		if(id < 0)
			return false;
		if(DECOR.get(storage, id))
			return false;
		DECOR.set(storage, id, true);
		return true;
	}
	
	public EnumFacing getFacing() {
		return FACING.get(storage);
	}
	public void setFacing(EnumFacing value) {
		FACING.set(storage, value);
	}
	
	public boolean getSide(StairSides side) {
		return SIDES.get(storage, side);
	}
	public void setSide(StairSides side, boolean value) {
		SIDES.set(storage, side, value);
	}
	
	//endregion
	
	@Override
	public void markDirty() {
		super.markDirty();
	}
}
