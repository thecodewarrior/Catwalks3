package catwalks.compat.jei

import mezz.jei.api.*

@mezz.jei.api.JEIPlugin
class CatwalksPlugin : BlankModPlugin() {

    override fun register(registry: IModRegistry?) {
        val itemRegistry = registry!!.itemRegistry
        val jeiHelpers = registry.jeiHelpers
        val guiHelper = jeiHelpers.guiHelper

        //		registry.addRecipeCategories(
        //			new DecorationRecipeCategory(jeiHelpers.getGuiHelper());
        //		);

        registry.addRecipeHandlers(
                DecorationSplitRecipeHandler(guiHelper),
                DecorationRepairRecipeHandler(guiHelper))

    }

}
