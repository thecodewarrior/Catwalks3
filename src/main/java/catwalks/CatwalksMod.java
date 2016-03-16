package catwalks;

import catwalks.movement.MovementHandler;
import catwalks.proxy.CommonProxy;
import catwalks.register.BlockRegister;
import catwalks.register.ItemRegister;
import catwalks.util.WrenchChecker;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = CatwalksMod.MODID, version = CatwalksMod.VERSION)
public class CatwalksMod {
    public static final String MODID = "catwalks";
    public static final String VERSION = "0.0.0a2";
    
    public static boolean developmentEnvironment = (Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
    
    @SidedProxy(serverSide="catwalks.proxy.CommonProxy", clientSide="catwalks.proxy.ClientProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	BlockRegister.register();
    	ItemRegister.register();
    	MinecraftForge.EVENT_BUS.register(proxy);
    	proxy.preInit();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	WrenchChecker.init();
    	MovementHandler justToLoadTheClass = MovementHandler.INSTANCE;
    }
    
    public static CreativeTabs tab = new CreativeTabs("tabCatwalks") {
		@Override
		@SideOnly(Side.CLIENT)
		public Item getTabIconItem() {
			return Item.getItemFromBlock( BlockRegister.catwalk );
		}
	};
}
