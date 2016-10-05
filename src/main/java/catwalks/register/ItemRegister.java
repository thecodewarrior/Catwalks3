package catwalks.register;

import catwalks.item.*;

import java.util.ArrayList;
import java.util.List;

public class ItemRegister {
	
	public static ItemDecoration lights, tape, speed;
	public static ItemCatwalkTool tool;
	public static ItemBase grate;
	public static ItemLadderGrabber ladderGrabber;
	public static ItemScaffold scaffold;
	public static ItemCatwalk catwalk;
	public static ItemStair stair;
	
	public static void register() {
		lights = new ItemDecoration("lights");
		tape   = new ItemDecoration("tape");
		speed  = new ItemDecoration("speed");
		tool   = new ItemCatwalkTool();
		grate  = new ItemBase("steelgrate");
		ladderGrabber = new ItemLadderGrabber();
		scaffold = new ItemScaffold("scaffoldpart");
		catwalk = new ItemCatwalk("catwalkpart");
		stair = new ItemStair("stairpart");
	}
	
	public static List<ItemBase> renderRegsiterItems = new ArrayList<>();
}
