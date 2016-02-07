package catwalks.register;

import catwalks.item.ItemBase;
import catwalks.item.ItemCatwalkTool;
import catwalks.item.ItemDecoration;
import catwalks.item.crafting.RecipeDecorationRepair;
import catwalks.item.crafting.RecipeDecorationSplit;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRegister {
	
	public static ItemDecoration lights, tape;
	public static ItemCatwalkTool tool;
	public static ItemBase grate;
	
	public static void register() {
		lights = new ItemDecoration("lights");
		tape   = new ItemDecoration("tape");
		tool   = new ItemCatwalkTool();
		grate  = new ItemBase("steelgrate");
		
		CraftingManager.getInstance().addRecipe(new RecipeDecorationRepair());
		CraftingManager.getInstance().addRecipe(new RecipeDecorationSplit());
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRender() {
		registerRender(lights, tape, tool);
	}
	
	@SideOnly(Side.CLIENT)
	private static void registerRender(ItemBase... items) {		
		for (ItemBase item : items) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
//			item.initModel();
		}
	}
}
