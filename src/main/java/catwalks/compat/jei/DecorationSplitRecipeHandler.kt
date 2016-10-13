package catwalks.compat.jei

import catwalks.item.crafting.RecipeDecorationSplit
import mezz.jei.api.IGuiHelper
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.BlankRecipeWrapper
import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack

class DecorationSplitRecipeHandler(internal var helper:

                                   IGuiHelper) : IRecipeHandler<RecipeDecorationSplit> {

    override fun getRecipeClass(): Class<RecipeDecorationSplit> {
        return RecipeDecorationSplit::class.java
    }

    override fun getRecipeCategoryUid(): String {
        return VanillaRecipeCategoryUid.CRAFTING
    }

    override fun getRecipeCategoryUid(recipe: RecipeDecorationSplit): String {
        return VanillaRecipeCategoryUid.CRAFTING
    }

    override fun getRecipeWrapper(recipe: RecipeDecorationSplit): IRecipeWrapper {
        return Wrapper(helper, recipe)
    }

    override fun isRecipeValid(recipe: RecipeDecorationSplit): Boolean {
        return true
    }

    class Wrapper
    @SuppressWarnings("unchecked")
    constructor(guiHelper: IGuiHelper, recipe: RecipeDecorationSplit) : BlankRecipeWrapper(), ICraftingRecipeWrapper {

        internal val inputs = mutableListOf<ItemStack>()
        internal val outputs = mutableListOf<ItemStack>()

        init {
            val item = recipe.targetItem

            inputs.add(ItemStack(item))
            outputs.add(ItemStack(item, 1, item.maxDamage - item.maxDamage / 2))
        }

        override fun getIngredients(ingredients: IIngredients) {
            ingredients.setInputs<ItemStack>(ItemStack::class.java, inputs)
            ingredients.setOutputs<ItemStack>(ItemStack::class.java, outputs)
        }

        override fun getInputs(): List<*> {
            return inputs
        }

        override fun getOutputs(): List<ItemStack> {
            return outputs
        }

        override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY)

            GlStateManager.pushMatrix()

            val str = I18n.format("gui.jei.crafting.decorationSplit")
            val width = minecraft.fontRendererObj.getStringWidth(str)

            minecraft.fontRendererObj.drawString(str, 103 - width / 2, 41, 4210752)

            GlStateManager.popMatrix()
        }

    }

}
