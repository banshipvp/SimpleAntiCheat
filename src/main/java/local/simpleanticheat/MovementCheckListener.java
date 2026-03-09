package local.simpleanticheat;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementCheckListener implements Listener {

    private final JavaPlugin plugin;
    private final PunishManager punishManager;
    private final Map<UUID, Integer> consecutiveJumps = new HashMap<>();

    public MovementCheckListener(JavaPlugin plugin, PunishManager punishManager) {
        this.plugin = plugin;
        this.punishManager = punishManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.isFlying()) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;

        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        double maxHorizontal = Math.max(0.7, plugin.getConfig().getDouble("checks.speed.max-horizontal-per-tick", 1.1));
        int speedViol = Math.max(1, plugin.getConfig().getInt("checks.speed.max-violations", 3));
        if (horizontal > maxHorizontal && player.getFallDistance() < 1.0f) {
            punishManager.flag(player, "Speed", speedViol);
        }

        int minConsecutive = Math.max(4, plugin.getConfig().getInt("checks.bhop.min-consecutive-jumps", 6));
        int bhopViol = Math.max(1, plugin.getConfig().getInt("checks.bhop.max-violations", 2));

        if (!player.isOnGround() && Math.abs(to.getY() - from.getY()) > 0.35 && horizontal > 0.35) {
            int count = consecutiveJumps.merge(player.getUniqueId(), 1, Integer::sum);
            if (count >= minConsecutive) {
                punishManager.flag(player, "BHop", bhopViol);
                consecutiveJumps.put(player.getUniqueId(), 0);
            }
        } else if (player.isOnGround()) {
            consecutiveJumps.put(player.getUniqueId(), 0);
        }
    }
}
