package catwalks.util

import catwalks.Const
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Logs {
    private val logger: Logger

    init {
        logger = LogManager.getLogger("Catwalks")
    }

    var prevTicks: Long = -1

    var debugMode = Const.developmentEnvironment
    var doLogging = false

    fun error(message: String, vararg args: Any) {
        logger.log(Level.ERROR, String.format(message, *args))
    }

    fun error(e: Exception, message: String, vararg args: Any) {
        logger.log(Level.ERROR, String.format(message, *args))
        e.printStackTrace()
    }

    fun warn(message: String, vararg args: Any) {
        logger.log(Level.WARN, String.format(message, *args))
    }

    fun log(message: String, vararg args: Any) {
        logger.log(Level.INFO, String.format(message, *args))
    }

    fun debug(message: String, vararg args: Any) {
        if (debugMode) {
            logger.log(Level.INFO, String.format(message, *args))
        }
    }

    fun log(world: World, te: TileEntity, message: String, vararg args: Any) {
        if (doLogging) {
            val ticks = world.totalWorldTime
            if (ticks != prevTicks) {
                prevTicks = ticks
                logger.log(Level.INFO, "=== Time $ticks ===")
            }
            val id = "${te.pos.x},${te.pos.y},${te.pos.z}"
            logger.log(Level.INFO, id + String.format(message, *args))
        }
    }

    fun message(player: EntityPlayer, message: String, vararg args: Any) {
        player.addChatComponentMessage(TextComponentString(String.format(message, *args)))
    }

    fun warn(player: EntityPlayer, message: String, vararg args: Any) {
        player.addChatComponentMessage(TextComponentString(String.format(message, *args)).setStyle(Style().setColor(TextFormatting.RED)))
    }

}
