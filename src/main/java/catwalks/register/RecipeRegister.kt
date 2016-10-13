package catwalks.register

import catwalks.Const
import catwalks.item.crafting.RecipeDecorationRepair
import catwalks.item.crafting.RecipeDecorationSplit
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.RecipeSorter
import net.minecraftforge.oredict.ShapedOreRecipe

import net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS

object RecipeRegister {

    fun register() {
        if (1 == 1) {
            return
        }

        RecipeSorter.register(Const.MODID + ":decorationCombine", RecipeDecorationRepair::class.java, SHAPELESS, "before:minecraft:repair")
        RecipeSorter.register(Const.MODID + ":decorationSplit", RecipeDecorationSplit::class.java, SHAPELESS, "after:minecraft:shapeless")

        val m_steel = 0
        val m_iesteel = 1
        val m_wood = 2//, m_custom = 3;
        val tape = ItemRegister.tape
        val lights = ItemRegister.lights
        val speed = ItemRegister.speed
        val grate = ItemRegister.grate
        val blowtorch = ItemRegister.tool
        val paper = Items.PAPER
        val sugar = Items.SUGAR
        val flintNsteel = Items.FLINT_AND_STEEL
        val catwalk: Block? = null
        val stair: Block? = null
        val ladder: Block? = null
        val scaffold = BlockRegister.scaffolds[0]
        val pPlateWood = Blocks.WOODEN_PRESSURE_PLATE
        val vladder = Blocks.LADDER
        val iron = "ingotIron"
        val slimeball = "slimeball"
        val yellow = "dyeYellow"
        val glowstone = "dustGlowstone"
        val stick = "stickWood"


        CraftingManager.getInstance().addRecipe(RecipeDecorationRepair(ItemRegister.lights))
        CraftingManager.getInstance().addRecipe(RecipeDecorationRepair(ItemRegister.speed))
        CraftingManager.getInstance().addRecipe(RecipeDecorationRepair(ItemRegister.tape))
        CraftingManager.getInstance().addRecipe(RecipeDecorationSplit(ItemRegister.lights))
        CraftingManager.getInstance().addRecipe(RecipeDecorationSplit(ItemRegister.speed))
        CraftingManager.getInstance().addRecipe(RecipeDecorationSplit(ItemRegister.tape))

        // Items

        addShapedOreRecipe(true, ItemStack(blowtorch),
                "  f",
                " i ",
                "i  ",
                'i', iron,
                'f', flintNsteel)

        addShapedOreRecipe(true, ItemStack(grate, 16),
                "i i",
                " i ",
                "i i",
                'i', iron)

        addShapedOreRecipe(true, ItemStack(speed, 1, 256 - 16),
                "ppp",
                "sbs",
                "ppp",
                'p', paper,
                's', sugar,
                'b', slimeball)

        addShapedOreRecipe(true, ItemStack(tape, 1, 256 - 16),
                "ppp",
                "dbd",
                "ppp",
                'p', paper,
                'd', yellow,
                'b', slimeball)

        addShapedOreRecipe(true, ItemStack(lights, 1, 256 - 16),
                "ppp",
                "gbg",
                "ppp",
                'p', paper,
                'g', glowstone,
                'b', slimeball)

        // Catwalks

        addShapedOreRecipe(true, ItemStack(catwalk!!, 1, m_steel),
                "g g",
                " g ",
                'g', grate)
        // rusty/steel conversion
        addShapelessRecipe(true, ItemStack(catwalk, 1, m_iesteel),
                ItemStack(catwalk, 1, m_steel))
        addShapelessRecipe(true, ItemStack(catwalk, 1, m_steel),
                ItemStack(catwalk, 1, m_iesteel))

        addShapedOreRecipe(true, ItemStack(catwalk, 1, m_wood),
                "s s",
                " p ",
                's', stick,
                'p', pPlateWood)

        // Stairs

        addShapedOreRecipe(true, ItemStack(stair!!, 1, m_steel),
                "g  ",
                "gg ",
                " gg",
                'g', grate)
        // rusty/steel conversion
        addShapelessRecipe(true, ItemStack(stair, 1, m_iesteel),
                ItemStack(stair, 1, m_steel))
        addShapelessRecipe(true, ItemStack(stair, 1, m_steel),
                ItemStack(stair, 1, m_iesteel))

        addShapedOreRecipe(true, ItemStack(stair, 1, m_wood),
                "s  ",
                "ps ",
                " ps",
                's', stick,
                'p', pPlateWood)

        // Ladders

        addShapedOreRecipe(true, ItemStack(ladder!!, 1, m_steel),
                "glg",
                'g', grate,
                'l', vladder)
        // rusty/steel conversion
        addShapelessRecipe(true, ItemStack(ladder, 1, m_iesteel),
                ItemStack(ladder, 1, m_steel))
        addShapelessRecipe(true, ItemStack(ladder, 1, m_steel),
                ItemStack(ladder, 1, m_iesteel))

        addShapedOreRecipe(true, ItemStack(ladder, 1, m_wood),
                "sls",
                's', stick,
                'l', vladder)

        // Ladders

        addShapedOreRecipe(true, ItemStack(scaffold, 4, m_steel),
                "gg",
                "gg",
                'g', grate)
        // rusty/steel conversion
        addShapelessRecipe(true, ItemStack(scaffold, 1, m_iesteel),
                ItemStack(scaffold, 1, m_steel))
        addShapelessRecipe(true, ItemStack(scaffold, 1, m_steel),
                ItemStack(scaffold, 1, m_iesteel))

        addShapedOreRecipe(true, ItemStack(scaffold, 8, m_wood),
                " p ",
                "s s",
                " p ",
                's', stick,
                'p', pPlateWood)
    }


    // below methods snagged from CoFHLib

    fun addShapelessRecipe(condition: Boolean, out: ItemStack, vararg recipe: Any) {
        if (condition)
            GameRegistry.addShapelessRecipe(out, *recipe)
    }

    fun addShapelessRecipe(condition: Boolean, out: Item, vararg recipe: Any) {

        addShapelessRecipe(condition, ItemStack(out), *recipe)
    }

    fun addShapelessRecipe(condition: Boolean, out: Block, vararg recipe: Any) {

        addShapelessRecipe(condition, ItemStack(out), *recipe)
    }

    fun addShapedOreRecipe(condition: Boolean, out: ItemStack, vararg recipe: Any) {
        if (condition)
            GameRegistry.addRecipe(ShapedOreRecipe(out, *recipe))
    }

    fun addShapedOreRecipe(condition: Boolean, out: Item, vararg recipe: Any) {
        if (condition)
            GameRegistry.addRecipe(ShapedOreRecipe(out, *recipe))
    }

    fun addShapedOreRecipe(condition: Boolean, out: Block, vararg recipe: Any) {
        if (condition)
            GameRegistry.addRecipe(ShapedOreRecipe(out, *recipe))
    }

}
