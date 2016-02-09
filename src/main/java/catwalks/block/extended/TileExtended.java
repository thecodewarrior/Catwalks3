package catwalks.block.extended;

import java.util.BitSet;

import catwalks.util.GeneralUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileExtended extends TileEntity {

	public BitSet meta;
	public ExtendedData data; // unused for now
	
	public TileExtended() {}
	
	{ /* getters/setters */ }
	
	public boolean getBoolean(int id) {
		if(id < 0) return false;
		return meta.get(id);
	}
	
	public void setBoolean(int id, boolean bool) {
		int oldLight = worldObj.getBlockState(pos).getBlock().getLightValue(worldObj, pos);
		
		if(id < 0) return;
		
		meta.set(id, bool);
		
		this.worldObj.markBlockForUpdate(pos);
		
		if(worldObj.getBlockState(pos).getBlock().getLightValue(worldObj, pos) != oldLight)
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
//		compound.setLong("m", meta);
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeByteArray(meta.toByteArray());
		if(data != null) {
			data.write(buf);
		}
		compound.setByteArray("d", buf.array());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		byte[] array = compound.getByteArray("d");
		PacketBuffer buf = new PacketBuffer(Unpooled.wrappedBuffer(array));
		meta = BitSet.valueOf(array);
		if(data != null) {
			data.read(buf);
		}
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(pos, 0, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
		this.worldObj.markBlockForUpdate(pos);
	}
	
}
