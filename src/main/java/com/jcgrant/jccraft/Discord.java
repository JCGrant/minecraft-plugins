package com.jcgrant.jccraft;

import java.util.HashMap;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Discord extends ListenerAdapter implements CommandHandler, Listener {

  private final String CONFIG_KEY_DISCORD_TOKEN = "discord.token";
  private final String CONFIG_KEY_DISCORD_WELCOME_CHANNEL_ID = "discord.welcome-channel-id";
  private final String CONFIG_KEY_DISCORD_VERIFIED_ROLE_ID = "discord.verified-role-id";

  private JDA jda;
  private JavaPlugin plugin;
  private HashMap<String, Member> pendingUsers = new HashMap<>();

  Discord(JavaPlugin plugin) {
    this.plugin = plugin;
    plugin.getConfig().addDefault(CONFIG_KEY_DISCORD_TOKEN, "");
    plugin.getConfig().addDefault(CONFIG_KEY_DISCORD_WELCOME_CHANNEL_ID, "");
    plugin.getConfig().addDefault(CONFIG_KEY_DISCORD_VERIFIED_ROLE_ID, "");
    String token = plugin.getConfig().getString(CONFIG_KEY_DISCORD_TOKEN);
    try {
      jda = new JDABuilder(token)
          .addEventListener(this)
          .build();
      jda.awaitReady();
    }
    catch (LoginException e) {
      e.printStackTrace();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    event.getUser().openPrivateChannel().queue(channel -> {
      String code = generateCode();
      Member member = event.getMember();
      pendingUsers.put(code,  member);
      channel.sendMessage("Hey there! Thanks for joining JC Craft! Please copy and paste the following into Minecraft:\n" + String.format("`/discord %s`", code)).queue();
    });
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (player.isWhitelisted()) {
      return;
    }
    player.sendMessage(ChatColor.AQUA + "Please paste your verification code from Discord.");
    long numTicks = 200; // 10 seconds-ish
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      if (player.isWhitelisted()) {
        return;
      }
      Bukkit.getScheduler().runTask(plugin, () -> {
        player.kickPlayer("Did not verify code in time.");
      });
    }, numTicks);
  }

  @Override
  public boolean handleCommand(Player player, String[] args) {
    if (args.length != 1) {
      player.sendMessage(ChatColor.RED + "Usage: /discord <KEY>");
      return true;
    }
    String code = args[0];
    if (!pendingUsers.containsKey(code)) {
      player.sendMessage(ChatColor.RED + "Invalid key!");
      return true;
    }
    player.setWhitelisted(true);
    player.sendMessage(ChatColor.AQUA + "Whitelisted!");
    Member member = pendingUsers.remove(code);
    String nickname = member.getEffectiveName();
    String minecraftName = player.getName();
    member.getGuild().getController().setNickname(member, minecraftName).queue();
    String roleID = plugin.getConfig().getString(CONFIG_KEY_DISCORD_VERIFIED_ROLE_ID);
    member.getGuild().getController().addSingleRoleToMember(member, jda.getRoleById(roleID)).queue();
    String channelID = plugin.getConfig().getString(CONFIG_KEY_DISCORD_WELCOME_CHANNEL_ID);
    jda.getTextChannelById(channelID)
    .sendMessage(String.format("%s has now been whitelisted! Their Minecraft username is %s.", nickname, minecraftName)).queue();
    return true;
  }

  private String generateCode() {
    return "123";
  }

}
