package catwalks.util;

public class Vec2i {

	protected int x, z;
	
	public Vec2i(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec2i other = (Vec2i) obj;
		if (x != other.x)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	public static class MutableVec2i extends Vec2i {
		
		public MutableVec2i(int x, int z) {
			super(x, z);
		}
		
		public void setX(int x) {
			this.x = x;
		}
		
		public void setZ(int z) {
			this.z = z;
		}
	}
	
}
