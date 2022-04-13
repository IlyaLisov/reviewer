package com.example.reviewer.filter;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService {
    private static final int THRESHOLD = 150;
    private static DefaultDosProtectionService instance;
    private final Map<String, Long> countMap = new ConcurrentHashMap<>();

    private DefaultDosProtectionService() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                countMap.clear();
            }
        };

        Timer timer = new Timer("Timer");
        timer.schedule(timerTask, 60 * 1000L);
    }

    public static DefaultDosProtectionService getInstance() {
        if (instance == null) {
            instance = new DefaultDosProtectionService();
        }
        return instance;
    }

    public boolean isAllowed(String ip) {
        Long count = countMap.get(ip);
        if (count == null) {
            count = 1L;
        } else {
            if (count > THRESHOLD) {
                return false;
            }
            count++;
        }
        countMap.put(ip, count);
        return true;
    }
}
