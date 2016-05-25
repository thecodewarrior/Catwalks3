package catwalks.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraft.network.PacketBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import catwalks.Conf;
import catwalks.network.messages.*;
import io.netty.buffer.Unpooled;
import mcjty.lib.network.PacketHandler;

public class NetworkHandler {

	private static Logger logger = LogManager.getLogger("Catwalks-Packets");

	public static void notifyPacketHandling(String message, Object... args) {
		notifyPacketHandling(String.format(message, args));
	}
	
	public static void notifyPacketHandling(String message) {
		if(Conf.logPackets) {
			logger.info(message);
		}
	}
	
	public static SimpleNetworkWrapper network;
	
	public static void init() {
		network = NetworkRegistry.INSTANCE.newSimpleChannel("Catwalks");
		int i = PacketHandler.registerMessages(network);
		
	    network.registerMessage(PacketServerContainerCommand.Handler.class, PacketServerContainerCommand.class, i++, Side.SERVER);
		
	    network.registerMessage(PacketNodeClick.Handler.class, PacketNodeClick.class, i++, Side.SERVER);
	    network.registerMessage(PacketNodeInteract.Handler.class, PacketNodeInteract.class, i++, Side.SERVER);
	    network.registerMessage(PacketNodeConnect.Handler.class, PacketNodeConnect.class, i++, Side.SERVER);
	    network.registerMessage(PacketNodeSettingsUpdate.Handler.class, PacketNodeSettingsUpdate.class, i++, Side.SERVER);
	    network.registerMessage(PacketNodeSettingsQuery.Handler.class, PacketNodeSettingsQuery.class, i++, Side.SERVER);
	    network.registerMessage(PacketNodeRelocate.Handler.class, PacketNodeRelocate.class, i++, Side.SERVER);
		
	    network.registerMessage(PacketClientGuiCommand.Handler.class, PacketClientGuiCommand.class, i++, Side.CLIENT);
	    
		network.registerMessage(PacketUpdateNode.Handler.class, PacketUpdateNode.class, i++, Side.CLIENT);
	    network.registerMessage(PacketUpdatePort.Handler.class, PacketUpdatePort.class, i++, Side.CLIENT);
	    network.registerMessage(PacketClientPortConnection.Handler.class, PacketClientPortConnection.class, i++, Side.CLIENT);
	    network.registerMessage(PacketNodeSettingsResponse.Handler.class, PacketNodeSettingsResponse.class, i++, Side.CLIENT);
	    
	}

	public static PacketBuffer createBuffer() {
		return new PacketBuffer(Unpooled.buffer());
	}
	
}
