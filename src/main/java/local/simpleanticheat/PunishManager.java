package local.simpleanticheat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PunishManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Map<String, Integer>> violations = new ConcurrentHashMap<>();

    public PunishManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void flag(Player player, String check, int maxViolations) {
        if (player.hasPermission("simpleanticheat.bypass")) {
            return;
        }

        int vl = violations
                .computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>())
                .merge(check, 1, Integer::sum);

        if (vl >= Math.max(1, maxViolations)) {
            punish(player, check);
        }
    }

    private void punish(Player player, String check) {
        boolean instant = plugin.getConfig().getBoolean("punishment.instant-ban", true);
        String broadcast = plugin.getConfig().getString("punishment.broadcast-format", "&c%player% has been banned for &e%check%");
        String formatted = ChatColor.translateAlternateColorCodes('&',
                broadcast.replace("%player%", player.getName()).replace("%check%", check));

        Bukkit.broadcastMessage(formatted);

        if (instant) {
            String cmd = plugin.getConfig().getString("punishment.ban-command", "ban %player% Unfair Advantage (%check%)");
            cmd = cmd.replace("%player%", player.getName()).replace("%check%", check);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        } else {
            player.kickPlayer("Unfair Advantage (" + check + ")");
        }

        violations.remove(player.getUniqueId());
    }
}
