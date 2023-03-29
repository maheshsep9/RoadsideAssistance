package org.geico.rsa.service;

import ch.hsr.geohash.GeoHash;
import org.geico.rsa.entity.Assistant;
import org.geico.rsa.entity.Geolocation;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class DataLoad {
    private ArrayList<Assistant> assistants = getAssistants();
    private  HashMap<String, ArrayList<Assistant>> assistantsReverseIndex = loadAssistantsHashMap();
    Map<String, Assistant> reservedAssistants = new ConcurrentHashMap<>();

    public ArrayList<Assistant> getAssistants() {
        // Wire the logic to get data from the underlying database
        assistants = new ArrayList<Assistant>();
        assistants.add(new Assistant("Assistant1", new Geolocation(39.2591, -77.2829), true));
        assistants.add(new Assistant("Assistant2", new Geolocation(39.2692, -77.2827), true));
        assistants.add(new Assistant("Assistant3", new Geolocation(39.2793, -77.2524), true));
        assistants.add(new Assistant("Assistant4", new Geolocation(39.2894, -77.2625), true));
        assistants.add(new Assistant("Assistant5", new Geolocation(39.3095, -77.2726), true));
        assistants.add(new Assistant("Assistant6", new Geolocation(39.3196, -77.2827), true));
        assistants.add(new Assistant("Assistant7", new Geolocation(39.3297, -77.2928), true));
        assistants.add(new Assistant("Assistant8", new Geolocation(39.3398, -77.3029), true));
        assistants.add(new Assistant("Assistant9", new Geolocation(39.3399, -77.3029), true));
        assistants.add(new Assistant("Assistant10", new Geolocation(39.3340, -77.3029), true));
        // In the scalable application ,we use the pub sub to build the Reverse Index in near real time.
        return assistants;
    }

    public HashMap<String, ArrayList<Assistant>> loadAssistantsHashMap() {
        assistantsReverseIndex = new HashMap<>();
        for(Assistant assistant: assistants) {
            assistantsReverseIndex.computeIfAbsent(assistant.getLocation().getGeoHash().toBase32(), k -> new ArrayList<>()).add(assistant);
        }
        return assistantsReverseIndex;
    }
    // O(1) operation
    public ArrayList<Assistant> getNearestAssistantByGeoHash(String geoHash) {
        ArrayList<Assistant> list = assistantsReverseIndex.get(geoHash);
        if (list != null && !list.isEmpty())
            return list;
        return null;
    }

    public Map<String, Assistant> getReservedAssistants() {
        return reservedAssistants;
    }

    public void saveReservedAssistants(String CustId, Assistant assistant) {
        this.reservedAssistants.put(CustId, assistant);
    }


}
