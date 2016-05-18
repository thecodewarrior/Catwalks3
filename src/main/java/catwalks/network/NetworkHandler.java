package catwalks.network;

import catwalks.network.messages.PacketNodeClick;
import catwalks.network.messages.PacketNodeInteract;
import catwalks.network.messages.PacketUpdateNode;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {

	public static SimpleNetworkWrapper network;
	
	public static void init() {
		int i = 0;
		network = NetworkRegistry.INSTANCE.newSimpleChannel("Catwalks");
	    network.registerMessage(PacketUpdateNode.Handler.class, PacketUpdateNode.class, i++, Side.CLIENT);
	    network.registerMessage(PacketNodeClick.Handler.class, PacketNodeClick.class, i++, Side.SERVER);
	    network.registerMessage(PacketNodeInteract.Handler.class, PacketNodeInteract.class, i++, Side.SERVER);
	}
	
}
