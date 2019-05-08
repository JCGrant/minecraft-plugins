package com.jcgrant.jccraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class JCCraft extends JavaPlugin {
  @Override
  public void onEnable() {
    getServer().getLogger().info("JCCraft Suite is enabled.");
  }

  @Override
  public void onDisable() {
    getServer().getLogger().info("JCCraft Suite is disabled.");
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("mycommand")) {
      sender.sendMessage("You ran /mycommand!");
      return true;
    }
    return false;
  }
}
