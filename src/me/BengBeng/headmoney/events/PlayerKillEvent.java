package me.BengBeng.headmoney.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BengBeng.headmoney.file.Config;
import me.BengBeng.headmoney.utils.Utils;

public class PlayerKillEvent
	implements Listener {
	
	private List<String> list = Collections.synchronizedList(new ArrayList<String>());
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		Action act = event.getAction();
		if((act == Action.LEFT_CLICK_AIR) || (act == Action.LEFT_CLICK_BLOCK) || (act == Action.RIGHT_CLICK_AIR) || (act == Action.RIGHT_CLICK_BLOCK)) {
			if(act == Action.RIGHT_CLICK_BLOCK) {
				if(!list.contains(name)) {
					list.add(name);
				} else {
					list.remove(name);
					return;
				}
			}
			ItemStack inHand = player.getInventory().getItemInMainHand();
			for(String key : Config.getConfig().getConfigurationSection("ITEM-SAVED").getKeys(false)) {
				if(Utils.isItemStack(key)) {
					String owner = Utils.getOwner(key);
					ItemStack stack = Utils.getItemStack(key);
					if(inHand.isSimilar(stack)) {
						if(!owner.equals(name)) {
							Utils.sendMessage(player, Config.getConfig().getString("MESSAGE.not-owner").replaceAll("(?ium)(\\{player}|\\%player%)", owner));
							return;
						}
						
						double value = Utils.getLoreValue(inHand);
						Utils.getUser(name).giveMoney(value);
						Utils.sendMessage(player, Config.getConfig().getString("MESSAGE.money-received").replaceAll("(?ium)(\\{money}|\\%money%)", String.valueOf(value)));
						
						int current = inHand.getAmount();
						inHand.setAmount(current - 1);
						
						Utils.removeItem(key);
						break;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onKill(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player) {
			Player player = (Player)entity;
			String pName = player.getName();
			double money = Utils.getUser(pName).getMoney() / 3.0;
			Utils.getUser(pName).takeMoney(money);
			ItemStack skull = Utils.getUser(pName).getSkullTexture();
			if(skull.hasItemMeta()) {
				ItemMeta meta = skull.getItemMeta();
				List<String> lores = Collections.synchronizedList(new ArrayList<String>());
				String format = Utils.toColor(Utils.getLoreFormat(money));
				lores.add(format);
				meta.setLore(lores);
				skull.setItemMeta(meta);
			}
			Entity entityKiller = player.getKiller();
			if(entityKiller instanceof Player) {
				Player killer = (Player)entityKiller;
				String kName = killer.getName();
				if(Utils.isDropHead()) {
					Location pLoc = player.getLocation();
					pLoc.getWorld().dropItemNaturally(pLoc, skull);
				} else {
					killer.getInventory().addItem(new ItemStack[] { skull });
				}
				Utils.saveItem(kName, Utils.getRandomUUID(), skull);
			}
		}
	}
	
}
