package catwalks.block.extended;

import java.util.BitSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import catwalks.util.GeneralUtil;

public class TileExtended extends TileEntity {

	public BitSet meta = new BitSet();
	
	public TileExtended() {}
	
	{ /* getters/setters */ }
	
	public boolean getBoolean(int id) {
		if(id < 0) return false;
		return meta.get(id);
	}
	
	public void setBoolean(int id, boolean bool) {
		int oldLight = worldObj.getBlockState(pos).getBlock().getLightValue(worldObj.getBlockState(pos), worldObj, pos);
		
		if(id < 0) return;
		
		meta.set(id, bool);
		
		GeneralUtil.markForUpdate(worldObj, pos);
		
		if(worldObj.getBlockState(pos).getBlock().getLightValue(worldObj.getBlockState(pos), worldObj, pos) != oldLight)
			this.worldObj.checkLight(pos);
		this.markDirty();
	}
	
	/**
	 * UNTESTED
	 */
	public int getNumber(int id, int len) {
		return GeneralUtil.getNum(meta.get(id, id+len));
	}
	
	/**
	 * UNTESTED
	 */
	public void setNumber(int id, int len, int val) {
		meta.clear(id, id+len);
		meta.or(GeneralUtil.getSet(val, id));
	}
	
	{ /* normal Tile Entity stuff */ }
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setByteArray("m", meta.toByteArray());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		byte[] array = compound.getByteArray("m");
		meta = BitSet.valueOf(array);
	}
	
	@Override
	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 0, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
		GeneralUtil.markForUpdate(worldObj, pos);
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
}
