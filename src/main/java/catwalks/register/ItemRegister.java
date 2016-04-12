package catwalks.register;

import catwalks.item.ItemBase;
import catwalks.item.ItemCatwalkTool;
import catwalks.item.ItemDecoration;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRegister {
	
	public static ItemDecoration lights, tape, speed;
	public static ItemCatwalkTool tool;
	public static ItemBase grate;
	
	public static void register() {
		lights = new ItemDecoration("lights");
		tape   = new ItemDecoration("tape");
		speed  = new ItemDecoration("speed");
		tool   = new ItemCatwalkTool();
		grate  = new ItemBase("steelgrate");
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRender() {
		registerRender(lights, tape, speed, tool, grate);
	}
	
	@SideOnly(Side.CLIENT)
	private static void registerRender(ItemBase... items) {		
		for (ItemBase item : items) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
//			item.initModel();
		}
	}
}
