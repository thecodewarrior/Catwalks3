package catwalks.block.property;

/**
 * Created by TheCodeWarrior
 */
public class UPropertyObject<T extends Comparable<T>> extends UPropertyHelper<T>{
	
	public UPropertyObject(String name, Class<T> clazz) {
		super(name, clazz);
	}
	
	@Override
	public boolean isValid(T value) {
		return true;
	}
	
	@Override
	public String valueToString(T value) {
		return "complexData";
	}
}
