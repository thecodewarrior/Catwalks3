package catwalks.util.meta;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by TheCodeWarrior
 */
public class BoolArrayProp extends MetaMapProperty<Integer, Boolean> {
	protected List<Integer> indices = new ArrayList<>(), bits = new ArrayList<>();
	
	public BoolArrayProp(int index, int bits) {
		this.indices.add(index);
		this.bits.add(bits);
	}
	
	@Override
	public boolean addBits(int index, int bits) {
		this.indices.add(index);
		this.bits.add(bits);
		return true;
	}
	
	@Override
	public void set(MetaStorage storage, Integer val, Boolean value) {
		
		if(val < 0)
			throw new IndexOutOfBoundsException("Can't have negative array index!");
		
		int beginningBitIndex = 0;
		for(int i = 0; i < indices.size(); i++) {
			int index = indices.get(i);
			int len = bits.get(i);
			if(beginningBitIndex <= val && beginningBitIndex+len > val) {
				int subindex = val - beginningBitIndex;
				storage.set(index+subindex, value);
				storage.notifyIfDirty();
				return;
			}
			beginningBitIndex += len;
		}
		throw new NoSuchElementException("Couldn't find an allocated bit for array element " + val + "! Allocated bits can only fit " + (beginningBitIndex+1) + " values!");
	}
	
	@Override
	public Boolean get(MetaStorage storage, Integer val) {
		if(val < 0)
			throw new IndexOutOfBoundsException("Can't have negative array index!");
		int beginningBitIndex = 0;
		for(int i = 0; i < indices.size(); i++) {
			int index = indices.get(i);
			int len = bits.get(i);
			if(beginningBitIndex <= val && beginningBitIndex+len > val) {
				int subindex = val - beginningBitIndex;
				return storage.get(index+subindex);
			}
			beginningBitIndex += len;
		}
		throw new NoSuchElementException("Couldn't find an allocated bit for array element " + val + "! Allocated bits can only fit " + (beginningBitIndex+1) + " values!");
	}
	
	@Override
	public String getValue(MetaStorage storage) {
		String str = "[";
		for(int i = 0; i < indices.size(); i++) {
			int index = indices.get(i);
			int len = bits.get(i);
			for(int j = 0; j < len; j++) {
				str += storage.get(index+j) ? "1" : "0";
			}
		}
		str += "]";
		
		return str;
	}
}
