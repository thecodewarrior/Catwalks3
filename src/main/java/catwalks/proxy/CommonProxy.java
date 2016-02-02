package catwalks.proxy;

import java.util.List;

import codechicken.lib.raytracer.RayTracer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;

public class CommonProxy {
	public void preInit() {}
	
	public EntityPlayer getPlayerLooking(Vec3 start, Vec3 end) {
		EntityPlayer player = null;
		List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		
		for (final EntityPlayerMP p : players) { // for each player
			Vec3 lookStart = RayTracer.getStartVec(p);
			Vec3 lookEnd   = RayTracer.getEndVec(p);
			double lookDistance = RayTracer.getBlockReachDistance(p);
			
			double dStart  = lookStart.distanceTo(start);
			double dEnd    = lookEnd  .distanceTo(start);
			
			double dStart_ = lookStart.distanceTo(end);
			double dEnd_   = lookEnd  .distanceTo(end);
			
			
			if(dStart + dEnd == lookDistance && dStart_ + dEnd_ == lookDistance) {
				player = p; break;
			}
		}
		return player;
	}
}
