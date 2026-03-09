package local.simpleanticheat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ClientBrandCheckListener implements Listener {

    private final Set<String> blocked;

    public ClientBrandCheckListener(JavaPlugin plugin, PunishManager punishManager) {
        List<String> brands = plugin.getConfig().getStringList("client.blocked-brands");
        blocked = new HashSet<>();
        for (String b : brands) {
            blocked.add(b.toLowerCase(Locale.ROOT));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String brand;
        try {
            brand = player.getClientBrandName();
        } catch (Throwable t) {
            return;
        }

        if (brand == null) {
            return;
        }

        String lower = brand.toLowerCase(Locale.ROOT);
        for (String blockedBrand : blocked) {
            if (lower.contains(blockedBrand)) {
                player.kickPlayer(ChatColor.RED + "Blocked client/mod detected: " + blockedBrand);
                break;
            }
        }
    }
}
