package catwalks.util;

import java.util.function.Consumer;

/**
 * Created by TheCodeWarrior
 */
public class BooleanCombos {
	
	public static void loop(int amount, BooleanLoopRunnable run) {
		boolean[] arr = new boolean[amount];
		
		loop(arr, arr.length-1, run);
	}
	
	private static void loop(boolean[] arr, int index, BooleanLoopRunnable run) {
		if(index == -1) {
			run.run(arr);
			return;
		}
		
		arr[index] = false;
		loop(arr, index-1, run);
		arr[index] = true;
		loop(arr, index-1, run);
	}
	
	@FunctionalInterface
	public interface BooleanLoopRunnable {
		void run(boolean[] arr);
	}
}
