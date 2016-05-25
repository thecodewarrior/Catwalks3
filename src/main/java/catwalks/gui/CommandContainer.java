package catwalks.gui;

import java.util.Map;

import mcjty.lib.network.Argument;

public interface CommandContainer {

	public void execute(String command, Map<String, Argument> args);
	
}
