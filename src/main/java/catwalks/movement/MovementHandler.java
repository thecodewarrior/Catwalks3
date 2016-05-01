package catwalks.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import catwalks.Conf;
import catwalks.block.IDecoratable;
import catwalks.block.extended.ICustomLadder;
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
	
	public final UUID speedModifierUUID = UUID.randomUUID();
	public final String speedModifierID = "catwalkmod.speedup";
	public final double speedModifierBaseValue = 0.2D;
	public final int speedModifierOperation = 2;
	
	public double currentSpeedLevel = -1;
	
	private MovementHandler() {
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
	
	@SubscribeEvent
	public void playerUpdate(LivingUpdateEvent event) {
		if(!( event.entity instanceof EntityPlayer )) {
			return;
		}
		EntityPlayer entity = (EntityPlayer) event.entity;
		CatwalkEntityProperties ep = getOrCreateEP(entity);
		
		World world = entity.worldObj;
		
		double climbSpeedMultiplier = Double.NEGATIVE_INFINITY;
		double fallSpeedMultiplier = Double.POSITIVE_INFINITY;
		double horizontalSpeedMultiplier = Double.POSITIVE_INFINITY;
		
		for(BlockPos pos : eachTouching(event.entityLiving, false, new Vector3(0,0,0), new Vector3(0,0,0)) ) {
			Block block = world.getBlockState(pos).getBlock();
			if( block instanceof ICustomLadder) {
				ICustomLadder icl = (ICustomLadder) block;
//				&& ((ICustomLadder) block).shouldApply(world, pos, entity)
				if(icl.shouldApplyClimbing(world, pos, entity))
					climbSpeedMultiplier = Math.max(climbSpeedMultiplier, ((ICustomLadder) block).climbSpeed(world, pos, entity) );
				
				if(icl.shouldApplyFalling(world, pos, entity))
					fallSpeedMultiplier  = Math.min(fallSpeedMultiplier, ((ICustomLadder) block).fallSpeed(world, pos, entity) );
				horizontalSpeedMultiplier = Math.min(horizontalSpeedMultiplier, ((ICustomLadder) block).horizontalSpeed(world, pos, entity) );
			}
		}
		
		if(Double.isInfinite(climbSpeedMultiplier)) {
			if(entity.motionY > 0.2 & ep.lastTickLadderSpeed > 0) {
				entity.motionY = 0.2;
				ep.lastTickLadderSpeed = -1;
			}
		}
		
		double horizontalSpeed = 0.15 * horizontalSpeedMultiplier;
		double fallSpeed = 0.15 * fallSpeedMultiplier;
		double climbSpeed = 0.2 * climbSpeedMultiplier;
		
		entity.motionX = MathHelper.clamp_double(entity.motionX, -horizontalSpeed, horizontalSpeed);
        entity.motionZ = MathHelper.clamp_double(entity.motionZ, -horizontalSpeed, horizontalSpeed);
		
        if(Double.isFinite(fallSpeedMultiplier)) { // slow down the player if a fall speed was found
        	entity.fallDistance = 0.0F;

            if (entity.motionY < -fallSpeed)
            {
                entity.motionY = -fallSpeed;
            }
            
            if (entity.isSneaking() && entity.motionY < 0.0D)
            {
                entity.motionY = 0.0D;
            }
        }
        
        if(Double.isFinite(climbSpeedMultiplier)) { // do climbing stuff if a climb speed was found
			if(ep.lastTickLadderSpeed > climbSpeedMultiplier) {
				entity.motionY = climbSpeed;
			}
		
			ep.lastTickLadderSpeed = climbSpeedMultiplier;
			
	        if (entity.isCollidedHorizontally && entity.motionY <= climbSpeed)
	        {
	            entity.motionY = climbSpeed;
	        }
        }
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
	
	/**
	 * 
	 * @param entity Entity to check
	 * @param checkFullBlocks Slightly expand the bounding box to catch blocks the player is only touching but not inside
	 * @param offsetMin The vector to offset the lower point of the bounding box
	 * @param offsetMax The vector to offset the upper point of the bounding box
	 * @return
	 */
	public List<BlockPos> eachTouching(EntityLivingBase entity, boolean checkFullBlocks, Vector3 offsetMin, Vector3 offsetMax) {
		List<BlockPos> positions = new ArrayList<>();
		
		AxisAlignedBB bb = entity.getEntityBoundingBox();
		double buf = checkFullBlocks ? 1/1024F :0; // so when the player is touching a full 1m cube they can climb it.
        int mX = MathHelper.floor_double(bb.minX-buf + offsetMin.x);
        int mY = MathHelper.floor_double(bb.minY     + offsetMin.y);
        int mZ = MathHelper.floor_double(bb.minZ-buf + offsetMin.z);
        for (int y2 = mY; y2 < bb.maxY+offsetMax.y-offsetMin.y; y2++)
        {
            for (int x2 = mX; x2 < bb.maxX+buf+offsetMax.x-offsetMin.x; x2++)
            {
                for (int z2 = mZ; z2 < bb.maxZ+buf+offsetMax.z-offsetMin.z; z2++)
                {
                	positions.add( new BlockPos(x2, y2, z2) );
                }
            }
        }
        return positions;
	}
	
	@SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {		
    	if( event.phase == Phase.END) {
    		List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
    		
    		if(Conf.catwalkSpeed == 0) {
    			for (EntityPlayerMP player : players) {
    				IAttributeInstance attrInstance = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
    				AttributeModifier m = attrInstance.getModifier(speedModifierUUID);
    				if(m != null)
    					attrInstance.removeModifier(m);
    			}
    			currentSpeedLevel = 0;
    			return;
    		}
    		
    		if(Conf.catwalkSpeed != currentSpeedLevel) {
    			speedModifier = new AttributeModifier(speedModifierUUID, speedModifierID, speedModifierBaseValue * Conf.catwalkSpeed, 2);
    			speedModifier.setSaved(false);
    			currentSpeedLevel = Conf.catwalkSpeed;
    		}
    		
    		
    		
    		for (EntityPlayerMP player : players) { // for each player
    			// find any catwalks
				BlockPos speedpos = findCollidingBlock(player, true, (BlockPos pos) -> {
					IBlockState state = player.worldObj.getBlockState(pos);
					Block b = state.getBlock();
					if(b instanceof IDecoratable) {
						IDecoratable idec = (IDecoratable) b;
						return idec.canGiveSpeedBoost(player.worldObj, pos) && idec.hasDecoration(player.worldObj, pos, "speed");
					}
					return false;
				});
				
				IAttributeInstance attrInstance = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
				AttributeModifier m = attrInstance.getModifier(speedModifierUUID);
				
				CatwalkEntityProperties ep = getOrCreateEP(player);
				
				if(speedpos == null && ep.jumpTimer > 0) {
					speedpos = findCollidingBlock(player, true, new Vector3(0, -2, 0), new Vector3(0, 0, 0), (BlockPos pos) -> {
						IBlockState state = player.worldObj.getBlockState(pos);
						Block b = state.getBlock();
						if(b instanceof IDecoratable) {
							IDecoratable idec = (IDecoratable) b;
							if(idec.canGiveSpeedBoost(player.worldObj, pos))
								return idec.hasDecoration(player.worldObj, pos, "speed");
						}
						return false;
					});
					if(speedpos == null) { ep.jumpTimer = 0; }
				}
				
				if(ep.jumpTimer > 0) {
					ep.jumpTimer--;
				}
				
				if(speedpos == null) { // if no blocks found
					if(m != null) { // and speed modifier is still applied
						attrInstance.removeModifier(m); // remove it
					}
					continue;
				}
								
				if(player.motionY > 0) {
					ep.jumpTimer = 30;
				}
				
				if(m != speedModifier && m != null) {
					attrInstance.removeModifier(m);
					m = null;
				}
				
				if(m == null) { // if modifier isn't applied or the amount has changed
					attrInstance.applyModifier(speedModifier); // re-apply it
				}
			} // end for
    		
    	}
	}
	
}
