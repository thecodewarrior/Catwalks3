package catwalks.register;

import catwalks.Const;
import catwalks.block.EnumCatwalkMaterial;
import catwalks.item.ItemBase;
import catwalks.part.PartScaffold;
import catwalks.render.ModelHandler;
import catwalks.render.cached.CachedSmartModel;
import catwalks.render.cached.models.CatwalkModel;
import catwalks.render.cached.models.LadderModel;
import catwalks.render.cached.models.StairBottomModel;
import catwalks.render.cached.models.StairTopModel;
import catwalks.render.part.CatwalkBakedModel;
import mcmultipart.client.multipart.MultipartRegistryClient;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static catwalks.register.BlockRegister.*;
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
		private static void registerTextureAllMaterials(String path) {
			for (EnumCatwalkMaterial mat : EnumCatwalkMaterial.values()) {
				ModelHandler.INSTANCE.registerTexture(path.replace("<mat>", mat.getName().toLowerCase()));
			}
		}
		
		public static void initRender() {
			
			ModelHandler.INSTANCE.registerModel("catwalk",          () -> new CachedSmartModel(new CatwalkModel()) );
			ModelHandler.INSTANCE.registerModel("catwalkStair",     () -> new CachedSmartModel(new StairBottomModel()));
			ModelHandler.INSTANCE.registerModel("catwalkStairTop",  () -> new CachedSmartModel(new StairTopModel()));
			
			ModelHandler.INSTANCE.registerModel("cagedLadder",      () -> new CachedSmartModel(new LadderModel()));
			
			// Catwalk
			registerTextureAllMaterials("blocks/catwalk/<mat>/side/base");
			registerTextureAllMaterials("blocks/catwalk/<mat>/side/tape");
			registerTextureAllMaterials("blocks/catwalk/<mat>/side/lights");
			registerTextureAllMaterials("blocks/catwalk/<mat>/side/speed");
			
			registerTextureAllMaterials("blocks/catwalk/<mat>/bottom/base");
			registerTextureAllMaterials("blocks/catwalk/<mat>/bottom/tape");
			registerTextureAllMaterials("blocks/catwalk/<mat>/bottom/lights");
			registerTextureAllMaterials("blocks/catwalk/<mat>/bottom/speed");
			
			// Stair
			registerTextureAllMaterials("blocks/stair/<mat>/base");
			registerTextureAllMaterials("blocks/stair/<mat>/tape");
			registerTextureAllMaterials("blocks/stair/<mat>/lights");
			registerTextureAllMaterials("blocks/stair/<mat>/speed");
			
			// Ladder
			registerTextureAllMaterials("blocks/ladder/<mat>/base");
			registerTextureAllMaterials("blocks/ladder/<mat>/tape");
			registerTextureAllMaterials("blocks/ladder/<mat>/lights");
			registerTextureAllMaterials("blocks/ladder/<mat>/speed");
			
			registerMaterialModels(scaffold, null);
			registerMaterialModels(catwalk,      "catwalk");
			registerMaterialModels(catwalkStair, "catwalkStair");
			registerMaterialModels(cagedLadder,  "cagedLadder");
			
			ModelHandler.setStaticMap(stairTop, "catwalkStairTop");
		}
		
		public static void registerMaterialModels(Block block, String modelName) {
			if(modelName != null)
				ModelHandler.setStaticMap(block, modelName);
			Item item = Item.getItemFromBlock(block);
			
			String rl = String.format("%s:%s.inv", Const.MODID, modelName == null ? Item.REGISTRY.getNameForObject(item).getResourcePath() : modelName);
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(rl, "material=steel"  ));
			ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(rl, "material=iesteel"  ));
			ModelLoader.setCustomModelResourceLocation(item, 2, new ModelResourceLocation(rl, "material=wood"   ));
			ModelLoader.setCustomModelResourceLocation(item, 3, new ModelResourceLocation(rl, "material=custom" ));
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
