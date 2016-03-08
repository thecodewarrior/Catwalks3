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
import catwalks.texture.TextureGenerator;
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

	public static BlockCatwalk catwalk;
	public static BlockCatwalkStair catwalkStair;
	public static BlockCatwalkStairTop multiblockPart;
	
	public static void register() {
		GameRegistry.registerTileEntity(TileExtended.class, "tileExtended");
		catwalk = new BlockCatwalk();
		catwalkStair = new BlockCatwalkStair();
		multiblockPart = new BlockCatwalkStairTop();
	}
	
	
	private static void registerTextureVariants(String path, String textureLocation) {
		for (StaticCatwalkVariant variant : CatwalkVariant.VARIANTS) {
			String texturePrefix = textureLocation.replace("<mat>", variant.getMaterial().getName().toLowerCase());
			
			List<ResourceLocation> textures = new ArrayList<>();
			
			textures.add(new ResourceLocation(CatwalksMod.MODID + ":" + texturePrefix + "base"));
			if(variant.getTape())
				textures.add(new ResourceLocation(CatwalksMod.MODID + ":" + texturePrefix + "decorations/tape"));
			if(variant.getLights())
				textures.add(new ResourceLocation(CatwalksMod.MODID + ":" + texturePrefix + "decorations/lights"));
			if(variant.getVines())
				textures.add(new ResourceLocation(CatwalksMod.MODID + ":" + texturePrefix + "decorations/vines"));
			TextureGenerator.addTexture(new CompositeTexture(
				new ResourceLocation(variant.getTextureName(path)),
				textures
			));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRender() {
		ModelLoader.setCustomStateMapper(catwalk, new StateMapperStatic("catwalk"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(catwalk), 0, new ModelResourceLocation(catwalk.getRegistryName(), "inventory"));
		ModelLoader.setCustomStateMapper(catwalkStair, new StateMapperStatic("stair"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(catwalkStair), 0, new ModelResourceLocation(catwalkStair.getRegistryName(), "inventory"));
		
		registerTextureVariants("catwalk/side",   "blocks/catwalk/<mat>/side/");
		registerTextureVariants("catwalk/bottom", "blocks/catwalk/<mat>/bottom/");
		
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
