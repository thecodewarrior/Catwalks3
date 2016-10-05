package catwalks.part;

import catwalks.block.EnumDecoration;

/**
 * Created by TheCodeWarrior
 */
public interface IDecoratable {
	boolean addDecoration(EnumDecoration decor);
	boolean removeDecoration(EnumDecoration decor);
	boolean hasDecoration(EnumDecoration decor);
}
