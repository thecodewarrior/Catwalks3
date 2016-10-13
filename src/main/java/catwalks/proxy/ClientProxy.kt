package catwalks.proxy

import catwalks.Conf
import catwalks.Const
import catwalks.register.RenderRegister
import catwalks.render.ModelHandle
import catwalks.render.StateHandle
import catwalks.util.GeneralUtil
import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.client.model.obj.OBJLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

class ClientProxy : CommonProxy() {

    override val server: MinecraftServer
        get() = FMLClientHandler.instance().server

    override fun preInit() {
        RenderRegister.Blocks.initRender()
        RenderRegister.Items.initRender()
        RenderRegister.Parts.initRender()
        MinecraftForge.EVENT_BUS.register(Conf)
        OBJLoader.INSTANCE.addDomain(Const.MODID)
        ModelHandle.init()
        StateHandle.init()
    }

    override fun reloadConfigs() {
//        if (Minecraft.getMinecraft().renderGlobal != null)
//            Minecraft.getMinecraft().renderGlobal.loadRenderers()
    }

    init { /* dev only */
    }

    @SubscribeEvent
    fun debugText(event: RenderGameOverlayEvent.Text) {
        if (!Const.developmentEnvironment)
            return

        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo)
            return

        val mc = Minecraft.getMinecraft()
        val player = mc.thePlayer
        if (player.isSneaking) {
            event.left.add(9, String.format("Motion: actual %.5f / %.5f / %.5f",
                    player.posX - player.lastTickPosX,
                    player.posY - player.lastTickPosY,
                    player.posZ - player.lastTickPosZ))
        } else {
            event.left.add(9, String.format("Motion: fields %.5f / %.5f / %.5f",
                    player.motionX, player.motionY, player.motionZ))
        }

        event.left.add(String.format("Move: forward %.5f, strafe %.5f", player.moveForward, player.moveStrafing))

        if (player.isSneaking) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && mc.objectMouseOver.blockPos != null) {
                val blockpos = mc.objectMouseOver.blockPos
                val iblockstate = mc.theWorld.getBlockState(blockpos)

                val maybeExtended = iblockstate.block.getExtendedState(iblockstate, mc.theWorld, blockpos)

                if (maybeExtended is IExtendedBlockState) {

                    for (entry in maybeExtended.unlistedNames) {
                        val value = maybeExtended.getValue(entry)

                        val NULL_VALUE = "${ChatFormatting.OBFUSCATED}|${ChatFormatting.RESET}${ChatFormatting.ITALIC}NULL${ChatFormatting.OBFUSCATED}${ChatFormatting.RESET}"

                        var s = value?.toString() ?: NULL_VALUE

                        if (value === java.lang.Boolean.TRUE) {
                            s = "${ChatFormatting.GREEN}${ChatFormatting.ITALIC}$s"
                        } else if (value === java.lang.Boolean.FALSE) {
                            s = "${ChatFormatting.RED}${ChatFormatting.ITALIC}$s"
                        }

                        event.right.add("${ChatFormatting.ITALIC}${entry.name}: $s")
                    }
                }



                event.left.add("Looking at side: " + mc.objectMouseOver.sideHit.getName() + " - meta: " + iblockstate.block.getMetaFromState(iblockstate))
            }
        }
    }


    @SubscribeEvent
    fun worldRender(event: RenderWorldLastEvent) {
        if (!Const.developmentEnvironment)
            return

        val mc = Minecraft.getMinecraft()

        GlStateManager.pushAttrib()
        GlStateManager.disableLighting()

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        GlStateManager.color(0.0f, 0.0f, 0.0f, 0.4f)
        GL11.glLineWidth(2.0f)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)

        GlStateManager.pushMatrix()

        val rootPlayer = Minecraft.getMinecraft().thePlayer
        val x = rootPlayer.lastTickPosX + (rootPlayer.posX - rootPlayer.lastTickPosX) * event.partialTicks
        val y = rootPlayer.lastTickPosY + (rootPlayer.posY - rootPlayer.lastTickPosY) * event.partialTicks
        val z = rootPlayer.lastTickPosZ + (rootPlayer.posZ - rootPlayer.lastTickPosZ) * event.partialTicks
        GlStateManager.translate(-x, -y, -z)

        mc.mcProfiler.startSection("blockBBs")

        if (!mc.renderManager.isDebugBoundingBox || mc.theWorld == null || mc.thePlayer == null || mc.thePlayer.entityBoundingBox == null) {
        } else {

            val d = 0.0020000000949949026
            val searchBB = mc.thePlayer.entityBoundingBox.expand(2.0, 2.0, 2.0)

            val aabbs = mc.theWorld.getCollisionBoxes(mc.thePlayer, searchBB)

            for (bb in aabbs) {
                val bb_ = bb.expand(d, d, d)
                RenderGlobal.drawBoundingBox(bb_.minX, bb_.minY, bb_.minZ, bb.maxX, bb.maxY, bb.maxZ, 0.5f, 1f, 0.5f, 1f)
            }

            // desired move vec

            if (mc.gameSettings.thirdPersonView > 0) {
                val p1 = Vec3d(x, y + 1, z)
                val p2 = p1.add(GeneralUtil.getDesiredMoveVector(rootPlayer))

                val tessellator = Tessellator.getInstance()
                val vertexbuffer = tessellator.buffer
                vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR)
                vertexbuffer.pos(p1.xCoord, p1.yCoord, p1.zCoord).color(127, 127, 255, 255).endVertex()
                vertexbuffer.pos(p2.xCoord, p2.yCoord, p2.zCoord).color(127, 127, 255, 255).endVertex()
                tessellator.draw()
            }
        }
        mc.mcProfiler.endSection()

        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()

        GlStateManager.popAttrib()
        GlStateManager.popMatrix()
    }

}
