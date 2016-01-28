package catwalks;

import catwalks.proxy.CommonProxy;
import catwalks.register.BlockRegister;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CatwalksMod.MODID, version = CatwalksMod.VERSION)
public class CatwalksMod {
    public static final String MODID = "catwalks";
    public static final String VERSION = "1.0";
    
    @SidedProxy(serverSide="catwalks.proxy.CommonProxy", clientSide="catwalks.proxy.ClientProxy")
    public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	BlockRegister.register();
    	MinecraftForge.EVENT_BUS.register(proxy);
    	proxy.preInit();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	
    }
}
