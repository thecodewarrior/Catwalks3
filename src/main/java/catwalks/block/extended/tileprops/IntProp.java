package catwalks.block.extended.tileprops;

/**
 * Created by TheCodeWarrior
 */
public class IntProp {
	
	protected int index, bits;
	
	public IntProp(int index, int bits) {
		this.index = index;
		this.bits = bits;
	}
	
	public void set(TileExtended tile, int value) {
		tile.setNumber(index, bits, value);
	}
	
	public int get(TileExtended tile) {
		return tile.getNumber(index, bits);
	}
}
