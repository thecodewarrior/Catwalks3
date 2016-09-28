package catwalks.compat.jei;

import mezz.jei.api.*;

@mezz.jei.api.JEIPlugin
public class CatwalksPlugin extends BlankModPlugin {

	@Override
	public void register(IModRegistry registry) {
		IItemRegistry itemRegistry = registry.getItemRegistry();
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		
//		registry.addRecipeCategories(
//			new DecorationRecipeCategory(jeiHelpers.getGuiHelper());
//		);
		
		registry.addRecipeHandlers(
			new DecorationSplitRecipeHandler(guiHelper),
			new DecorationRepairRecipeHandler(guiHelper)
		);
		
	}
	
}
