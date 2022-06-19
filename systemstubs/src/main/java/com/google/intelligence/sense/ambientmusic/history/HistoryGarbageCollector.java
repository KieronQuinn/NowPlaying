package com.google.intelligence.sense.ambientmusic.history;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HistoryGarbageCollector extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        throw new RuntimeException("Stub!");
    }
}
