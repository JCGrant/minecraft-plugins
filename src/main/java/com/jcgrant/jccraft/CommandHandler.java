package com.jcgrant.jccraft;

import org.bukkit.entity.Player;

public interface CommandHandler {
  boolean handleCommand(Player sender, String[] args);
}
