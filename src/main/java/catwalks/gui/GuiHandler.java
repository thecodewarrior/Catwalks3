package catwalks.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraftforge.fml.common.network.IGuiHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {

	List<Function<GuiParams, Object>> serverHandlers = new ArrayList<>();
	List<Function<GuiParams, Object>> clientHandlers = new ArrayList<>();
	int index = 0;
	
	public int register(Function<GuiParams, Object> client, Function<GuiParams, Object> server) {
		clientHandlers.add(client);
		serverHandlers.add(server);
		return index++;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID >= serverHandlers.size())
			return null;
		return serverHandlers.get(ID).apply(new GuiParams(player, world, x, y, z));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID >= clientHandlers.size())
			return null;
		return clientHandlers.get(ID).apply(new GuiParams(player, world, x, y, z));
	}

	public static class GuiParams {
		public EntityPlayer player;
		public World world;
		int x, y, z;
		
		public GuiParams(EntityPlayer player, World world, int x, int y, int z) {
			super();
			this.player = player;
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
