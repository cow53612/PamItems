package io.github.cow53612.pamItems.manager;

import java.util.HashMap;
import java.util.Map;

public class DoubleDataManager {

    private static final Map<String, Double> doubleMap;

    public static void addData(String key, double data) {
        doubleMap.put(key, data);
    }

    public static void removeData(String key) {
        doubleMap.remove(key);
    }

    public static double getData(String key) {
        return doubleMap.get(key);
    }

    public static boolean hasData(String key) {
        return doubleMap.containsKey(key);
    }

    static {
        doubleMap = new HashMap<>();
    }

}
