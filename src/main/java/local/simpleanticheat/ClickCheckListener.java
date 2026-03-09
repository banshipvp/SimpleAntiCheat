package local.simpleanticheat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClickCheckListener implements Listener {

    private final JavaPlugin plugin;
    private final PunishManager punishManager;
    private final Map<UUID, Deque<Long>> clicks = new HashMap<>();

    public ClickCheckListener(JavaPlugin plugin, PunishManager punishManager) {
        this.plugin = plugin;
        this.punishManager = punishManager;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Deque<Long> queue = clicks.computeIfAbsent(player.getUniqueId(), k -> new ArrayDeque<>());
        long now = System.currentTimeMillis();
        queue.addLast(now);

        while (!queue.isEmpty() && now - queue.peekFirst() > 1000L) {
            queue.removeFirst();
        }

        int cpsThreshold = Math.max(12, plugin.getConfig().getInt("checks.autoclicker.cps-threshold", 24));
        int maxViol = Math.max(1, plugin.getConfig().getInt("checks.autoclicker.max-violations", 3));

        if (queue.size() > cpsThreshold) {
            punishManager.flag(player, "AutoClicker", maxViol);
        }
    }
}
