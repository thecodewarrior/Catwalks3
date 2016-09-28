package catwalks.langplus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashSet;
import java.util.Set;

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
			LanguageMap.inject(LangPlusParser.parse(domain + ":langplus/" + lang + "/"));
		}
		
		
	}
	
	
	
}
