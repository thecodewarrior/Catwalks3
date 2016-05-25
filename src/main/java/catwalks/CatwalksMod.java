package catwalks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

import catwalks.gui.GuiHandler;
import catwalks.movement.MovementHandler;
import catwalks.network.NetworkHandler;
import catwalks.node.NodeUtil;
import catwalks.proxy.CommonProxy;
import catwalks.register.BlockRegister;
import catwalks.register.ItemRegister;
import catwalks.register.NodeRegister;
import catwalks.register.RecipeRegister;
import catwalks.util.Logs;
import catwalks.util.WrenchChecker;
import mcjty.lib.McJtyLib;
import mcjty.lib.base.ModBase;

@Mod(modid = CatwalksMod.MODID, version = CatwalksMod.VERSION,
	guiFactory = "catwalks.gui.CatwalksModConfGuiFactory",
	dependencies="required-after:McJtyLib")
public class CatwalksMod implements ModBase {
	public static final String MODID = "catwalks";
    public static final String VERSION = "0.3.0";
    
    @SidedProxy(serverSide="catwalks.proxy.CommonProxy", clientSide="catwalks.proxy.ClientProxy")
    public static CommonProxy proxy;
    
    @Instance
    public static CatwalksMod INSTANCE;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	BlockRegister.register();
    	ItemRegister.register();
    	RecipeRegister.register();
    	NodeRegister.register();
    	NetworkHandler.init();
    	McJtyLib.preInit(event);
    	
    	Conf.loadConfigsFromFile(event.getSuggestedConfigurationFile());
    	MinecraftForge.EVENT_BUS.register(proxy);
    	proxy.preInit();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	WrenchChecker.init();
    	// just to load the classes and their instances
		MovementHandler.INSTANCE.getClass();
		NodeUtil.INSTANCE.getClass();
		GuiHandler.INSTANCE.getClass();
    }
    
    @Override
	public String getModId() {
		return MODID;
	}
	@Override
	public void openManual(EntityPlayer player, int bookindex, String page) {
		
	}
    
    public static CreativeTabs tab = new CreativeTabs("tabCatwalks") {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return Item.getItemFromBlock( BlockRegister.catwalk );
		}
	};
}
