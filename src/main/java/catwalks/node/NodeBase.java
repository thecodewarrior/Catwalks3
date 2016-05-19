package catwalks.node;

import java.util.List;

import com.google.common.collect.ImmutableList;

import catwalks.node.net.OutputPort;
import net.minecraft.nbt.NBTTagCompound;

public class NodeBase {

	protected EntityNodeBase entity;
	
	public NodeBase(EntityNodeBase entity) {
		this.entity = entity;
	}
	
	/**
	 * Returns a list of output ports, order <i>must</i> be the same on server and client
	 */
	public List<OutputPort> outputs() { return ImmutableList.of(); }
	
	public int getColor() { return 0xFFFFFF; }
	
	public void serverTick() {}
	public void clientTick() {}
	public void onFirstTick() {}
	public void onLoad() {}
	
	public void saveToNBT(NBTTagCompound tag) {}
	public void readFromNBT(NBTTagCompound tag) {}
	
}
