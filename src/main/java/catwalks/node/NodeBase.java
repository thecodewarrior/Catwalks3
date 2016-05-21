package catwalks.node;

import java.util.List;

import net.minecraftforge.fml.common.network.ByteBufUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.ImmutableList;

import catwalks.network.NetworkHandler;
import catwalks.node.net.InputPort;
import catwalks.node.net.OutputPort;
import io.netty.buffer.ByteBuf;

public class NodeBase {
	
	public final EntityNodeBase entity;
	
	public NodeBase(EntityNodeBase entity) {
		this.entity = entity;
	}
	
	public void openGUI(NBTTagCompound tag) {
		if(entity.worldObj.isRemote) {
			Minecraft.getMinecraft().displayGuiScreen(createGui(tag));
		}
	}
	
	public GuiScreen createGui(NBTTagCompound tag) {
		return null;
	}
	
	public void updateSettings(NBTTagCompound tag) {
		
	}
	public NBTTagCompound getSettings() {
		return new NBTTagCompound();
	}
	
	/**
	 * Returns a list of output ports, order <i>must</i> be the same on server and client
	 */
	public List<OutputPort> outputs() { return ImmutableList.of(); }
	
	/**
	 * Returns a list of input ports, order <i>must</i> be the same on server and client
	 */
	public List<InputPort> inputs() { return ImmutableList.of(); }
	
	public int getColor() { return 0xFFFFFF; }
	
	public void serverTick() {}
	public void clientTick() {}
	public void onFirstTick() {}
	public void onLoad() {}
	
	public void saveToNBT(NBTTagCompound tag) {
		NBTTagList outputs = new NBTTagList();
		NBTTagList inputs = new NBTTagList();
		ByteBuf buf = NetworkHandler.createBuffer();
		
		for (OutputPort port : outputs()) {
			port.writeToBuf(buf);
			byte[] arr = new byte[buf.writerIndex()];
			buf.getBytes(0, arr);
			outputs.appendTag(new NBTTagByteArray(arr));
			buf.clear();
		}
		
		for (InputPort port : inputs()) {
			port.writeToBuf(buf);
			byte[] arr = new byte[buf.writerIndex()];
			buf.getBytes(0, arr);
			inputs.appendTag(new NBTTagByteArray(arr));
			buf.clear();
		}
		
		tag.setTag("outputPorts", outputs);
		tag.setTag("inputPorts",   inputs);
		
		tag.setTag("settings", getSettings());
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList outputs = tag.getTagList("outputPorts", 7);
		NBTTagList inputs = tag.getTagList("inputPorts", 7);
		ByteBuf buf = NetworkHandler.createBuffer();
		
		int i = 0;
		for (OutputPort port : outputs()) {
			byte[] arr = ((NBTTagByteArray)outputs.get(i) ).getByteArray();
			buf.writeBytes(arr);
			port.readFromBuf(buf);
			buf.clear();
			i++;
		}
		
		i = 0;
		for (InputPort port : inputs()) {
			byte[] arr = ((NBTTagByteArray)inputs.get(i) ).getByteArray();
			buf.writeBytes(arr);
			port.readFromBuf(buf);
			buf.clear();
			i++;
		}
		
		updateSettings(tag.getCompoundTag("settings"));
	}
	
	public void writeSyncData(ByteBuf buf) {
		for (OutputPort port : outputs()) {
			port.writeToBuf(buf);
		}
		
		for (InputPort port : inputs()) {
			port.writeToBuf(buf);
		}
		
		ByteBufUtils.writeTag(buf, getSettings());
	}
	
	public void readSyncData(ByteBuf buf) {
		for (OutputPort port : outputs()) {
			port.readFromBuf(buf);
		}
		
		for (InputPort port : inputs()) {
			port.readFromBuf(buf);
		}
		
		updateSettings(ByteBufUtils.readTag(buf));
	}
}
