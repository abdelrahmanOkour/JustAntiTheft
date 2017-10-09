package com.example.justantitheft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Ahmad on 2016-12-24.
 */
public class BootCompletedIntentReceiver extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks {
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if(isConnected()) {
                Intent pushIntent = new Intent(context, EmailService.class);
                context.startService(pushIntent);
                Intent mapsIntent = new Intent(context, MapsService.class);
                context.startService(mapsIntent);
            }
        }
    }
    public boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent pushIntent = new Intent(context, EmailService.class);
        context.startService(pushIntent);
        Intent mapsIntent = new Intent(context, MapsService.class);
        context.startService(mapsIntent);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
