package xyz.refinedev.api.nametag.util;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.api.nametag.NameTagHandler;

public class TaskUtil {
	
	public static void run(Runnable runnable) {
		NameTagHandler.getInstance().getPlugin().getServer().getScheduler().runTask(NameTagHandler.getInstance().getPlugin(), runnable);
	}
	
	public static void runTimer(Runnable runnable, long delay, long timer) {
		NameTagHandler.getInstance().getPlugin().getServer().getScheduler().runTaskTimer(NameTagHandler.getInstance().getPlugin(), runnable, delay, timer);
	}
	
	public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
		runnable.runTaskTimer(NameTagHandler.getInstance().getPlugin(), delay, timer);
	}
	
	public static void runLater(Runnable runnable, long delay) {
		NameTagHandler.getInstance().getPlugin().getServer().getScheduler().runTaskLater(NameTagHandler.getInstance().getPlugin(), runnable, delay);
	}
	
	public static void runAsynchronously(Runnable runnable) {
		NameTagHandler.getInstance().getPlugin().getServer().getScheduler().runTaskAsynchronously(NameTagHandler.getInstance().getPlugin(), runnable);
	}
	
	public static void runAsynchronously(Runnable runnable, long delay, long timer) {
		NameTagHandler.getInstance().getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(NameTagHandler.getInstance().getPlugin(), runnable, delay, timer);
	}
}
