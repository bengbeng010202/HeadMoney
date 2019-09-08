package me.BengBeng.headmoney.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.BengBeng.headmoney.HeadMoney;
import me.BengBeng.headmoney.file.Config;
import net.milkbowl.vault.economy.Economy;

public class Utils {
	
	private static HeadMoney main = HeadMoney.getPlugin(HeadMoney.class);
	
	public static HeadMoney getMain() {
		return main;
	}
	
	
	
	/*
	 * KHAI BÁO CLASS:
	 */
	
	private static User user = new User();
	
	public static User getUser(String name) {
		user.setName(name);
		return user;
	}
	
	
	
	/*
	 * KIỂM TRA HOOK:
	 */
	
	private static Economy econ;
	
	public static boolean isHookEconomy() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Vault");
		if((plugin == null) || (!plugin.isEnabled())) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return (econ != null);
	}
	
	public static Economy getEconomy() {
		isHookEconomy();
		return econ;
	}
	
	
	
	/*
	 * LẤY CÁC THÔNG TIN TỪ config.yml:
	 */
	
	public static boolean isDropHead() {
		return (Config.getConfig().getBoolean("drop-head") == true);
	}
	
	public static String getSeparatorFormat() {
		return toColor(Config.getConfig().getString("FORMAT.separator"));
	}
	
	public static String getValueFormat(double value) {
		return toColor(Config.getConfig().getString("FORMAT.value").replaceAll("(?ium)(\\{value}|\\%value%)", String.valueOf(value)));
	}
	
	public static String getLoreMoneyFormat() {
		return toColor(Config.getConfig().getString("FORMAT.lore-money"));
	}
	
	public static String getLoreFormat(double value) {
		return toColor("&8&0&f&r" + getLoreMoneyFormat() + getSeparatorFormat() + "&8&0&f&r" + getValueFormat(value));
	}
	
	public static List<String> getListUUID() {
		List<String> uuids = Collections.synchronizedList(new ArrayList<String>());
		for(String key : Config.getConfig().getConfigurationSection("ITEM-SAVED").getKeys(false)) {
			uuids.add(key);
		}
		return uuids;
	}
	
	public static String getRandomUUID() {
		boolean success = false;
		UUID uuid = UUID.randomUUID();
		String uid = uuid.toString();
		while(getListUUID().contains(uid)) {
			if(success == false) {
				uid = UUID.randomUUID().toString();
				if(!getListUUID().contains(uid)) {
					success = true;
				}
			}
			if(success == true) {
				break;
			}
		}
		return uid;
	}
	
	public static boolean isItemStack(String uuid) {
		return Config.getConfig().isItemStack("ITEM-SAVED." + uuid + ".item");
	}
	
	public static ItemStack getItemStack(String uuid) {
		return Config.getConfig().getItemStack("ITEM-SAVED." + uuid + ".item");
	}
	
	public static boolean hasOwner(String uuid) {
		String path = Config.getConfig().getString("ITEM-SAVED." + uuid + ".owner");
		return ((path != null) && (!path.isEmpty()) && (path.length() > 0) && (!path.equalsIgnoreCase("none")));
	}
	
	public static String getOwner(String uuid) {
		return Config.getConfig().getString("ITEM-SAVED." + uuid + ".owner");
	}
	
	public static void saveItem(String name, String uuid, ItemStack item) {
		Config.getConfig().set("ITEM-SAVED." + uuid + ".owner", name);
		Config.getConfig().set("ITEM-SAVED." + uuid + ".item", item);
		Config.saveConfig();
	}
	
	public static void removeItem(String uuid) {
		Config.getConfig().set("ITEM-SAVED." + uuid, null);
		Config.saveConfig();
	}
	
	public static double getLoreValue(ItemStack item) {
		double value = 0.0;
		ItemMeta meta = item.getItemMeta();
		List<String> lores = meta.getLore();
		for(int x = 0; x < lores.size(); x++) {
			String str = toColor(lores.get(x));
			if(str.contains(getLoreMoneyFormat())) {
				String strValue = str.split(getSeparatorFormat())[1].replaceAll("((((\\&|\\§)(r|[a-fk-o0-9]))+)?| )", "");
				value = Double.parseDouble(strValue);
			}
		}
		return value;
	}
	
	
	
	/*
	 * CÁC PHẦN KHÁC:
	 */
	
	public static boolean regexMatches(String str, String regex) {
		return str.matches("(?ium)(" + regex.replaceAll("( |_|-)", "|") + ")");
	}
	
	public static String toColor(String str) {
		return ChatColor.translateAlternateColorCodes('&', str.replaceAll("(?ium)(\\{prefix}|\\%prefix%)", Config.getConfig().getString("MESSAGE.PREFIX")));
	}
	
	public static void sendMessage(CommandSender sender, String... msg) {
		for(int x = 0; x < msg.length; x++) {
			String newMsg = toColor(msg[x]);
			if(sender != null) {
				sender.sendMessage(newMsg);
			}
		}
	}
	
	public static void sendPlayerMessage(String... msg) {
		for(int x = 0; x < msg.length; x++) {
			String newMsg = toColor(msg[x]);
			for(Player online : Bukkit.getServer().getOnlinePlayers()) {
				if(online != null) {
					online.sendMessage(newMsg);
				}
			}
		}
	}
	
	public static void sendConsoleMessage(String... msg) {
		for(int x = 0; x < msg.length; x++) {
			String newMsg = toColor(msg[x]);
			ConsoleCommandSender ccs = Bukkit.getServer().getConsoleSender();
			ccs.sendMessage(newMsg);
		}
	}
	
	public static void sendBroadcastMessage(String... msg) {
		for(int x = 0; x < msg.length; x++) {
			String newMsg = toColor(msg[x]);
			ConsoleCommandSender ccs = Bukkit.getServer().getConsoleSender();
			ccs.sendMessage(newMsg);
			for(Player online : Bukkit.getServer().getOnlinePlayers()) {
				if(online != null) {
					online.sendMessage(newMsg);
				}
			}
		}
	}
	
}
