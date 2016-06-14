package catwalks.compat.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import catwalks.item.crafting.RecipeDecorationRepair;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.vanilla.crafting.AbstractShapelessRecipeWrapper;

public class DecorationRepairRecipeHandler implements IRecipeHandler<RecipeDecorationRepair> {

	IGuiHelper helper;
	
	public DecorationRepairRecipeHandler(IGuiHelper helper) {
		this.helper = helper;
	}
	
	@Override
	public Class getRecipeClass() {
		return RecipeDecorationRepair.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull RecipeDecorationRepair recipe) {
		return new Wrapper(helper, recipe);
	}

	@Override
	public boolean isRecipeValid(@Nonnull RecipeDecorationRepair recipe) {
		return true;
	}
	
	public static class Wrapper extends AbstractShapelessRecipeWrapper {

		List inputs, outputs;
		
		@SuppressWarnings("unchecked")
		public Wrapper(IGuiHelper guiHelper, RecipeDecorationRepair recipe) {
			super(guiHelper);
			
			Item item = recipe.getTargetItem();
			inputs =  new ArrayList<>();
			outputs = new ArrayList<>();
			
			inputs.add(new ItemStack(item, 1, item.getMaxDamage() - (item.getMaxDamage()/4)));
			inputs.add(new ItemStack(item, 1, item.getMaxDamage() - (item.getMaxDamage()/2)));
			
			outputs.add(new ItemStack(item, 1, item.getMaxDamage() - (3*item.getMaxDamage()/4)));
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
			
			String str = I18n.format("gui.jei.crafting.decorationMerge");
			int width = minecraft.fontRendererObj.getStringWidth(str);
			
			minecraft.fontRendererObj.drawString(str, 103-(width/2), 41, 0x404040);
			
			GlStateManager.popMatrix();
		}
		
	}

}
