package com.jcgrant.jccraft;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class JCCraft extends JavaPlugin {

  private HashMap<String, CommandHandler> commands = new HashMap<>();

  @Override
  public void onEnable() {
    getServer().getLogger().info("JCCraft Suite is enabled.");

    ChatLimiter chatLimiter = new ChatLimiter(this);
    registerEvents(chatLimiter);
    registerCommand("chat", chatLimiter);

    Rituals rituals = new Rituals();
    registerEvents(rituals);

    Discord discord = new Discord(this);
    registerCommand("discord", discord);
    registerEvents(discord);

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
    CommandHandler handler = commands.get(commandName);
    if (handler == null) {
      return false;
    }
    return handler.handleCommand((Player) sender, args);
  }

  private void registerCommand(String name, CommandHandler command) {
    commands.put(name, command);
  }

  private void registerEvents(Listener listener) {
    getServer().getPluginManager().registerEvents(listener, this);
  }
}
