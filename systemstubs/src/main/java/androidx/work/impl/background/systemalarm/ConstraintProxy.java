package androidx.work.impl.background.systemalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConstraintProxy {

    public static class BatteryChargingProxy extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            throw new RuntimeException("Stub");
        }
    }

    public static class BatteryNotLowProxy extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            throw new RuntimeException("Stub");
        }
    }

    public static class StorageNotLowProxy extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            throw new RuntimeException("Stub");
        }
    }

    public static class NetworkStateProxy extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            throw new RuntimeException("Stub");
        }
    }

}
