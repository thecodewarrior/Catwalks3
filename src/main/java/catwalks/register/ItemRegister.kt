package catwalks.register

import catwalks.item.*

object ItemRegister {

    val renderRegisterItems: MutableList<IItemBase> = mutableListOf()

    var lights = ItemDecoration("lights")
    var tape = ItemDecoration("tape")
    var speed = ItemDecoration("speed")
    var tool = ItemCatwalkTool()
    var grate = ItemBase("steelgrate")
    var ladderGrabber = ItemLadderGrabber()
    var scaffold = ItemScaffold("scaffold")
    var catwalk = ItemCatwalk("catwalk")
    var stair = ItemStair("stair")
}
