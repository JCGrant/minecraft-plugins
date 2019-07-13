package com.jcgrant.jccraft;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatLimiter implements Listener {

  private final int MAX_CHAT_DISTANCE = 100;

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent e) {
    Player sender = e.getPlayer();
    e.getRecipients().removeIf((r) -> !canHearMessage(r, sender));
  }

  private boolean canHearMessage(Player receiver, Player sender)  {
    return sender.getWorld() == receiver.getWorld() &&
        sender.getLocation().distance(receiver.getLocation()) <= MAX_CHAT_DISTANCE;
  }
}
