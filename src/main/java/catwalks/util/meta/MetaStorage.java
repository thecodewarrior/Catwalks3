package catwalks.util.meta;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TheCodeWarrior
 */
public class MetaStorage {
	
	public BitSet meta = new BitSet();
	protected IDirtyable dirtyListener;
	protected Allocator allocator;
	
	public MetaStorage(Allocator allocator) {
		this.allocator = allocator;
	}
	
	public MetaStorage(Allocator allocator, IDirtyable dirtyListener) {
		this(allocator); this.dirtyListener = dirtyListener;
	}
	
	protected boolean dirty = false;
	protected void markDirty() {
		dirty = true;
	}
	
	public void notifyIfDirty() {
		if(dirty && dirtyListener != null)
			dirtyListener.markDirty();
		dirty = false;
	}
	
	public boolean get(int bit) {
		return meta.get(bit);
	}
	
	public void set(int bit, boolean value) {
		if(meta.get(bit) != value)
			markDirty();
		meta.set(bit, value);
	}
	
	public byte[] toByteArray() {
		return meta.toByteArray();
	}
	public void fromByteArray(byte[] array) {
		meta = BitSet.valueOf(array);
	}
	
	public long[] toLongArray() {
		return meta.toLongArray();
	}
	public void fromLongArray(long[] array) {
		meta = BitSet.valueOf(array);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		int len = allocator.getLength();
		for(int i = 0; i < len; i++) {
			if(meta.get(i))
				builder.append("1");
			else
				builder.append("0");
		}
		
		builder.append("\n");
		
		builder.append(allocator.toString(this));
		
		return builder.toString();
	}
	
	public static class Allocator {
		protected int index = 0;
		protected Map<String, MetaProperty> props = new HashMap<>();
		
		public int getLength() {
			return index;
		}
		
		public void bufferBits(int bits) {
			index += bits;
		}
		
		public BoolProp allocateBool(String name) {
			BoolProp p = new BoolProp(index);
			index++;
			props.put(name, p);
			return p;
		}
		
		public IntProp allocateInt(String name, int bits) {
			IntProp p = new IntProp(index, bits);
			index += bits;
			props.put(name, p);
			return p;
		}
		
		public FloatProp allocateFloat(String name, int bits) {
			FloatProp p = new FloatProp(index, bits);
			index += bits;
			props.put(name, p);
			return p;
		}
		
		public <T> ArrayProp<T> allocateArray(String name, T[] values, int bits) {
			ArrayProp<T> p = new ArrayProp<T>(index, values, bits);
			index += bits;
			props.put(name, p);
			return p;
		}
		
		public <T> BoolMapProp<T> allocateBoolMap(String name, T[] values, int bits) {
			BoolMapProp<T> p = new BoolMapProp<T>(index, values, bits);
			index += bits;
			props.put(name, p);
			return p;
		}
		
		public void addBits(MetaProperty prop, int bits) {
			prop.addBits(index, bits);
			index += bits;
		}
		
		public String toString(MetaStorage storage) {
			StringBuilder builder = new StringBuilder();
			
			for(Map.Entry<String, MetaProperty> entry : props.entrySet()) {
				builder.append(entry.getKey()).append(" : ").append(entry.getValue().getValue(storage)).append("\n");
			}
			
			return builder.toString();
		}
	}
	
	public static int bits(int combos) {
		int value = combos-1;
		int count = 0;
		while (value > 0) {
			count++;
			value = value >> 1;
		}
		return count;
	}
}
