package com.google.intelligence.sense.ambientmusic.reload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ModelReloadService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        throw new RuntimeException("Stub!");
    }
}
