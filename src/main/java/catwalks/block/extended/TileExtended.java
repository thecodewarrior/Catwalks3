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
	
	{ /* getters/setters */ }
	
	public boolean getBoolean(int id) {
		if(id < 0) return false;
		return ( meta & (0x1 << id) ) != 0;
	}
	
	public void setBoolean(int id, boolean bool) {
		int oldLight = worldObj.getBlockState(pos).getBlock().getLightValue(worldObj, pos);
		
		if(id < 0) return;
		if(bool) {
			meta = meta | (1 << id);
		} else {
			meta = meta & ~(1 << id);
		}
		
		this.worldObj.markBlockForUpdate(pos);
		
		if(worldObj.getBlockState(pos).getBlock().getLightValue(worldObj, pos) != oldLight)
			this.worldObj.checkLight(pos);
		this.markDirty();
	}
	
	/**
	 * UNTESTED
	 */
	public int getNumber(int id, int len) {
		long lenMask = (1 << len);   // 0x0000000000100000
		lenMask -= 1;                // 0x0000000000011111
		lenMask = lenMask << id;     // 0x0000000001111100
		long value = meta & lenMask; // 0x000000000xxxxx00
		value = value >> id;         // 0x00000000000xxxxx
		return (int)value;
	}
	
	/**
	 * UNTESTED
	 */
	public void setNumber(int id, int len, int val) {
		long lenMask = (1 << len);   //m 0x0000000000100000
		lenMask -= 1;                //m 0x0000000000011111
		lenMask = lenMask << id;     //m 0x0000000001111100
		lenMask = ~lenMask;          //m 0x1111111110000011
		meta = meta & lenMask;       //d 0xddddddddd00000dd
		lenMask = ~lenMask;          //m 0x0000000001111100
		long value = val;            //# 0xtttttttttttxxxxx // t=trash, x=data
		value = value << id;         //# 0xtttttttttxxxxx00
		value = value & lenMask;     //# 0x000000000xxxxx00
		meta = meta | value;         //d 0xdddddddddxxxxxdd
	}
	
	{ /* normal Tile Entity stuff */ }
	
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
