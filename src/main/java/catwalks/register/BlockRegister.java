package catwalks.register;

import java.util.ArrayList;
import java.util.List;

import catwalks.CatwalksMod;
import catwalks.block.BlockCatwalk;
import catwalks.block.BlockCatwalkBase;
import catwalks.block.BlockCatwalkBase.EnumCatwalkMaterial;
import catwalks.block.BlockCatwalkStair;
import catwalks.block.BlockCatwalkStairTop;
import catwalks.block.extended.TileExtended;
import catwalks.texture.CatwalkVariant;
import catwalks.texture.CatwalkVariant.StaticCatwalkVariant;
import catwalks.texture.CompositeTexture;
import catwalks.texture.TextureAtlasComposite;
import catwalks.texture.TextureGenerator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRegister {

	public static List<ResourceLocation> textures = new ArrayList<>();
	
	public static BlockCatwalk catwalk;
	public static BlockCatwalkStair catwalkStair;
	public static BlockCatwalkStairTop multiblockPart;
	
	public static void register() {
		GameRegistry.registerTileEntity(TileExtended.class, "tileExtended");
		catwalk = new BlockCatwalk();
		catwalkStair = new BlockCatwalkStair();
		multiblockPart = new BlockCatwalkStairTop();
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
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(catwalk), 0, new ModelResourceLocation(catwalk.getRegistryName(), "inventory"));
		ModelLoader.setCustomStateMapper(catwalkStair, new StateMapperStatic("catwalkStair"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(catwalkStair), 0, new ModelResourceLocation(catwalkStair.getRegistryName(), "inventory"));
		
//		registerTextureVariants("catwalk/side",   "blocks/catwalk/<mat>/side/");
//		registerTextureVariants("catwalk/bottom", "blocks/catwalk/<mat>/bottom/");
		
		registerTextureAllMaterials("blocks/catwalk/<mat>/bottom/base");
		registerTextureAllMaterials("blocks/catwalk/<mat>/side/base");
		registerTextureAllMaterials("blocks/catwalk/<mat>/side/decorations/tape");
		registerTextureAllMaterials("blocks/catwalk/<mat>/side/decorations/lights");
		registerTextureAllMaterials("blocks/catwalk/<mat>/side/decorations/vines");
		
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
