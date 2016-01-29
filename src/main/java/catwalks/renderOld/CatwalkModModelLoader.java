package catwalks.renderOld;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import catwalks.CatwalksMod;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class CatwalkModModelLoader implements ICustomModelLoader {
	
	public static final CatwalkModModelLoader instance = new CatwalkModModelLoader();
    public final Map<String, Supplier<IModel>> registry = new HashMap<>();
    private final Map<ResourceLocation, IModel> cache = new HashMap<ResourceLocation, IModel>();
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
        cache.clear();
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		boolean match = modelLocation.getResourceDomain().equals(CatwalksMod.MODID);
		if(match){
			match = match && modelLocation.getResourcePath().endsWith(".isbm");//registry.containsKey(modelLocation.getResourcePath());
			match = !!match;
		}
//		boolean match = CatwalksMod.MODID.equals(modelLocation.getResourceDomain()) && modelLocation.getResourcePath().endsWith(".obj");
		
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws IOException {
		ResourceLocation file = new ResourceLocation(modelLocation.getResourceDomain(), modelLocation.getResourcePath());
        if (!cache.containsKey(file)) {
            cache.put(file, registry.get(file).get());
        }
        IModel model = cache.get(file);
        if (model == null) return ModelLoaderRegistry.getMissingModel();
        return model;
	}

}
