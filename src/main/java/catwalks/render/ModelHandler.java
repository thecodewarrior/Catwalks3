package catwalks.render;

import catwalks.Const;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class ModelHandler {
	public static String MODID = Const.MODID;
	public static final ModelHandler INSTANCE = new ModelHandler();
	
	public ModelHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void registerTexture(String path) {
		textures.add(new ResourceLocation(MODID + ":" + path));
	}
	
	public void registerModel(String name, Supplier<IBakedModel> generator) {
		registeredModels.put(name, generator);
	}
	
	public static void setStaticMap(Block block, String loc) {
		ModelLoader.setCustomStateMapper(block, new StateMapperStatic(loc));
	}
	
	// boring implementation details below
	
	private Map<String, Supplier<IBakedModel>> registeredModels = new HashMap<>();
	private Map<ModelResourceLocation, IBakedModel> modelsToInsert = new HashMap<>();
	
	{/* models */}
	
	private void model(String loc, IBakedModel model) {
		modelsToInsert.put(new ModelResourceLocation(MODID + ":" + loc), model);
	}
	
	@SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
		modelsToInsert.clear();
		
		for(Entry<String, Supplier<IBakedModel>> entry : registeredModels.entrySet()) {
			model(entry.getKey(), entry.getValue().get());
		}
		
        for (Entry<ModelResourceLocation, IBakedModel> model : modelsToInsert.entrySet()) {
        	event.getModelRegistry().putObject(model.getKey(), model.getValue());
		}
    }
	
	{/* textures */}
	
	public List<ResourceLocation> textures = new ArrayList<>();
	
	@SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event) {
		
		TextureMap map = event.getMap();
		
		for(ResourceLocation tex : textures) {
			
			map.getTextureExtry(tex.toString());
			TextureAtlasSprite texture = map.getTextureExtry(tex.toString());
			
			if(texture == null) {
				map.registerSprite(tex);
			}
		}

    }
	
	public static class StateMapperStatic extends StateMapperBase {

		ModelResourceLocation loc;
		
		public StateMapperStatic(String loc) {
			this.loc = new ModelResourceLocation(MODID + ":" + loc);
		}
		
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
			return loc;
		}
		
	}
}
