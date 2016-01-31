package catwalks.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import catwalks.CatwalksMod;
import catwalks.register.BlockRegister;
import catwalks.render.catwalk.CatwalkSmartModel;
import catwalks.texture.TextureGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
//	
//	List<String>
//	
	
	public void preInit() {
//		ModelLoaderRegistry.registerLoader(ModelLoaderCatwalksMod.instance);
		BlockRegister.initRender();
		MinecraftForge.EVENT_BUS.register(TextureGenerator.instance);
		( (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager() ).registerReloadListener(TextureGenerator.instance);
	}
	
	Map<ModelResourceLocation, IBakedModel> models = new HashMap<>();
	
	private void model(String loc, IBakedModel model) {
		models.put(new ModelResourceLocation(CatwalksMod.MODID + ":" + loc), model);
	}
	
	@SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent event) {
		models.clear();
		
		model("catwalk", new CatwalkSmartModel());
		
        for (Entry<ModelResourceLocation, IBakedModel> model : models.entrySet()) {
			
        	Object object =  event.modelRegistry.getObject(model.getKey());
            if (object != null) {
                event.modelRegistry.putObject(model.getKey(), model.getValue());
            }
        	
		}
    }
	
//	@SubscribeEvent
//    public void onModelBakeEvent(ModelBakeEvent event) {
//        Object object =  event.modelRegistry.getObject(CatwalkModel.modelResourceLocation);
//        if (object == null) {
//        	CatwalkModel customModel = new CatwalkModel();
//            event.modelRegistry.putObject(CatwalkModel.modelResourceLocation, customModel);
//        }
//    }
//	@SubscribeEvent
//	public void onTextureStitchEvent(TextureStitchEvent event) {
//		event.map.registerSprite(new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/side/base"));
//		event.map.registerSprite(new ResourceLocation(CatwalksMod.MODID + ":blocks/catwalk/bottom/base"));
//	}
	
//	public void
	
}
