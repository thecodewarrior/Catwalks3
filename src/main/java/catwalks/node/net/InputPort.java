package catwalks.node.net;

import io.netty.buffer.ByteBuf;

public abstract class InputPort<T> {
	
	public final Class<T> type;
	T value;
	boolean modified;
	
	public InputPort(Class<T> type, T value) {
		this.type = type;
		this.value = value;
	}
	
	public void setValue(T value) {
		if(this.value == value) return;
		if(this.value != null && this.value.equals(value)) return;
		if(value != null && value.equals(this.value)) return;
		this.value = value;
		this.modified = true;
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
	
	public abstract void readFromBuf(ByteBuf buf);
	
	public abstract void writeToBuf(ByteBuf buf);
	
}
