package androidx.work.impl.diagnostics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DiagnosticsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        throw new RuntimeException("Stub!");
    }
}
