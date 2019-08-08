package com.jcgrant.jccraft;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class Phantoms implements Listener {

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    Entity entity = event.getEntity();
    if (entity.getType() != EntityType.PHANTOM) {
      return;
    }
    event.setCancelled(true);
  }

}
