package catwalks.render;

import com.google.common.base.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TheCodeWarrior
 */
public class StateHandle {
	
	protected static Map<ModelResourceLocation, IBakedModel> cache = new HashMap<>();
	
	public final ModelResourceLocation loc;
	
	public StateHandle(ModelResourceLocation loc) {
		this.loc = loc;
	}
	
	public IBakedModel get() {
		return loadModel(this);
	}
	
	// ========================================================= STATIC METHODS
	
	@Nonnull
	public static StateHandle of(String model, String variant)
	{
		return new StateHandle(new ModelResourceLocation(new ResourceLocation(model), variant));
	}
	
	@Nonnull
	public static StateHandle of(ResourceLocation model, String variant)
	{
		return new StateHandle(new ModelResourceLocation(model, variant));
	}
	
	private static IBakedModel loadModel(StateHandle handle)
	{
		IBakedModel model = cache.get(handle.loc);
		if (model != null)
			return model;
		
		try
		{
			IModel mod = ModelLoaderRegistry.getModelOrMissing(handle.loc);
			model = mod.bake(mod.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
			cache.put(handle.loc, model);
			return model;
		}
		catch (Exception e)
		{
			throw new ReportedException(new CrashReport("Error loading custom model " + handle.loc, e));
		}
	}
	
	public static void init()
	{
		IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
		if (rm instanceof IReloadableResourceManager)
		{
			((IReloadableResourceManager) rm).registerReloadListener(new IResourceManagerReloadListener()
			{
				@Override
				public void onResourceManagerReload(IResourceManager __)
				{
					cache.clear();
				}
			});
		}
	}
}
