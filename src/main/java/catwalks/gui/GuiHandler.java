package catwalks.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

import catwalks.CatwalksMod;
import catwalks.Const;
import catwalks.gui.nodemanipulator.GuiNodeConfItem;
import catwalks.gui.nodemanipulator.NodeConfItemContainer;

public class GuiHandler implements IGuiHandler {
	
	List<Function<GuiParams, Object>> serverHandlers = new ArrayList<>();
	List<Function<GuiParams, Object>> clientHandlers = new ArrayList<>();
	int index = 0;
	
	public static final GuiHandler INSTANCE = new GuiHandler();
	
	public GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(CatwalksMod.INSTANCE, this);
		Const.GUI.NODE_MANIPULATOR = register(
				(params) -> new NodeConfItemContainer(params.player),
				(params) -> new GuiNodeConfItem((NodeConfItemContainer) params.container())
		);
	}
	
	public int register(Function<GuiParams, Object> server, Function<GuiParams, Object> client) {
		serverHandlers.add(server);
		clientHandlers.add(client);
		return index++;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID >= serverHandlers.size())
			return null;
		return serverHandlers.get(ID).apply(new GuiParams(player, world, x, y, z, null));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID >= clientHandlers.size())
			return null;
		return clientHandlers.get(ID).apply(new GuiParams(player, world, x, y, z, serverHandlers.get(ID)));
	}
	
	public static class GuiParams {
		private Function<GuiParams, Object> containerProducer;
		public EntityPlayer player;
		public World world;
		int x, y, z;
		
		public GuiParams(EntityPlayer player, World world, int x, int y, int z, Function<GuiParams, Object> containerProducer) {
			super();
			this.player = player;
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			this.containerProducer = containerProducer;
		}
		
		public Object container() {
			return containerProducer.apply(this);
		}
	}
}
