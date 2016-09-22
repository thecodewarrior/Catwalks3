package catwalks.register;

import java.util.function.Supplier;

import catwalks.Const;
import catwalks.block.BlockCatwalk;
import catwalks.block.BlockCatwalkStair;
import catwalks.block.BlockCatwalkStairTop;
import catwalks.block.BlockScaffolding;
import catwalks.block.EnumCatwalkMaterial;
import catwalks.block.extended.BlockCagedLadder;
import catwalks.block.extended.tileprops.TileExtended;
import catwalks.render.ModelHandler;
import catwalks.render.cached.CachedSmartModel;
import catwalks.render.cached.models.CatwalkModel;
import catwalks.render.cached.models.LadderModel;
import catwalks.render.cached.models.StairBottomModel;
import catwalks.render.cached.models.StairTopModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRegister {

	
	public static BlockCatwalk catwalk;
	public static BlockCatwalkStair catwalkStair;
	public static BlockCatwalkStairTop stairTop;
	public static BlockCagedLadder cagedLadder;
	public static BlockScaffolding scaffold;
	
	public static void register() {
		GameRegistry.registerTileEntity(TileExtended.class, "tileExtended");
		catwalk = new BlockCatwalk();
		catwalkStair = new BlockCatwalkStair();
		stairTop = new BlockCatwalkStairTop();
		cagedLadder = new BlockCagedLadder();
		scaffold = new BlockScaffolding();
	}
	
	private static void registerTextureAllMaterials(String path) {
		for (EnumCatwalkMaterial mat : EnumCatwalkMaterial.values()) {
			ModelHandler.INSTANCE.registerTexture(path.replace("<mat>", mat.getName().toLowerCase()));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRender() {
		
		ModelHandler.INSTANCE.registerModel("catwalk",          () -> new CachedSmartModel(new CatwalkModel()) );
		ModelHandler.INSTANCE.registerModel("catwalkStair",     () -> new CachedSmartModel(new StairBottomModel()));
		ModelHandler.INSTANCE.registerModel("catwalkStairTop",  () -> new CachedSmartModel(new StairTopModel()));
		
		ModelHandler.INSTANCE.registerModel("cagedLadder",      new Supplier<IBakedModel>() {

			@Override
			public IBakedModel get() {
				return new CachedSmartModel(new LadderModel());
			}
			
		}); // still possible like this, but it's clunky.
		
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
