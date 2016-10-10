package catwalks.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by TheCodeWarrior
 */
public class StateHandle {
	
	protected static Map<ModelResourceLocation, IBakedModel> cache = new HashMap<>();
	protected static Set<ModelResourceLocation> missingModels = new HashSet<>();
	
	public final ModelResourceLocation loc;
	
	public StateHandle(ModelResourceLocation loc) {
		this.loc = loc;
	}
	
	@Nonnull
	public IBakedModel get() {
		return getModel(this.loc);
	}
	
	public StateHandle load() {
		getModel(this.loc);
		return this;
	}
	
	public StateHandle reload() {
		loadModel(this.loc);
		return this;
	}
	
	public boolean isMissing() {
		getModel(this.loc);
		return missingModels.contains(this.loc);
	}
	
	// ========================================================= STATIC METHODS
	
	//region creators
	
	@Nonnull
	public static StateHandle of(String model, String variant)
	{
		return of(new ResourceLocation(model), variant);
	}
	
	@Nonnull
	public static StateHandle of(ResourceLocation model, String variant)
	{
		return of(new ModelResourceLocation(model, variant));
	}
	
	@Nonnull
	public static StateHandle ofLazy(String model, String variant)
	{
		return ofLazy(new ResourceLocation(model), variant);
	}
	
	@Nonnull
	public static StateHandle ofLazy(ResourceLocation model, String variant)
	{
		return ofLazy(new ModelResourceLocation(model, variant));
	}
	
	@Nonnull
	public static StateHandle ofLazy(ModelResourceLocation loc)
	{
		return new StateHandle(loc);
	}
	
	@Nonnull
	public static StateHandle of(ModelResourceLocation loc)
	{
		return new StateHandle(loc).reload();
	}
	
	//endregion
	
	@Nonnull
	private static IBakedModel getModel(@Nonnull ModelResourceLocation loc)
	{
		IBakedModel model = cache.get(loc);
		if(model != null)
			return model;
		
		loadModel(loc);
		model = cache.get(loc);
		if(model == null)
			throw new IllegalStateException("Cache contained null even after loading for model " + loc);
		return model;
	}
	
	private static void loadModel(@Nonnull ModelResourceLocation loc)
	{
		try
		{
			IModel mod = ModelLoaderRegistry.getModelOrMissing(loc);
			if(mod == ModelLoaderRegistry.getMissingModel())
				missingModels.add(loc);
			IBakedModel model = mod.bake(mod.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
			cache.put(loc, model);
		}
		catch (Exception e)
		{
			throw new ReportedException(new CrashReport("Error loading custom model " + loc, e));
		}
	}
	
	public static void init()
	{
		IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
		if (rm instanceof IReloadableResourceManager)
		{
			((IReloadableResourceManager) rm).registerReloadListener(__ -> {
				cache.clear();
				missingModels.clear();
			});
		}
	}
}
