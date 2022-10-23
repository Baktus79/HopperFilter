package io.github.levtey.HopperFilter;

import org.bukkit.plugin.java.JavaPlugin;

public class HopperFilter extends JavaPlugin {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new Listeners(), this);
	}

}
