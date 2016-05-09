package catwalks.register;

import java.util.ArrayList;
import java.util.List;

import catwalks.item.ItemBase;
import catwalks.item.ItemCatwalkTool;
import catwalks.item.ItemDecoration;
import catwalks.item.ItemLadderGrabber;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRegister {
	
	public static ItemDecoration lights, tape, speed;
	public static ItemCatwalkTool tool;
	public static ItemBase grate;
	public static ItemLadderGrabber ladderGrabber;
	
	public static void register() {
		lights = new ItemDecoration("lights");
		tape   = new ItemDecoration("tape");
		speed  = new ItemDecoration("speed");
		tool   = new ItemCatwalkTool();
		grate  = new ItemBase("steelgrate");
		ladderGrabber = new ItemLadderGrabber();
	}
	
	public static List<ItemBase> renderRegsiterItems = new ArrayList<>();
	
	@SideOnly(Side.CLIENT)
	public static void initRender() {
		registerRender();
	}
	
	@SideOnly(Side.CLIENT)
	private static void registerRender() {		
		for (ItemBase item : renderRegsiterItems) {
			String[] customVariants = item.getCustomRenderVariants();
			if(customVariants == null) { 
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), ""));
			} else {
				for (int i = 0; i < customVariants.length; i++) {
					if( "".equals(customVariants[i]) ) {
						ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName(), ""));
					} else {
						ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName() + "." + customVariants[i], ""));
					}
				}
			}
//			item.initModel();
		}
	}
}
