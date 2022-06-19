package com.google.intelligence.sense.ambientmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class AmbientMusicDetector {

    public static class Service extends android.app.Service {
        @Override
        public IBinder onBind(Intent intent) {
            throw new RuntimeException("Stub!");
        }
    }

    public static class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            throw new RuntimeException("Stub!");
        }
    }

}
