package catwalks.util.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheCodeWarrior
 */
public class IntProp extends MetaValueProperty<Integer> {
	
	protected List<Integer> indices = new ArrayList<>(), bits = new ArrayList<>();
	
	public IntProp(int index, int bits) {
		addBits(index, bits);
	}
	
	@Override
	public boolean addBits(int index, int bits) {
		this.indices.add(index);
		this.bits.add(bits);
		return true;
	}
	
	@Override
	public void set(MetaStorage storage, Integer value) {
		int currentBit = 0;
		for (int i = 0; i < indices.size(); i++) {
			int index = indices.get(i);
			int len = bits.get(i);
			for (int j = 0; j < len; j++) {
				storage.set(j + index, ( ( value >> currentBit ) & 1 ) == 1);
				currentBit++;
			}
		}
		storage.notifyIfDirty();
	}
	
	@Override
	public Integer get(MetaStorage storage) {
		int value = 0;
		int currentBit = 0;
		
		for (int i = 0; i < indices.size(); i++) {
			int index = indices.get(i);
			int len = bits.get(i);
			for (int j = 0; j < len; j++) {
				value += storage.get(j + index) ? ( 1 << currentBit ) : 0;
				currentBit++;
			}
		}
		
		return value;
	}
}
