package io.github.levtey.HopperFilter;

import org.bukkit.plugin.java.JavaPlugin;

public class HopperFilter extends JavaPlugin {
	
	public void onEnable() {
		new Listeners(this);
	}

}
