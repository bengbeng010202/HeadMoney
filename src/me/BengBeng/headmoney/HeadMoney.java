package me.BengBeng.headmoney;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.BengBeng.headmoney.events.PlayerKillEvent;
import me.BengBeng.headmoney.file.Config;
import me.BengBeng.headmoney.utils.Utils;

public class HeadMoney
	extends JavaPlugin
	implements CommandExecutor {
	
	private PluginManager pm = Bukkit.getServer().getPluginManager();
	
	@Override
	public void onEnable() {
		loadFiles();
		
		if(!Utils.isHookEconomy()) {
			Utils.sendConsoleMessage("&6[&bHeadMoney&6] &cKhông thể tìm thấy plugin yêu cầu: &eVault&c.");
			Utils.sendConsoleMessage("&6[&bHeadMoney&6] &cVui lòng cài đặt plugin yêu cầu trước khi khởi động plugin này!");
			Utils.sendConsoleMessage("&6[&bHeadMoney&6] &cTự động dừng lại plugin...");
			pm.disablePlugin(this);
			return;
		}
		
		loadEvents();
		
		getCommand("headmoney").setExecutor(this);
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}
	
	private void loadEvents() {
		pm.registerEvents(new PlayerKillEvent(), this);
	}
	
	private void loadFiles() {
		Config.loadConfig();
	}
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("headmoney.command.reload")) {
			Utils.sendMessage(sender, Config.getConfig().getString("MESSAGE.no-permission"));
			return true;
		}
		Config.reloadConfig();
		Utils.sendMessage(sender, Config.getConfig().getString("MESSAGE.config-reload"));
		return false;
	}
	
}
