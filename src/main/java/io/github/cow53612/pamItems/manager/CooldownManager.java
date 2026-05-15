package io.github.cow53612.pamItems.manager;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {

    private static final Map<String, Long> cooldownMap;

    public static void addCooldown(String cooldownId, long cooldownTime) {
        cooldownMap.put(cooldownId, cooldownTime);
    }

    public static long getCooldown(String cooldownId) {
        return cooldownMap.get(cooldownId);
    }

    public static boolean hasCooldown(String cooldownId) {
        return cooldownMap.containsKey(cooldownId);
    }

    static {
        cooldownMap = new HashMap<>();
    }

}
