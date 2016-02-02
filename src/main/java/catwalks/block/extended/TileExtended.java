package catwalks.block.extended;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileExtended extends TileEntity {

	public long meta;
	public ExtendedData data; // unused for now
	
	public TileExtended() {}
	
	public boolean getBoolean(int id) {
		if(id < 0) return false;
		return ( meta & (0x1 << id) ) != 0;
	}
	
	public void setBoolean(int id, boolean bool) {
		if(id < 0) return;
		if(bool) {
			meta = meta | (1 << id);
		} else {
			meta = meta & ~(1 << id);
		}
		this.markDirty();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setLong("m", meta);
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		if(data != null) {
			data.write(buf);
		}
		compound.setByteArray("d", buf.array());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		meta = compound.getLong("m");
		byte[] array = compound.getByteArray("d");
		PacketBuffer buf = new PacketBuffer(Unpooled.wrappedBuffer(array));
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
	}
	
}
