package catwalks;

import catwalks.movement.MovementHandler;
import catwalks.proxy.CommonProxy;
import catwalks.register.BlockRegister;
import catwalks.register.ItemRegister;
import catwalks.register.RecipeRegister;
import catwalks.util.WrenchChecker;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CatwalksMod.MODID, version = CatwalksMod.VERSION,
	guiFactory = "catwalks.gui.CatwalksModConfGuiFactory")
public class CatwalksMod {
	public static final String MODID = "catwalks";
    public static final String VERSION = "0.2.0";
    
    @SidedProxy(serverSide="catwalks.proxy.CommonProxy", clientSide="catwalks.proxy.ClientProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	BlockRegister.register();
    	ItemRegister.register();
    	RecipeRegister.register();
    	Conf.loadConfigsFromFile(event.getSuggestedConfigurationFile());
    	MinecraftForge.EVENT_BUS.register(proxy);
    	proxy.preInit();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	WrenchChecker.init();
		MovementHandler.INSTANCE.getClass(); // just to load the class
    }
    
    public static CreativeTabs tab = new CreativeTabs("tabCatwalks") {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return Item.getItemFromBlock( BlockRegister.catwalk );
		}
	};
}
