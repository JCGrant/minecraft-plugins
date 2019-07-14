package com.jcgrant.jccraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class JCCraft extends JavaPlugin {

  private ChatLimiter chatLimiter;

  @Override
  public void onEnable() {
    getServer().getLogger().info("JCCraft Suite is enabled.");
    chatLimiter = new ChatLimiter(this);
    getServer().getPluginManager().registerEvents(chatLimiter, this);
    getConfig().options().copyDefaults(true);
    saveConfig();
  }

  @Override
  public void onDisable() {
    getServer().getLogger().info("JCCraft Suite is disabled.");
    saveConfig();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }
    String commandName = command.getName().toLowerCase();
    switch (commandName) {
    case "chat":
      return chatLimiter.setChatMode((Player) sender, args);
    }
    return false;
  }
}
