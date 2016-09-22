package catwalks.block.extended.tileprops;

/**
 * Created by TheCodeWarrior
 */
public class FloatProp {
	
	protected int index, bits;
	protected float coefficient;
	
	public FloatProp(int index, int bits) {
		this.index = index;
		this.bits = bits;
		coefficient = (float)Math.pow(2, bits/2);
	}
	
	public void set(TileExtended tile, float value) {
		int intVal = (int)( value * coefficient );
		
		tile.setNumber(index, bits, intVal);
	}
	
	public float get(TileExtended tile) {
		int intVal = tile.getNumber(index, bits);
		return intVal / coefficient;
	}
}
