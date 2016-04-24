package catwalks.node;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

public abstract class Node<T> {

	public NodeWorld world;
	public Vec3 pos;
	protected UUID nodeID;
	
	public UUID connection;
	protected Node connectedCache;
	
	protected float rotX, rotY;
	protected T value;
	
	
	
	public Node(NodeWorld world) {
		this.world = world;
	}
	
	public UUID getUUID() {
		return nodeID;
	}
	
	public abstract void saveToNBT(NBTTagCompound tag);
	public abstract void loadFromNBT(NBTTagCompound tag);
	
	public abstract Class<T> getType();

	//-------------------------------------------------------------------------
	
	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		if(this.rotX != rotX)
			rotationChanged();
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		if(this.rotY != rotY)
			rotationChanged();
		this.rotY = rotY;
	}
	
	public abstract void rotationChanged();
	
	//-------------------------------------------------------------------------
	
	public void setValue(T value) {
		if(this.value != value) {
			valueChanged();
			updateConnectedValue();
		}
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public abstract void valueChanged();
	
	public void updateConnectedValue() {
		if(connectedCache == null)
			return;
		
		connectedCache.setValue(this.getValue());
	}
	
	//-------------------------------------------------------------------------
	
	public void connectionChanged() {
		if(connection == null)
			connectedCache = null;
		else
			connectedCache = world.getByUUID(connection);
	}
	
	public void onOtherLoad(UUID uuid, Node node) {
		if(uuid.equals(connection)) {
			connectedCache = node;
			updateConnectedValue();
		}
	}
	
	public abstract void onLoad();
	public abstract void tick();
	// clienttick
	// onactivate(player)
	// onhit(player)
	// onbreak()
	// onplace()
	// onmove
}
