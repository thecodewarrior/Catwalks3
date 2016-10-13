package catwalks.register;

import catwalks.item.ItemBase;
import catwalks.render.part.CatwalkBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static catwalks.register.ItemRegister.renderRegsiterItems;

/**
 * Created by TheCodeWarrior
 */
@SideOnly(Side.CLIENT)
public class RenderRegister {
	public static class Parts {
		public static void initRender() {
			ModelLoaderRegistry.registerLoader(new CatwalkBakedModel.ModelLoader());
//			MultipartRegistryClient.registerSpecialPartStateMapper(new ResourceLocation(PartScaffold.ID), new CatwalkBakedModel.Statemapper());
		}
	}
	public static class Blocks {
		public static void initRender() {
			
		}
	}
	
	public static class Items {
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
}
