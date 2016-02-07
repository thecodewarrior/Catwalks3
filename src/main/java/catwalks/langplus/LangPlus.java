package catwalks.langplus;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.common.MinecraftForge;

public class LangPlus implements IResourceManagerReloadListener {

	public static final LangPlus instance = new LangPlus();
	private Set<String> domains = new HashSet<>();
	
	public static void addMod(String modid) {
		instance.domains.add(modid);
	}
	
	private LangPlus() {
		MinecraftForge.EVENT_BUS.register(this);
		( (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager() ).registerReloadListener(this);
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		String lang = Minecraft.getMinecraft().gameSettings.language;
		
		for (String domain : domains) {
			StringTranslate.inject(LangPlusParser.parse(domain + ":langplus/" + lang + "/"));
		}
		
		
	}
	
	
	
}
