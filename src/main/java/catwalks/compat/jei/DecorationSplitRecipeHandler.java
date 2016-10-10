package catwalks.compat.jei;

import catwalks.item.crafting.RecipeDecorationSplit;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DecorationSplitRecipeHandler implements IRecipeHandler<RecipeDecorationSplit> {

	IGuiHelper helper;
	
	public DecorationSplitRecipeHandler(IGuiHelper helper) {
		this.helper = helper;
	}
	
	@Nonnull
	@Override
	public Class getRecipeClass() {
		return RecipeDecorationSplit.class;
	}

	@Nonnull
	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}
	
	@Nonnull
	@Override
	public String getRecipeCategoryUid(@Nonnull RecipeDecorationSplit recipe) {
		return VanillaRecipeCategoryUid.CRAFTING;
	}
	
	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull RecipeDecorationSplit recipe) {
		return new Wrapper(helper, recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull RecipeDecorationSplit recipe) {
		return true;
	}
	
	public static class Wrapper extends BlankRecipeWrapper implements ICraftingRecipeWrapper {

		List inputs, outputs;
		
		@SuppressWarnings("unchecked")
		public Wrapper(IGuiHelper guiHelper, RecipeDecorationSplit recipe) {
			Item item = recipe.getTargetItem();
			inputs =  new ArrayList<>();
			outputs = new ArrayList<>();
			
			inputs.add(new ItemStack(item));
			outputs.add(new ItemStack(item, 1, item.getMaxDamage() - (item.getMaxDamage()/2)));
		}
		
		@Override
		public void getIngredients(IIngredients ingredients) {
			ingredients.setInputs(ItemStack.class, inputs);
			ingredients.setOutputs(ItemStack.class, outputs);
		}
		
		@Nonnull
		@Override
		public List getInputs() {
			return inputs;
		}

		@Nonnull
		@Override
		public List<ItemStack> getOutputs() {
			return outputs;
		}
		
		@Override
		public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
			super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);

			GlStateManager.pushMatrix();
			
			String str = I18n.format("gui.jei.crafting.decorationSplit");
			int width = minecraft.fontRendererObj.getStringWidth(str);
			
			minecraft.fontRendererObj.drawString(str, 103-(width/2), 41, 4210752);
			
			GlStateManager.popMatrix();
		}
		
	}

}
