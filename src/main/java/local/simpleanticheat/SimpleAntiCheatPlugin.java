package local.simpleanticheat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleAntiCheatPlugin extends JavaPlugin {

    private PunishManager punishManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.punishManager = new PunishManager(this);

        Bukkit.getPluginManager().registerEvents(new ClickCheckListener(this, punishManager), this);
        Bukkit.getPluginManager().registerEvents(new CombatCheckListener(this, punishManager), this);
        Bukkit.getPluginManager().registerEvents(new MovementCheckListener(this, punishManager), this);
        Bukkit.getPluginManager().registerEvents(new ClientBrandCheckListener(this, punishManager), this);

        getLogger().info("SimpleAntiCheat enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SimpleAntiCheat disabled.");
    }
}
