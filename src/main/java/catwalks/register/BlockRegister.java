package catwalks.register;

import java.util.ArrayList;
import java.util.List;

import catwalks.CatwalksMod;
import catwalks.block.BlockCatwalk;
import catwalks.block.BlockCatwalkBase.EnumCatwalkMaterial;
import catwalks.block.BlockCatwalkStair;
import catwalks.block.BlockCatwalkStairTop;
import catwalks.block.extended.TileExtended;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRegister {

	public static List<ResourceLocation> textures = new ArrayList<>();
	
	public static BlockCatwalk catwalk;
	public static BlockCatwalkStair catwalkStair;
	public static BlockCatwalkStairTop stairTop;
	
	public static void register() {
		GameRegistry.registerTileEntity(TileExtended.class, "tileExtended");
		catwalk = new BlockCatwalk();
		catwalkStair = new BlockCatwalkStair();
		stairTop = new BlockCatwalkStairTop();
	}
	
	private static void registerTexture(String path) {
		textures.add(new ResourceLocation(CatwalksMod.MODID + ":" + path));
	}
	
	private static void registerTextureAllMaterials(String path) {
		for (EnumCatwalkMaterial mat : EnumCatwalkMaterial.values()) {
			registerTexture(path.replace("<mat>", mat.getName().toLowerCase()));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRender() {
		ModelLoader.setCustomStateMapper(catwalk, new StateMapperStatic("catwalk"));
		Item catwalkItem = Item.getItemFromBlock(catwalk);
		ResourceLocation catwalkRL = Item.itemRegistry.getNameForObject(catwalkItem);
		ModelLoader.setCustomModelResourceLocation(catwalkItem, 0, new ModelResourceLocation(catwalkRL.toString()+"_steel" , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(catwalkItem, 1, new ModelResourceLocation(catwalkRL.toString()+"_stone" , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(catwalkItem, 2, new ModelResourceLocation(catwalkRL.toString()+"_wood"  , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(catwalkItem, 3, new ModelResourceLocation(catwalkRL.toString()+"_custom", "inventory" ));
		
		ModelLoader.setCustomStateMapper(catwalkStair, new StateMapperStatic("catwalkStair"));
		ModelLoader.setCustomStateMapper(stairTop, new StateMapperStatic("catwalkStairTop"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(catwalkStair), 0, new ModelResourceLocation(catwalkStair.getRegistryName(), "inventory"));
		
//		registerTextureVariants("catwalk/side",   "blocks/catwalk/<mat>/side/");
//		registerTextureVariants("catwalk/bottom", "blocks/catwalk/<mat>/bottom/");
		
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
		
	}
	
	
	public static class StateMapperStatic extends StateMapperBase {

		ModelResourceLocation loc;
		
		public StateMapperStatic(String loc) {
			this.loc = new ModelResourceLocation(CatwalksMod.MODID + ":" + loc);
		}
		
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			return loc;
		}
		
	}
}
