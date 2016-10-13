package catwalks.register

import catwalks.item.*
import java.util.*

object ItemRegister {

    var lights = ItemDecoration("lights")
    var tape = ItemDecoration("tape")
    var speed = ItemDecoration("speed")
    var tool = ItemCatwalkTool()
    var grate = ItemBase("steelgrate")
    var ladderGrabber = ItemLadderGrabber()
    var scaffold = ItemScaffold("scaffoldpart")
    var catwalk = ItemCatwalk("catwalkpart")
    var stair = ItemStair("stairpart")

    var renderRegsiterItems: MutableList<ItemBase> = mutableListOf()
}
