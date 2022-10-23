package io.github.levtey.HopperFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHopperMove(InventoryMoveItemEvent e) {
		final List<ItemStack> include = new ArrayList<>();
		final List<ItemStack> exclude = new ArrayList<>();
		final InventoryHolder holder = e.getDestination().getHolder();

		if(!(holder instanceof final Hopper hopper)) { return; }

		final List<ItemFrame> nearby = hopper.getWorld()
				.getNearbyEntities(hopper.getLocation().add(0.5, 0.5, 0.5), 0.6, 0.1, 0.6)
				.stream()
				.filter(ItemFrame.class::isInstance)
				.map(ItemFrame.class::cast)
				.collect(Collectors.toList());

		// populate include/exclude lists
		for(final ItemFrame frame : nearby) {
			final ItemStack item = frame.getItem();
			if (item.getType() == Material.AIR) continue;
			(isDiagonal(frame) ? exclude : include).add(item);
		}
		final ItemStack item = e.getItem();
		e.setCancelled(!include.isEmpty() && !isPartOf(include, item) || isPartOf(exclude, item));
	}

	@SuppressWarnings("deprecation")
	private boolean isPartOf(List<ItemStack> filter, ItemStack item) {
		for(final ItemStack filterItem : filter) {
			final ItemMeta meta = filterItem.getItemMeta();
			if (meta.hasDisplayName()
					&& meta.getDisplayName().equalsIgnoreCase("plain")
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
