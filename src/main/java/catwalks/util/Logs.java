package catwalks.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class Logs {
    private static Logs instance = null;

    public static long prevTicks = -1;
    private Logger logger;

    public static boolean debugMode = false;
    public static boolean doLogging = false;

    private Logs() {
        logger = LogManager.getLogger();
        instance = this;
    }

    private static Logs getInstance() {
        if (instance == null) {
            instance = new Logs();
        }
        return instance;
    }

    public static void logError(String msg) {
        getInstance().logger.log(Level.ERROR, msg);
    }

    public static void log(World world, TileEntity te, String message) {
        if (doLogging) {
            long ticks = world.getTotalWorldTime();
            if (ticks != prevTicks) {
                prevTicks = ticks;
                getInstance().logger.log(Level.INFO, "=== Time " + ticks + " ===");
            }
            String id = te.getPos().getX() + "," + te.getPos().getY() + "," + te.getPos().getZ() + ": ";
            getInstance().logger.log(Level.INFO, id + message);
        }
    }

    public static void log(String message) {
        getInstance().logger.log(Level.INFO, message);
    }

    public static void logDebug(String message) {
        if (debugMode) {
            getInstance().logger.log(Level.INFO, message);
        }
    }

    public static void message(EntityPlayer player, String message) {
        player.addChatComponentMessage(new ChatComponentText(message));
    }

    public static void warn(EntityPlayer player, String message) {
        player.addChatComponentMessage(new ChatComponentText(message).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
    }
}