package sdk.chat.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HashMapHelper {

    public static Map<String, Object> expandStringMap(Map<String, String> flatMap) {
        Map<String, Object> map = new HashMap<>();
        for (String key: flatMap.keySet()) {
            map.put(key, flatMap.get(key));
        }
        return expand(map);
    }

    public static Map<String, Object> expand(Map<String, Object> flatMap) {

        Map<String, Object> expandedMap = new HashMap<>();

        for (String key : flatMap.keySet()) {
            String [] splitKey = key.split("/");
            Object value = flatMap.get(key);

            // In this case, we have a composite key i.e. A/B/C which needs to be expanded
            if (splitKey.length > 1) {

                // Split the key into the first part A and child part B/C
                String parentKey = splitKey[0];
                String childKey = key.replace(parentKey + "/", "");

                // Make a new child map which would contain the value keyed by the child key
                // i.e. B/C => Value
                Map<String, Object> childMap = new HashMap<>();
                childMap.put(childKey, value);

                // It may be that we already have a sub map, mapped to this key,
                // in that case get it
                Map<String, Object> existingMap = expandedMap.get(parentKey) instanceof Map ? (Map) expandedMap.get(parentKey) : new HashMap<>();

                // Expand the child map recursively and merge it with the existing map
                existingMap.putAll(expand(childMap));

                // Add existing map back in if necessary
                expandedMap.put(parentKey, existingMap);
            }
            else {
                expandedMap.put(key, value);
            }
        }
        return expandedMap;
    }

    public static Map<String, Object> flatten (Map<String, Object> map) {
        return flatten(map, null);
        //This function loops on itself. This is the initial part of it. The other flatten function with an extra variable is needed when the function begins looping on itself.
    }

    public static Map<String, Object> flatten(Map<String, Object> map, String previousKey) {
        HashMap<String, Object> outputMap = new HashMap<>();

        //Here we loop over the keyset, and retrieve each value for that.
        for (String key : map.keySet()) {
            Object value = map.get(key);

            //Here the key is determined based on whether it is a new key or a nested key.
            String newKey = previousKey == null ? key : previousKey + "/" + key;

            //If the value is a hashmap, the function loops on itself, otherwise the value is placed into the final map.
            // In Firebase if we have a number indexing the data, Firebase thinks its an array list.
            if (value instanceof ArrayList) {
                ArrayList arrayList = (ArrayList) value;
                Map<String, Object> tempMap = new HashMap<>();
                for(Integer i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) != null) {
                        tempMap.put(i.toString(), arrayList.get(i));
                    }
                }
                value = tempMap;
            }
            if (value instanceof HashMap) {
                outputMap.putAll(flatten((Map) value, newKey));
            }
            else if (value instanceof String) {
                outputMap.put(newKey, (String) value);
            }
        }
        return outputMap;
    }

    public static void putIfNotNull(Map<Object, Object> map, Object key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

}
