package Xeocas.Listeners;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List; // Import List here

public class OilFactoryListener implements Listener {

	private List<OilZone> oilZones = new ArrayList<>();

	public OilFactoryListener() {
		loadOilZones();
	}

	@EventHandler
	public void onFactoryPlaced(BlockPlaceEvent event) {
		Block block = event.getBlock();
		if (isInOilZone(block.getX(), block.getY(), block.getZ())) {
			event.getPlayer().sendMessage("Oil factory placed! Extraction is allowed in this zone.");
		} else {
			event.getPlayer().sendMessage("Oil extraction is not allowed in this area.");
			event.setCancelled(true);
		}
	}

	private void loadOilZones() {
		File file = new File("plugins/YourPlugin/oil_zones.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		for (String key : config.getConfigurationSection("oil_zones").getKeys(false)) {
			int minX = config.getInt("oil_zones." + key + ".min_coords.x");
			int minY = config.getInt("oil_zones." + key + ".min_coords.y");
			int minZ = config.getInt("oil_zones." + key + ".min_coords.z");

			int maxX = config.getInt("oil_zones." + key + ".max_coords.x");
			int maxY = config.getInt("oil_zones." + key + ".max_coords.y");
			int maxZ = config.getInt("oil_zones." + key + ".max_coords.z");

			oilZones.add(new OilZone(minX, minY, minZ, maxX, maxY, maxZ));
		}
	}

	private boolean isInOilZone(int x, int y, int z) {
		for (OilZone zone : oilZones) {
			if (zone.isInside(x, y, z)) {
				return true;
			}
		}
		return false;
	}

	private static class OilZone {
		private final int minX, minY, minZ, maxX, maxY, maxZ;

		public OilZone(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}

		public boolean isInside(int x, int y, int z) {
			return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
		}
	}
}
