package catwalks.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.network.PacketBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import catwalks.Conf;
import catwalks.network.messages.PacketClientPortConnection;
import catwalks.network.messages.PacketNodeClick;
import catwalks.network.messages.PacketNodeConnect;
import catwalks.network.messages.PacketNodeInteract;
import catwalks.network.messages.PacketUpdateNode;
import catwalks.network.messages.PacketUpdatePort;
import io.netty.buffer.Unpooled;

public class NetworkHandler {

	private static Logger logger = LogManager.getLogger("Catwalks-Packets");

	public static void notifyPacketHandling(boolean serverSide, String message, Object... args) {
		notifyPacketHandling(serverSide, String.format(message, args));
	}
	
	public static void notifyPacketHandling(boolean serverSide, String message) {
		if(Conf.logPackets) {
			logger.info(( serverSide ? "[S] " : "[C] ") + message);
		}
	}
	
	public static SimpleNetworkWrapper network;
	
	public static void init() {
		int i = 1;
		network = NetworkRegistry.INSTANCE.newSimpleChannel("Catwalks");
	    network.registerMessage(PacketUpdateNode.Handler.class, PacketUpdateNode.class, i++, Side.CLIENT);
	    network.registerMessage(PacketNodeClick.Handler.class, PacketNodeClick.class, i++, Side.SERVER);
	    network.registerMessage(PacketNodeInteract.Handler.class, PacketNodeInteract.class, i++, Side.SERVER);
	    network.registerMessage(PacketUpdatePort.Handler.class, PacketUpdatePort.class, i++, Side.CLIENT);
	    network.registerMessage(PacketNodeConnect.Handler.class, PacketNodeConnect.class, i++, Side.SERVER);
	    network.registerMessage(PacketClientPortConnection.Handler.class, PacketClientPortConnection.class, i++, Side.CLIENT);
	}

	public static PacketBuffer createBuffer() {
		return new PacketBuffer(Unpooled.buffer());
	}
	
}
