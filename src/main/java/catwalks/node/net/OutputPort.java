package catwalks.node.net;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import catwalks.node.EntityNodeBase;
import catwalks.node.NodeBase;
import catwalks.shade.ccl.math.MathHelper;
import catwalks.util.Logs;
import io.netty.buffer.ByteBuf;

public abstract class OutputPort<T> {

	public List<Vec3d> connectedLocs;
	
	public final Class<T> type;
	List<PortConnection<T>> connections;
	T value;
	boolean modified;
	NodeBase node;
	
	public OutputPort(Class<T> type, T value, NodeBase node) {
		this.type = type;
		this.value = value;
		this.node = node;
		connections = new ArrayList<>();
		connectedLocs = new ArrayList<>();
	}
	
	public void setValue(T value) {
		if(this.value == value) return;
		if(this.value != null && this.value.equals(value)) return;
		if(value != null && value.equals(this.value)) return;
		this.value = value;
		this.modified = true;
	}
	
	public int getColor() {
		return 0x7f7f7f;
	}
	
	public T getValue() {
		return value;
	}
	
	public boolean isModified() {
		return modified;
	}
	
	public void resetModified() {
		modified = false;
	}
	
	public boolean updateConnected(World world) {
		boolean update = false;
		for (PortConnection<T> connection : connections) {
			update = connection.updateConnected(world, value) || update;
		}
		update = connections.removeIf((con) -> con.isInvalid) || update;
		if(update) {
			connectedLocs.clear();
		}
		return update;
	}
	
	public void connectTo(EntityNodeBase entity, int index) {
		if( connections.removeIf((con) -> {
			return entity.getPersistentID().equals(con.connectedUUID);
		}) ) {
			return;
		}
		PortConnection<T> con = new PortConnection<>(entity.getPersistentID(), index, entity.worldObj, this.type);
		connections.add(con);
		con.updateConnected(entity.worldObj, value);
	}
	
	public List<Vec3d> connectedPoints() {
		if(connectedLocs.size() != connections.size()) {
			List<Vec3d> points = new ArrayList<>();
			
			for (PortConnection<T> connection : connections) {
				if(connection.connectedLoc != null)
					points.add(connection.connectedLoc);
			}
			connectedLocs = points;
		}
		return connectedLocs;
	}
	
	public abstract void readValueFromBuf(ByteBuf buf);
	public abstract void writeValueToBuf(ByteBuf buf);
	
	public void readFromBuf(ByteBuf buf) {
		readValueFromBuf(buf);
		int count = buf.readInt(); 
		connections = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			connections.add(PortConnection.readFromBuf(buf, node.entity.worldObj, type));
		}
	}
	
	public void writeToBuf(ByteBuf buf) {
		writeValueToBuf(buf);
		buf.writeInt(connections.size());
		for (PortConnection<T> connection : connections) {
			connection.writeToBuf(buf);
		}
	}
	
	
	public static class PortConnection<T> {
		public Class<T> type;
		public boolean isInvalid;
		public Vec3d connectedLoc;
		public WeakReference<InputPort<T>> port = new WeakReference<InputPort<T>>(null);
		public WeakReference<NodeBase> node = new WeakReference<NodeBase>(null);
		
		public int connectedIndex;
		public UUID connectedUUID;
		
		public PortConnection(UUID uuid, int index, World world, Class<T> type) {
			this.connectedUUID = uuid;
			this.connectedIndex = index;
			tryConnect(world);
		}
		
		public boolean updateConnected(World world, T value) {
			boolean didChange = false;
			if(port.get() == null) {
				didChange = tryConnect(world);
			}
			checkValidity(world);
			if(node.get() != null) {
				Vec3d loc = node.get().entity.getPositionVector();
				double SMALL = 0.0001;
				if(!(
						MathHelper.between(-SMALL, loc.xCoord - connectedLoc.xCoord, SMALL) &&
						MathHelper.between(-SMALL, loc.yCoord - connectedLoc.yCoord, SMALL) &&
						MathHelper.between(-SMALL, loc.zCoord - connectedLoc.zCoord, SMALL)
					)) {
					connectedLoc = loc;
					didChange = true;
				}
			}
			if(port.get() != null) {
				port.get().setValue(value);
			}
			return didChange;
		}
		
		@SuppressWarnings("unchecked")
		public boolean tryConnect(World world) {
			for(Entity e : world.loadedEntityList) {
				if(e.getPersistentID().equals(connectedUUID) && e instanceof EntityNodeBase) {
					EntityNodeBase entity = (EntityNodeBase) e;
					InputPort<?> otherPort = entity.getNode().inputs().get(connectedIndex);
					node = new WeakReference<NodeBase>(entity.getNode());
					port = new WeakReference<InputPort<T>>((InputPort<T>) otherPort);
					connectedLoc = entity.getPositionVector();
					return true;
				}
			}
			return false;
		}
		
		public void checkValidity(World world) {
			if(!world.isBlockLoaded(new BlockPos(connectedLoc)))
				return;
			
			if(connectedLoc != null && port.get() == null) {
				isInvalid = true;
			}
			if(node.get() != null && node.get().entity != null && node.get().entity.isDead) {
				isInvalid = true;
			}
		}
		
		public void writeToBuf(ByteBuf buf) {
			buf.writeLong(connectedUUID.getMostSignificantBits());
			buf.writeLong(connectedUUID.getLeastSignificantBits());
			buf.writeInt(connectedIndex);
			buf.writeFloat((float) connectedLoc.xCoord);
			buf.writeFloat((float) connectedLoc.yCoord);
			buf.writeFloat((float) connectedLoc.zCoord);
		}
		
		public static <T> PortConnection<T> readFromBuf(ByteBuf buf, World world, Class<T> type) {
			UUID uuid = new UUID(buf.readLong(), buf.readLong());
			int index = buf.readInt();
			Vec3d vec = new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat());
			PortConnection<T> con = new PortConnection<>(uuid, index, world, type);
			con.connectedLoc = vec;
			return con;
		}
	}
}
