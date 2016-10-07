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
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by TheCodeWarrior
 */
public class StateHandle {
	
	protected static Map<ModelResourceLocation, IBakedModel> cache = new HashMap<>();
	protected static Map<ModelResourceLocation, IBakedModel> nullcache = new HashMap<>();
	
	public final ModelResourceLocation loc;
	
	public StateHandle(ModelResourceLocation loc) {
		this.loc = loc;
	}
	
	@Nonnull
	public IBakedModel get() {
		return loadModel(this.loc);
	}
	
	@Nullable
	public IBakedModel getNull() {
		return loadModelNullable(this.loc);
	}
	
	public StateHandle load() {
		loadModel(this.loc);
		return this;
	}
	
	public StateHandle loadNull() {
		loadModelNullable(this.loc);
		return this;
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
	
	@Nonnull
	private static IBakedModel loadModel(@Nonnull ModelResourceLocation loc)
	{
		if (cache.containsKey(loc))
			return cache.get(loc);
		IBakedModel model = null;
		
		try
		{
			IModel mod = ModelLoaderRegistry.getModelOrMissing(loc);
			model = mod.bake(mod.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
			cache.put(loc, model);
			return model;
		}
		catch (Exception e)
		{
			throw new ReportedException(new CrashReport("Error loading custom model " + loc, e));
		}
	}
	
	@Nullable
	private static IBakedModel loadModelNullable(@Nonnull ModelResourceLocation loc)
	{
		if (nullcache.containsKey(loc))
			return nullcache.get(loc);
		IBakedModel model = null;
		
		try
		{
			IModel mod = ModelLoaderRegistry.getModel(loc);
			model = mod.bake(mod.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
			nullcache.put(loc, model);
			return model;
		}
		catch (Exception e)
		{
			FMLLog.log("Catwalks", Level.WARN, new ReportedException(new CrashReport("Error loading custom model " + loc + ", defaulting to null", e)), "");
			nullcache.put(loc, null);
			return null;
		}
	}
	
	public static void init()
	{
		IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
		if (rm instanceof IReloadableResourceManager)
		{
			((IReloadableResourceManager) rm).registerReloadListener(__ -> {
				cache.clear();
				nullcache.clear();
			});
		}
	}
}
