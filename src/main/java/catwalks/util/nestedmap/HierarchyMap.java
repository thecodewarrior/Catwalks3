package catwalks.util.nestedmap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by TheCodeWarrior
 */
public class HierarchyMap<T> {
	
	private final Map<HierarchyKey, T> map = new HashMap<>();
	public final int depth;
	
	public HierarchyMap(int depth) {
		this.depth = depth;
	}
	
	public void put(T value, Object... keys) {
		if(keys.length != depth) {
			throw new IllegalArgumentException(String.format("Provided key length %d doesn't match depth %d", keys.length, depth));
		}
		map.put(HierarchyKey.ACCESS_KEY.get().set(keys), value);
	}
	
	public T get(Object... keys) {
		if(keys.length != depth) {
			throw new IllegalArgumentException(String.format("Provided key length %d doesn't match depth %d", keys.length, depth));
		}
		return map.get(HierarchyKey.ACCESS_KEY.get().set(keys));
	}
}
