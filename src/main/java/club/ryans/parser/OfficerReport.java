package club.ryans.parser;

import club.ryans.models.Officer;
import club.ryans.models.managers.OfficerManager;
import lombok.Data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OfficerReport {
    private Map<String, OfficerProcCount> countMap = new HashMap<>();

    void addProc(final String officer, final String ability) {
        String key = String.format("[%s-%s]", officer, ability);
        if (countMap.containsKey(key)) {
            countMap.get(key).increment();
        } else {
            countMap.put(key, new OfficerProcCount(null, officer, ability, 1));
        }
    }

    public Collection<OfficerProcCount> getCounts() {
        return countMap.values();
    }

    public void findOfficers(final OfficerManager manager) {
        countMap.values().forEach(officerProcCount -> {
            Officer officer = manager.getOfficer(officerProcCount.getOfficerName());
            officerProcCount.setOfficer(officer);
        });

    }
}