package me.BengBeng.headmoney.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.BengBeng.headmoney.utils.Utils;

public class Config {
	
	private static File getPluginFolder() {
		return Utils.getMain().getDataFolder();
	}
	
	
	
	private static FileConfiguration config;
	private static File configF;
	
	private static void copy(InputStream in, File out) {
		InputStream fis = in;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch(IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fis.close();
				fos.close();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	/*
	 * KHỞI ĐỘNG FILE:
	 */
	
	public static void loadConfig() {
		configF = new File(getPluginFolder(), "config.yml");
		config = new YamlConfiguration();
		if(!configF.exists()) {
			File parent = configF.getParentFile();
			if((parent != null) && (!parent.isFile()) && (!parent.exists())) {
				parent.mkdirs();
			}
			try {
				configF.createNewFile();
				InputStream in = Config.class.getResourceAsStream("/config.yml");
				copy(in, configF);
				config.load(configF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				config.load(configF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	/*
	 * LƯU FILE:
	 */
	
	public static void saveConfig() {
		if(!configF.exists()) {
			loadConfig();
		} else {
			try {
				config.save(configF);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	/*
	 * LÀM MỚI FILE:
	 */
	
	public static void reloadConfig() {
		try {
			config.load(configF);
			InputStream input = new FileInputStream(configF);
			if(input != null) {
				config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(input, StandardCharsets.UTF_8)));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	/*
	 * LẤY FILE:
	 */
	
	public static FileConfiguration getConfig() {
		if(!configF.exists()) {
			loadConfig();
		}
		return config;
	}
	
}
