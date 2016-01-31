package catwalks.register;

import catwalks.CatwalksMod;
import catwalks.block.BlockCatwalk;
import catwalks.block.extended.TileExtended;
import catwalks.texture.CompositeTexture;
import catwalks.texture.TextureGenerator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
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
		TextureGenerator.addTexture(new CompositeTexture(
				new ResourceLocation(CatwalksMod.MODID + ":gen/catwalk_side"),
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
