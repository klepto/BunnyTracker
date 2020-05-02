package dev.klepto.bunnytracker.record.api;

import dev.klepto.bunnytracker.record.Record;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://klepto.dev/">Augustinas R.</a>
 */
public class ApiMapUtils {

    private static final Map<String, Record.Map> mapCache = new HashMap<>();

    public static Record.Map getMap(String mapName) {
        if (!mapCache.containsKey(mapName)) {
            val map = new Record.Map(mapName, getRecordsUrl(mapName), getImageUrl(mapName));
            mapCache.put(mapName, map);
        }

        return mapCache.get(mapName);
    }

    public static String getRecordsUrl(String mapName) {
        return "https://ut4bt.ga/map/" + mapName;
    }

    public static String getImageUrl(String mapName) {
        val imageName = mapName.replaceAll("/[-_]v?[0-9]+[a-z]?$/i", "").substring(3).toLowerCase();
        return "https://ut4bt.ga/img/maps/" + imageName + ".jpg";
    }

}
