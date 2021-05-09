package com.ragav.cashkaro.Broadcast;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import static android.content.Intent.EXTRA_CHOSEN_COMPONENT;

public class SharedAppReceiver extends BroadcastReceiver {
    private static Listener listener;

     public void setData(Listener mlistener) {
        listener = mlistener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String selectedAppPackage = ((ComponentName)intent.getExtras().get(EXTRA_CHOSEN_COMPONENT)).getPackageName();
        listener.receivedData(selectedAppPackage);
    }

    public interface Listener {
         void receivedData(String data);
    }
}
