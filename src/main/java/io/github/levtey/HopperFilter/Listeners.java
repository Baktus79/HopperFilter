package io.github.levtey.HopperFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Listeners implements Listener {
	
	private final HopperFilter plugin;
	
	public Listeners(HopperFilter plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHopperMove(InventoryMoveItemEvent evt) {
		List<ItemStack> include = new ArrayList<>();
		List<ItemStack> exclude = new ArrayList<>();
		InventoryHolder holder = evt.getDestination().getHolder();
		if (!(holder instanceof Hopper hopper)) return;
		List<ItemFrame> nearby = hopper.getWorld()
				.getNearbyEntities(hopper.getLocation().add(0.5, 0.5, 0.5), 0.6, 0.1, 0.6)
				.stream()
				.filter(ItemFrame.class::isInstance)
				.map(ItemFrame.class::cast)
				.collect(Collectors.toList());
		// populate include/exclude lists
		for (ItemFrame frame : nearby) {
			ItemStack item = frame.getItem();
			if (item.getType() == Material.AIR) continue;
			(isDiagonal(frame) ? exclude : include).add(item);
		}
		ItemStack item = evt.getItem();
		if ((!include.isEmpty() && !isPartOf(include, item)) || isPartOf(exclude, item)) {
			evt.setCancelled(true);
		}
	}
	
	private boolean isPartOf(List<ItemStack> filter, ItemStack item) {
		for (ItemStack filterItem : filter) {
			ItemMeta meta = filterItem.getItemMeta();
			if (meta.hasDisplayName()
					&& meta.getDisplayName().equalsIgnoreCase("fuzzy")
					? filterItem.getType() == item.getType()
					: filterItem.isSimilar(item)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isDiagonal(ItemFrame frame) {
		return frame.getRotation().toString().endsWith("5");
	}

}
