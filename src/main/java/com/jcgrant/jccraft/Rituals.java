package com.jcgrant.jccraft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

// has to be night and full moon
// uses xp
// player to type incantation
// SPAWNS ENEMIES?

public class Rituals implements Listener {

  class Ritual {

    class RitualLocation {
      Material material;
      Location location;
      boolean willDisappear;

      public RitualLocation(Material material, Location location, boolean willDisappear) {
        this.material = material;
        this.location = location;
        this.willDisappear = willDisappear;
      }
    }

    private final int Y_OFFSET = -2;
    private final EntityType SACRIFICE_ENTITY_TYPE = EntityType.COW;
    private final String[] SACRIFICE_MESSAGES =
        new String[] {
          "The air gets cold around you...",
          "The wind begins to pick up...",
          "The ground begins to rumble...",
          "You feel a great energy in the air, ready to be unleashed!"
        };
    private final int NUM_SACRIFICES_NEEDED = SACRIFICE_MESSAGES.length;

    private Location ignitionPoint;
    private Set<RitualLocation> locations = new HashSet<>();
    private Vector bottomLeft;
    private Vector topRight;
    private int numSacrifices = 0;

    public Ritual(Location ignitionPoint, String[] pattern) {
      this.ignitionPoint = ignitionPoint;
      HashMap<Character, Material> materials = new HashMap<>();
      materials.put(' ', Material.AIR);
      materials.put('r', Material.REDSTONE_WIRE);
      materials.put('c', Material.COBBLESTONE);
      HashSet<Material> dissappearingMaterials = new HashSet<>();
      dissappearingMaterials.add(Material.REDSTONE_WIRE);

      int length = pattern.length;
      int width = pattern[0].length();
      for (int i = 0; i < length; i++) {
        for (int j = 0; j < width; j++) {
          char c = pattern[i].charAt(j);
          int dz = i - length / 2;
          int dx = j - width / 2;
          Material material = materials.get(c);
          Location location = ignitionPoint.clone().add(dx, Y_OFFSET, dz);
          boolean willDisappear = dissappearingMaterials.contains(material);
          locations.add(new RitualLocation(material, location, willDisappear));
        }
      }

      bottomLeft = ignitionPoint.clone().subtract(width / 2, Y_OFFSET, length / 2).toVector();
      topRight = ignitionPoint.clone().add(width / 2, Y_OFFSET, length / 2).toVector();
    }

    public boolean isIgnitionPoint(Location location) {
      return location.equals(ignitionPoint);
    }

    public boolean inBounds(Location location) {
      return location.getBlockX() >= bottomLeft.getBlockX()
          && location.getBlockX() <= topRight.getBlockX()
          && location.getBlockZ() >= bottomLeft.getBlockZ()
          && location.getBlockZ() <= topRight.getBlockZ();
    }

    public boolean readyForSacrifices() {
      for (RitualLocation loc : locations) {
        if (loc.material == Material.AIR) {
          continue;
        }
        if (loc.location.getBlock().getType() != loc.material) {
          return false;
        }
      }
      return true;
    }

    public void performSacrifice(Player player, Entity entity) {
      if (numSacrifices == NUM_SACRIFICES_NEEDED) {
        player.sendMessage("Nothing changes, perhaps the ritual is ready?");
        return;
      }
      if (entity.getType() == SACRIFICE_ENTITY_TYPE) {
        numSacrifices++;
        player.sendMessage(SACRIFICE_MESSAGES[numSacrifices - 1]);
      }
    }

    public boolean readyForRitual() {
      return numSacrifices >= NUM_SACRIFICES_NEEDED;
    }

    public void performRitual(Player player) {
      numSacrifices = 0;
      locations.forEach(
          (loc) -> {
            Block block = loc.location.getBlock();
            if (loc.willDisappear) {
              block.setType(Material.AIR);
            }
          });
      World world = ignitionPoint.getWorld();
      world.strikeLightning(ignitionPoint);
      ThreadLocalRandom rand = ThreadLocalRandom.current();
      int numItems = rand.nextInt(2, 4);
      for (int i = 0; i < numItems; i++) {
        int randX = rand.nextInt(bottomLeft.getBlockX() + 1, topRight.getBlockX());
        int randZ = rand.nextInt(bottomLeft.getBlockZ() + 1, topRight.getBlockZ());
        Location dropLocation = new Location(world, randX, ignitionPoint.getBlockY() + 10, randZ);
        world.dropItemNaturally(dropLocation, new ItemStack(Material.DIAMOND, 1));
      }
    }
  }

  private Ritual ritual;

  public Rituals() {
    World world = Bukkit.getServer().getWorld("world");
    ritual =
        new Ritual(
            new Location(world, 231, 73, 578),
            new String[] {
              "  rrrrr  ",
              " rr r rr ",
              "rr rrr rr",
              "r rr rr r",
              "rrr   rrr",
              "r rr rr r",
              "rr rrr rr",
              " rr r rr ",
              "  rrrrr  ",
            });
  }

  @EventHandler
  public void onEntityDeath(EntityDeathEvent e) {
    Entity entity = e.getEntity();
    if (!ritual.inBounds(entity.getLocation())) {
      return;
    }
    if (!ritual.readyForSacrifices()) {
      return;
    }
    ritual.performSacrifice(e.getEntity().getKiller(), entity);
  }

  @EventHandler
  public void onIgniteBlock(BlockIgniteEvent e) {
    Block block = e.getBlock();
    if (!ritual.isIgnitionPoint(block.getLocation())) {
      return;
    }
    if (!ritual.readyForRitual()) {
      return;
    }
    ritual.performRitual(e.getPlayer());
  }
}
