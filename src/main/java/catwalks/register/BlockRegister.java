package catwalks.register;

import java.util.ArrayList;
import java.util.List;

import catwalks.Const;
import catwalks.block.BlockCatwalk;
import catwalks.block.BlockCatwalkStair;
import catwalks.block.BlockCatwalkStairTop;
import catwalks.block.BlockScaffolding;
import catwalks.block.EnumCatwalkMaterial;
import catwalks.block.extended.BlockCagedLadder;
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
	
	private static void registerTexture(String path) {
		textures.add(new ResourceLocation(Const.MODID + ":" + path));
	}
	
	private static void registerTextureAllMaterials(String path) {
		for (EnumCatwalkMaterial mat : EnumCatwalkMaterial.values()) {
			registerTexture(path.replace("<mat>", mat.getName().toLowerCase()));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRender() {
		Item item; String rl;
		
		item = Item.getItemFromBlock(scaffold);
		rl   = Item.itemRegistry.getNameForObject(item).toString();
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(rl+"_inv", "material=steel"  ));
		ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(rl+"_inv", "material=rusty"  ));
		ModelLoader.setCustomModelResourceLocation(item, 2, new ModelResourceLocation(rl+"_inv", "material=wood"   ));
		ModelLoader.setCustomModelResourceLocation(item, 3, new ModelResourceLocation(rl+"_inv", "material=custom" ));
		
		ModelLoader.setCustomStateMapper(catwalk, new StateMapperStatic("catwalk"));
		item = Item.getItemFromBlock(catwalk);
		rl   = Item.itemRegistry.getNameForObject(item).toString();
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(rl+"_steel" , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(rl+"_rusty" , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(item, 2, new ModelResourceLocation(rl+"_wood"  , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(item, 3, new ModelResourceLocation(rl+"_custom", "inventory" ));
		
		ModelLoader.setCustomStateMapper(catwalkStair, new StateMapperStatic("catwalkStair"));
		item = Item.getItemFromBlock(catwalkStair);
		rl   = Item.itemRegistry.getNameForObject(item).toString();
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(rl+"_steel" , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(rl+"_rusty" , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(item, 2, new ModelResourceLocation(rl+"_wood"  , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(item, 3, new ModelResourceLocation(rl+"_custom", "inventory" ));

		ModelLoader.setCustomStateMapper(stairTop, new StateMapperStatic("catwalkStairTop"));
		
		ModelLoader.setCustomStateMapper(cagedLadder, new StateMapperStatic("cagedLadder"));
		item = Item.getItemFromBlock(cagedLadder);
		rl   = Item.itemRegistry.getNameForObject(item).toString();
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(rl+"_steel" , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(rl+"_rusty" , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(item, 2, new ModelResourceLocation(rl+"_wood"  , "inventory" ));
		ModelLoader.setCustomModelResourceLocation(item, 3, new ModelResourceLocation(rl+"_custom", "inventory" ));
		
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
		
	}
	
	
	public static class StateMapperStatic extends StateMapperBase {

		ModelResourceLocation loc;
		
		public StateMapperStatic(String loc) {
			this.loc = new ModelResourceLocation(Const.MODID + ":" + loc);
		}
		
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			return loc;
		}
		
	}
}
