package catwalks.util

import net.minecraft.item.Item

import java.util.ArrayList

object WrenchChecker {

    @SuppressWarnings("rawtypes")
    private val wrenchClasses = ArrayList<Class<*>>()

    fun init() {
        for (className in arrayOf(
                /*
                 * Can add or remove class names here
                 * Use API interface names where possible, in case of refactoring
                 * note that many mods implement BC wrench API iff BC is installed
                 * and include no wrench API of their own - we use implementation
                 * classes here to catch these cases.
                */
                "buildcraft.api.tools.IToolWrench", //Buildcraft
                "resonant.core.content.ItemScrewdriver", //Resonant Induction
                "ic2.core.item.tool.ItemToolWrench", //IC2
                "ic2.core.item.tool.ItemToolWrenchElectric", //IC2 (more)
                "mrtjp.projectred.api.IScrewdriver", //Project Red
                "mods.railcraft.api.core.items.IToolCrowbar", //Railcraft
                "com.bluepowermod.items.ItemScrewdriver", //BluePower
                "cofh.api.item.IToolHammer", //Thermal Expansion and compatible
                "thermalexpansion.item.tool.ItemWrench", "appeng.items.tools.quartz.ToolQuartzWrench", //Applied Energistics
                "crazypants.enderio.api.tool.ITool", //Ender IO
                "mekanism.api.IMekWrench", //Mekanism
                "mcjty.rftools.items.smartwrench", //RFTools
                "pneumaticCraft.common.item.ItemPneumaticWrench", "powercrystals.minefactoryreloaded.api.IToolHammer", "catwalks.item.ItemCatwalkTool")) {
            try {
                wrenchClasses.add(Class.forName(className))
            } catch (e: ClassNotFoundException) {
                Logs.debug("Failed to load wrench class $className (this is not an error)")
            }

        }
    }

    @SuppressWarnings("unchecked", "rawtypes")
    fun isAWrench(item: Item): Boolean {
        for (c in wrenchClasses) {
            if (c.isAssignableFrom(item.javaClass)) {
                return true
            }
        }
        return false
    }

}