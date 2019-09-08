package me.BengBeng.headmoney.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class User {
	
	private String name;
	private Player player;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Player getPlayer() {
		player = Bukkit.getServer().getPlayer(getName());
		return player;
	}
	
	
	
	public boolean isValid() {
		return (getPlayer() != null);
	}
	
	public boolean isActive() {
		return ((getPlayer() != null) && (getPlayer().isOnline()));
	}
	
	
	
	public ItemStack getSkullTexture() {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte)SkullType.PLAYER.ordinal());
		SkullMeta meta = (SkullMeta)skull.getItemMeta();
		meta.setOwner(getName());
		skull.setItemMeta(meta);
		return skull;
	}
	
	
	
	public double getMoney() {
		return Utils.getEconomy().getBalance(getName());
	}
	
	public void giveMoney(double amount) {
		Utils.getEconomy().depositPlayer(getName(), amount);
	}
	
	public void takeMoney(double amount) {
		Utils.getEconomy().withdrawPlayer(getName(), amount);
	}
	
}
