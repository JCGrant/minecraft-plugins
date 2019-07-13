package com.jcgrant.jccraft;

import org.bukkit.plugin.java.JavaPlugin;

public class JCCraft extends JavaPlugin {
  @Override
  public void onEnable() {
    getServer().getLogger().info("JCCraft Suite is enabled.");
    getServer().getPluginManager().registerEvents(new ChatLimiter(), this);
  }

  @Override
  public void onDisable() {
    getServer().getLogger().info("JCCraft Suite is disabled.");
  }
}
