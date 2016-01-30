package catwalks.render;

import catwalks.CatwalksMod;
import catwalks.render.catwalk.CatwalkModel;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ModelLoaderCatwalksMod implements ICustomModelLoader {
	public static final ModelLoaderCatwalksMod instance = new ModelLoaderCatwalksMod();
	
	public final String SMART_MODEL_RESOURCE_LOCATION = "models/block/smartmodel/";
	
	@Override
	public boolean accepts(ResourceLocation resourceLocation) {
		boolean match = resourceLocation.getResourceDomain().equals(CatwalksMod.MODID);
		if(match) {
			match = match && resourceLocation.getResourcePath().startsWith(SMART_MODEL_RESOURCE_LOCATION);
		}
		return match;
	}
	
	@Override
	public IModel loadModel(ResourceLocation resourceLocation) {
		String resourcePath = resourceLocation.getResourcePath();
		if (!resourcePath.startsWith(SMART_MODEL_RESOURCE_LOCATION)) {
			assert false : "loadModel expected " + SMART_MODEL_RESOURCE_LOCATION + " but found " + resourcePath;
		}
		String modelName = resourcePath.substring(SMART_MODEL_RESOURCE_LOCATION.length());

		if (modelName.equals("catwalk")) {
			return new CatwalkModel();
		} else {
			return ModelLoaderRegistry.getMissingModel();
		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	private IResourceManager resourceManager;
}
