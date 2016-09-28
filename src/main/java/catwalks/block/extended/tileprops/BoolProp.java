package catwalks.block.extended.tileprops;

/**
 * Created by TheCodeWarrior
 */
public class BoolProp {
	
	protected int index;
	
	public BoolProp(int index) {
		this.index = index;
	}
	
	public void set(TileExtended tile, boolean value) {
		tile.setBoolean(index, value);
	}
	
	public boolean get(TileExtended tile) {
		return tile.getBoolean(index);
	}
}
