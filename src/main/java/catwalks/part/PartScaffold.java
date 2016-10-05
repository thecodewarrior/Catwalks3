package catwalks.part;

import catwalks.Const;
import catwalks.EnumCatwalkMaterial;
import catwalks.block.EnumCatwalkMaterialOld;
import catwalks.register.ItemRegister;
import catwalks.util.meta.ArrayProp;
import catwalks.util.meta.IDirtyable;
import catwalks.util.meta.MetaStorage;
import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.ISolidPart;
import mcmultipart.multipart.Multipart;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheCodeWarrior
 */
public class PartScaffold extends Multipart implements ISolidPart, IDirtyable {
	public static final String ID = Const.MODID + ":scaffold";
	
	protected List<AxisAlignedBB> selectionBoxes = new ArrayList<>();
	
	protected static MetaStorage.Allocator allocator = new MetaStorage.Allocator();
	public static ArrayProp<EnumCatwalkMaterial> MATERIAL = allocator.allocateArray("material", EnumCatwalkMaterial.values(), 7);
	
	protected MetaStorage storage = new MetaStorage(allocator, this);
	
	public PartScaffold() {
		double p = 1/16f, P = 1-p;
		double t = 4/16f, T = 1-t;
		selectionBoxes.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1));
	}
	
	public void setCatwalkMaterial(EnumCatwalkMaterial value) {
		if(value != getCatwalkMaterial())
			markDirty();
		MATERIAL.set(storage, value);
	}
	public EnumCatwalkMaterial getCatwalkMaterial() {
		return MATERIAL.get(storage);
	}
	
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
	
	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) {
		return layer == getCatwalkMaterial().LAYER;
	}
	
	//region Here be blockstates
	
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(MCMultiPartMod.multipart, Const.MATERIAL_META);
	}
	
	@Override
	public IBlockState getActualState(IBlockState state) {
		return state.withProperty(Const.MATERIAL_META, MATERIAL.get(storage));
	}
	
	//endregion
	
	@Override
	public boolean isSideSolid(EnumFacing side) {
		return true;
	}
	
	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.addAll(selectionBoxes);
	}
	
	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		AxisAlignedBB axisalignedbb = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
		
		if (mask.intersectsWith(axisalignedbb))
		{
			list.add(axisalignedbb);
		}
	}
	
	@Override
	public boolean occlusionTest(IMultipart part) {
		return super.occlusionTest(part) || part instanceof PartScaffold;
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
	}
	
	@Override
	public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
		return new ItemStack(ItemRegister.scaffold, 1, getCatwalkMaterial().ordinal());
	}
}
