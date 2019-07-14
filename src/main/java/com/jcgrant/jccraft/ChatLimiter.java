package com.jcgrant.jccraft;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatLimiter implements Listener {

  private final int MAX_CHAT_DISTANCE = 100;
  private final String CONFIG_KEY = "chat-modes";
  private final String GLOBAL = "global";
  private final String LOCAL = "local";

  private JavaPlugin plugin;

  public ChatLimiter(JavaPlugin plugin) {
    this.plugin = plugin;
    plugin.getConfig().addDefault(CONFIG_KEY, new HashMap<String, String>());
  }

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent e) {
    e.setCancelled(true);
    Player sender = e.getPlayer();
    e.getRecipients().stream()
    .forEach(r -> {
      ChatColor color = ChatColor.WHITE;
      if (!isGlobal(sender)) {
        color = ChatColor.GREEN;
      }
      if (canHearMessage(r, sender)) {
        r.sendMessage(color + "<" + sender.getName() + "> " + e.getMessage());
      }
    });
  }

  public boolean setChatMode(Player player, String[] args) {
    if (args.length != 1) {
      player.sendMessage(ChatColor.RED + "usage: /chat <global/local>");
      return true;
    }
    String arg = args[0].toLowerCase();
    if (arg.equals("global")) {
      setChatMode(player, GLOBAL);
      player.sendMessage(ChatColor.AQUA + "Set chat mode to Global");
      return true;
    } else if (arg.equals("local")) {
      setChatMode(player, LOCAL);
      player.sendMessage(ChatColor.AQUA + "Set chat mode to Local");
      return true;
    } else {
      player.sendMessage(ChatColor.RED + "usage: /chat <global/local>");
      return true;
    }
  }

  private boolean canHearMessage(Player receiver, Player sender)  {
    return isGlobal(sender) ||
        sender.getWorld() == receiver.getWorld() &&
        sender.getLocation().distance(receiver.getLocation()) <= MAX_CHAT_DISTANCE;
  }

  private boolean isGlobal(Player sender) {
    return getChatMode(sender) == GLOBAL;
  }

  private String getChatMode(Player p) {
    return plugin.getConfig().getString(CONFIG_KEY + "." + p.getName(), GLOBAL);
  }

  private void setChatMode(Player p, String mode) {
    plugin.getConfig().set(CONFIG_KEY + "." + p.getName(), mode);
    plugin.saveConfig();
  }
}
