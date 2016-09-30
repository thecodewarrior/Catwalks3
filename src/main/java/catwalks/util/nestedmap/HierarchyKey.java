package catwalks.util.nestedmap;

import java.util.Arrays;

/**
 * Created by TheCodeWarrior
 */
class HierarchyKey {
	
	public static final ThreadLocal<MutableHierarchyKey> ACCESS_KEY = new ThreadLocal<MutableHierarchyKey>() {
		@Override
		protected MutableHierarchyKey initialValue() {
			return new MutableHierarchyKey();
		}
	};
	
	protected Object[] array;
	
	public HierarchyKey(Object[] array) {
		this.array = array;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!( o instanceof HierarchyKey )) return false;
		
		HierarchyKey that = (HierarchyKey) o;
		
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(array, that.array);
		
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(array);
	}
	
	public static class MutableHierarchyKey extends HierarchyKey {
		
		protected MutableHierarchyKey() {
			super(null);
		}
		
		public MutableHierarchyKey set(Object[] keys) {
			this.array = keys;
			return this;
		}
	}
}
