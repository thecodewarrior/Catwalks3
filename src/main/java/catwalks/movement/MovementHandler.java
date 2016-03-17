package catwalks.movement;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import catwalks.CatwalksConfig;
import catwalks.block.BlockCatwalk;
import catwalks.block.IDecoratable;
import catwalks.shade.ccl.vec.BlockCoord;
import catwalks.shade.ccl.vec.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class MovementHandler {

	public static final MovementHandler INSTANCE = new MovementHandler();
	
	public AttributeModifier speedModifier;
	public String catwalkDataId = "catwalkmod.catwalkdata";
	
	private MovementHandler() {
		speedModifier =  new AttributeModifier(
    			"catwalkmod.speedup",
    			0.20000000298023224D,
    			2);
    	speedModifier.setSaved(false);
    	MinecraftForge.EVENT_BUS.register(this);
	}
	
	private CatwalkEntityProperties getOrCreateEP(Entity entity) {
		CatwalkEntityProperties catwalkEP = (CatwalkEntityProperties)entity.getExtendedProperties(catwalkDataId);
		if(catwalkEP == null) {
			catwalkEP = new CatwalkEntityProperties();
			entity.registerExtendedProperties(catwalkDataId, catwalkEP);
		}
		return catwalkEP;
	}
	
//	@SubscribeEvent
//	public void onLivingUpdate(LivingUpdateEvent event) {
//		
//		if(!( event.entity instanceof EntityPlayer )) {
//			return; // non-players won't be able to pathfind up the ladders, so we can cut corners and safely ignore them
//		}
//		
//		// copying minecraft's ladder code, just with customizable velocity
//		
//		
//		BlockPos coord = getLadderCoord(event.entityLiving); // find any caged ladders
//		EntityLivingBase e = event.entityLiving;
//
//		CatwalkEntityProperties catwalkEP = getOrCreateEP(event.entity); // get entity properties object "for future uses"
//		if(coord != null) { // if the block was found (y=-1 if not found)
//
//			IBlockState state = event.entity.worldObj.getBlockState(pos);
//			
//			Block b = event.entity.worldObj.getBlock(coord.x, coord.y, coord.z); // get the custom ladder block
//			ICustomLadder icl = CustomLadderRegistry.getCustomLadderOrNull(b);
//			double   upSpeed = icl.getLadderVelocity(e.worldObj, coord.x, coord.y, coord.z, e);
//			double downSpeed = icl.getLadderFallVelocity(e.worldObj, coord.x, coord.y, coord.z, e); // get custom fall velocity
//			double motY = e.posY - catwalkEP.lastPosY;
//			
//			
//			if(e.isCollidedHorizontally) { // entity is smashed up against something
//				if(e.motionY < upSpeed) {
//					e.motionY = upSpeed; // set the entity's upward velocity to the custom value
//					catwalkEP.highSpeedLadder = true; // now when they stop they'll be slowed down to 0.2 blocks/tick when they stop
//				}
//			} else {
//				if(downSpeed > 0)
//					e.fallDistance = 0.0F; // reset fall distance to prevent fall damage
//				
//                if (downSpeed > 0 && e.motionY < -downSpeed) // if the entity is falling faster than custom fall velocity
//                {
//                    e.motionY = -downSpeed; // set entity's velocity to the custom fall velocity
//                }
//
//                boolean shouldStopOnLadder = icl.shouldHoldOn(e.worldObj, coord.x, coord.y, coord.z, e);
//                boolean shouldClimbDown = icl.shouldClimbDown(e.worldObj, coord.x, coord.y, coord.z, e);
//                double climbDownSpeed = icl.getClimbDownVelocity(e.worldObj, coord.x, coord.y, coord.z, e);
//                
//                if (shouldStopOnLadder && !shouldClimbDown && e.motionY < 0.0D) { // should stop and entity is moving down
//    				e.motionY = 0.0D; // don't you DARE move down
//                }
//                
//                if(shouldClimbDown && e.motionY <= 0) {
//                	e.motionY = -climbDownSpeed;
//                }
//                
//                
//			}
//			if(motY >= 0) {
//				e.fallDistance = 0.0F;
//			}
//			
//			
//			
//			
//			double dY = e.posY - catwalkEP.lastStepY;
//			
//			double distanceClimbed = Math.abs(dY);
//			double distanceRequired = upSpeed * 10;
//			
//			if(catwalkEP.isSlidingDownLadder && dY >= 0) {
//				distanceRequired = 0;
//			}
//			catwalkEP.isSlidingDownLadder = (dY < 0);
//			
//			if(distanceClimbed > distanceRequired && distanceRequired > 0) {
//				catwalkEP.lastStepX = e.posX;
//				catwalkEP.lastStepY = e.posY;
//				catwalkEP.lastStepZ = e.posZ;
//				boolean shouldPlay = dY < 0 ?
//						icl.shouldPlayStepSound(e.worldObj, coord.x, coord.y, coord.z, e, true) :
//						icl.shouldPlayStepSound(e.worldObj, coord.x, coord.y, coord.z, e, false);
//						
//						
//		        if(shouldPlay) {
//		        	Block.SoundType soundtype = e.worldObj.getBlock(coord.x, coord.y, coord.z).stepSound;
//					e.playSound(soundtype.getStepResourcePath(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
//		        }
//			}
//			
//		}
//		
//		catwalkEP.lastPosX = e.posX;
//		catwalkEP.lastPosY = e.posY;
//		catwalkEP.lastPosZ = e.posZ;
//		
//		if(catwalkEP.highSpeedLadder && !event.entityLiving.isCollidedHorizontally) {
//			if(event.entity.motionY > 0.2D)
//				event.entity.motionY = 0.2D; // slow down entity once they stop climbing to prevent them flying upwards
//
//			catwalkEP.highSpeedLadder = false;
//		}
//	}
	
	public BlockPos getLadderPos(EntityLivingBase entity) {
		
		return findCollidingBlock(entity, false, (BlockPos pos) -> {
			World w = entity.worldObj;
			IBlockState state = w.getBlockState(pos);
			Block b = state.getBlock();
//			return b instanceof ICustomStair;
			return false;
		});
	}
	
	/**
	 * Shamelessly stolen from {net.minecraftforge.common.ForgeHooks.isLivingOnLadder}
	 * @param entity
	 * @param mat
	 * @return
	 */
	public BlockPos findCollidingBlock(EntityLivingBase entity, boolean onlyInside, Predicate<BlockPos> mat) {
		return findCollidingBlock(entity, onlyInside, Vector3.zero, Vector3.zero, mat);
	}
	public BlockPos findCollidingBlock(EntityLivingBase entity, boolean onlyInside, Vector3 offsetMin, Vector3 offsetMax, Predicate<BlockPos> mat) {
		
		AxisAlignedBB bb = entity.getEntityBoundingBox();
		double buf = onlyInside ? 0 : 1/1024F; // so when the player is touching a full 1m cube they can climb it.
        int mX = MathHelper.floor_double(bb.minX-buf + offsetMin.x);
        int mY = MathHelper.floor_double(bb.minY     + offsetMin.y);
        int mZ = MathHelper.floor_double(bb.minZ-buf + offsetMin.z);
        for (int y2 = mY; y2 < bb.maxY+offsetMax.y-offsetMin.y; y2++)
        {
            for (int x2 = mX; x2 < bb.maxX+buf+offsetMax.x-offsetMin.x; x2++)
            {
                for (int z2 = mZ; z2 < bb.maxZ+buf+offsetMax.z-offsetMin.z; z2++)
                {
                	BlockPos bc = new BlockPos(x2, y2, z2);
                    if (mat.test(bc))
                    {
                        return bc;
                    }
                }
            }
        }
        return null;
	}
	
	@SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {		
    	if( event.phase == Phase.END) {
    		if(CatwalksConfig.speedPotionLevel == 0) {
    			return;
    		}
    		
    		List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
    		
    		for (EntityPlayerMP player : players) { // for each player
    			// find any catwalks
				BlockPos speedpos = findCollidingBlock(player, true, (BlockPos pos) -> {
					IBlockState state = player.worldObj.getBlockState(pos);
					Block b = state.getBlock();
					if(b instanceof IDecoratable) {
						IDecoratable idec = (IDecoratable) b;
						return idec.hasDecoration(player.worldObj, pos, "speed");
					}
					return false;
				});
				
				IAttributeInstance attrInstance = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
				AttributeModifier m = attrInstance.getModifier(speedModifier.getID());
				
				CatwalkEntityProperties ep = getOrCreateEP(player);
				
				if(speedpos == null && ep.jumpTimer > 0) {
					speedpos = findCollidingBlock(player, true, new Vector3(0, -2, 0), new Vector3(0, 0, 0), (BlockPos pos) -> {
						IBlockState state = player.worldObj.getBlockState(pos);
						Block b = state.getBlock();
						if(b instanceof IDecoratable) {
							IDecoratable idec = (IDecoratable) b;
							return idec.hasDecoration(player.worldObj, pos, "speed");
						}
						return false;
					});
					if(speedpos == null) { ep.jumpTimer = 0; }
				}
				
				if(ep.jumpTimer > 0) {
					ep.jumpTimer--;
				}
				
				if(speedpos == null) { // if no catwalks found
					if(m != null) { // and speed modifier is still applied
						attrInstance.removeModifier(speedModifier); // remove it
					}
					continue;
				}
				
				double amt = speedModifier.getAmount() * CatwalksConfig.speedPotionLevel; // roughly the same as a Swiftness I potion
				
				if(player.motionY > 0) {
					ep.jumpTimer = 30;
				}
				
				if(m == null || m.getAmount() != amt ) { // if modifier isn't applied or the amount has changed
					attrInstance.removeModifier(speedModifier); // remove the modifier
					attrInstance.applyModifier(
			        		new AttributeModifier(speedModifier.getID(), "catwalkmod.speedup",
			        				amt, 2)); // re-apply it
				}
			} // end for
    		
    	}
	}
	
}
