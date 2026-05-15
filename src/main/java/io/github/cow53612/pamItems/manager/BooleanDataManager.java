package io.github.cow53612.pamItems.manager;

import java.util.HashMap;
import java.util.Map;

public class BooleanDataManager {

    private static final Map<String, Boolean> booleanMap;

    public static void addData(String key, boolean data) {
        booleanMap.put(key, data);
    }

    public static void removeData(String key) {
        booleanMap.remove(key);
    }

    public static boolean getData(String key) {
        return booleanMap.get(key);
    }

    public static boolean hasData(String key) {
        return booleanMap.containsKey(key);
    }

    static {
        booleanMap = new HashMap<>();
    }
    
}
