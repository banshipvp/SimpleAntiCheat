package local.simpleanticheat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatCheckListener implements Listener {

    private final JavaPlugin plugin;
    private final PunishManager punishManager;
    private final Map<UUID, Float> lastYaw = new HashMap<>();

    public CombatCheckListener(JavaPlugin plugin, PunishManager punishManager) {
        this.plugin = plugin;
        this.punishManager = punishManager;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) {
            return;
        }

        double maxReach = Math.max(3.0, plugin.getConfig().getDouble("checks.reach.max-distance", 3.35));
        int reachViol = Math.max(1, plugin.getConfig().getInt("checks.reach.max-violations", 3));
        int auraViol = Math.max(1, plugin.getConfig().getInt("checks.killaura.max-violations", 3));

        Location eye = attacker.getEyeLocation();
        Location victimLoc = victim.getLocation().add(0, 1.0, 0);
        double dist = eye.distance(victimLoc);
        if (dist > maxReach) {
            punishManager.flag(attacker, "Reach", reachViol);
        }

        Float prevYaw = lastYaw.put(attacker.getUniqueId(), attacker.getLocation().getYaw());
        if (prevYaw != null) {
            float delta = Math.abs(attacker.getLocation().getYaw() - prevYaw);
            if (delta > 120.0f && dist < 2.2) {
                punishManager.flag(attacker, "KillAura", auraViol);
            }
        }
    }
}
