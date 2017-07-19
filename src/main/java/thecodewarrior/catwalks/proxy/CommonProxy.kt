package thecodewarrior.catwalks.proxy

import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.plus
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import thecodewarrior.catwalks.Const

/**
 * TODO: Document file CommonProxy
 *
 * Created by TheCodeWarrior
 */
open class CommonProxy {
    init { MinecraftForge.EVENT_BUS.register(this) }

    open fun pre(e: FMLPreInitializationEvent) {}
    open fun init(e: FMLInitializationEvent) {}
    open fun post(e: FMLPostInitializationEvent) {}


    @SubscribeEvent
    fun sound(e: LivingEvent.LivingUpdateEvent) {
        if(e.entity.world.isRemote) return;
        stepSoundTick(e.entity)
    }

    fun stepSoundTick(entity: Entity) {
        val pos = BlockPos(entity.positionVector + vec(0, 0.01, 0))
        val state = entity.world.getBlockState(pos)
        if(state.block == Const.B_CATWALK) {
            if(!entity.noClip) {
                if (_canTriggerWalking(entity, emptyArray()) as Boolean && (!entity.onGround || !entity.isSneaking) && !entity.isRiding) {
                    val distWalked = entity._distanceWalkedOnStepModified
                    val nextStep = mh_get_nextStepDistance(entity)
                    if ((distWalked+1.5) > nextStep) {
                        mh_set_nextStepDistance(entity, entity._distanceWalkedOnStepModified + 2)
                        val soundtype = state.block.getSoundType(state, entity.world, pos, entity)

                        if(entity is EntityPlayer) {
                            entity.world.playSound(entity, entity.posX, entity.posY, entity.posZ,
                                    soundtype.getStepSound(), entity.soundCategory, soundtype.getVolume() * 0.15F, soundtype.getPitch())
                        } else {
                            entity.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch())
                        }
                    }
                }
            }
        }
    }

    private var mh_set_nextStepDistance: (Entity, Int) -> Unit = MethodHandleHelper.wrapperForSetter(Entity::class.java, "nextStepDistance", "field_70150_b")
    @Suppress("UNCHECKED_CAST")
    private var mh_get_nextStepDistance: (Entity) -> Int = MethodHandleHelper.wrapperForGetter(Entity::class.java, "nextStepDistance", "field_70150_b") as (Entity) -> Int
    private var Entity._distanceWalkedOnStepModified: Int by MethodHandleHelper.delegateForReadWrite(
            Entity::class.java, "distanceWalkedOnStepModified", "field_82151_R")
    @Suppress("UNCHECKED_CAST")
    private var _canTriggerWalking = MethodHandleHelper.wrapperForMethod(
            Entity::class.java, "canTriggerWalking", "func_70041_e_")
    @Suppress("UNCHECKED_CAST")
    private var _playStepSound = MethodHandleHelper.wrapperForMethod(
            Entity::class.java, "playStepSound", "func_180429_a",
            BlockPos::class.java, Block::class.java)
}

