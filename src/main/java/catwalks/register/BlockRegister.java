package catwalks.register;

import java.util.ArrayList;
import java.util.List;

import catwalks.CatwalksMod;
import catwalks.block.BlockCatwalk;
import catwalks.block.BlockCatwalkBase;
import catwalks.block.BlockCatwalkBase.EnumCatwalkMaterial;
import catwalks.block.extended.TileExtended;
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
	
	
	public static void register() {
		GameRegistry.registerTileEntity(TileExtended.class, "tileExtended");
		catwalk = new BlockCatwalk();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRender() {
		ModelLoader.setCustomStateMapper(catwalk, new StateMapperStatic("catwalk"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(catwalk), 0, new ModelResourceLocation(catwalk.getRegistryName(), "inventory"));
		
		boolean[] TF = new boolean[] {true, false};
		
		for (EnumCatwalkMaterial material : EnumCatwalkMaterial.values()) {
			for(boolean tape : TF) {
				for(boolean lights : TF) {
					for(boolean vines : TF) {					
						List<ResourceLocation> textures = new ArrayList<>();
						
						textures.add(new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/" + material.getName().toLowerCase() + "/side/base"));
						if(tape)
							textures.add(new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/" + material.getName().toLowerCase() + "/side/decorations/tape"));
						if(lights)
							textures.add(new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/" + material.getName().toLowerCase() + "/side/decorations/lights"));
						if(vines)
							textures.add(new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/" + material.getName().toLowerCase() + "/side/decorations/vines"));
						TextureGenerator.addTexture(new CompositeTexture(
							new ResourceLocation(BlockCatwalkBase.makeTextureGenName("catwalk", "side", material, tape, lights, vines)),
							textures
						));
					}
				}
			}
			TextureGenerator.addTexture(new CompositeTexture(
					new ResourceLocation(BlockCatwalkBase.makeTextureGenName("catwalk", "bottom", material, false, false, false)),
					new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/" + material.getName().toLowerCase() + "/bottom")
				));
		}
		
		TextureGenerator.addTexture(new CompositeTexture(
				new ResourceLocation(CatwalksMod.MODID + ":gen/catwalk_side_"),
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/base")
			));
		
		TextureGenerator.addTexture(new CompositeTexture(
				new ResourceLocation(CatwalksMod.MODID + ":gen/catwalk_side_t"),
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/base"),
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/decorations/tape")
			));
		
		TextureGenerator.addTexture(new CompositeTexture(
				new ResourceLocation(CatwalksMod.MODID + ":gen/catwalk_side_l"),
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/base"),
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/decorations/lights")
			));
		
		TextureGenerator.addTexture(new CompositeTexture(
				new ResourceLocation(CatwalksMod.MODID + ":gen/catwalk_side_tl"),
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/base"),
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/decorations/tape"),
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/decorations/lights")
			));
		
		TextureGenerator.addTexture(new CompositeTexture(
				new ResourceLocation(CatwalksMod.MODID + ":gen/catwalk_bottom"),
				new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/bottom/base")
			));
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
