package catwalks.register;

import catwalks.CatwalksMod;
import catwalks.node.EntityNodeBase;
import catwalks.node.render.RenderNode;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class NodeRegister {

	public static void register() {
		int id = 0;
		EntityRegistry.registerModEntity(EntityNodeBase.class, "node", id++, CatwalksMod.INSTANCE, 32, Integer.MAX_VALUE, false);
	}
	
	public static void initRender() {
		RenderingRegistry.registerEntityRenderingHandler(EntityNodeBase.class, (manager) -> new RenderNode(manager));
	}
	
}
