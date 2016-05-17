package catwalks.node;

import catwalks.Const;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;

public class NodeParticleEmitter extends NodeBase {

	public NodeParticleEmitter(EntityNodeBase entity) {
		super(entity);
	}

	@Override
	public void onTick() {
		Vec3d look = entity.getLook(1).scale(0.25);
		for (int i = 0; i < Const.RAND.nextInt(5); i++) {
			entity.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
					entity.posX+rand(-0.0625, 0.0625), entity.posY+rand(-0.0625,  0.0625 ), entity.posZ+rand(-0.0625,  0.0625 ),
					look.xCoord+rand(-0.03125, 0.03125), look.yCoord+rand(-0.03125, 0.03125), look.zCoord+rand(-0.03125, 0.03125), new int[0]);
		}
	}
	
	public double rand(double min, double max) {
		return ( Const.RAND.nextDouble()*(max-min) )+min;
	}

}
