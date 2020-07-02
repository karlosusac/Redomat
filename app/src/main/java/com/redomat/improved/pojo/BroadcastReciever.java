package com.redomat.improved.pojo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AlertDialog;

public class BroadcastReciever extends BroadcastReceiver {

    static AlertDialog noInternetConnection;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            final boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if(noInternetConnection != null){
                noInternetConnection.dismiss();
            }
            noInternetConnection = new AlertDialog.Builder(context)
                    .setTitle("Nema konekcije sa internetom")
                    .setMessage("Spojite se na internet da bi ste nastavili sa radom.")
                    .setNegativeButton("Zatvori", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Close
                        }
                    })
                    .show();

            if (!noConnectivity) {
                noInternetConnection.dismiss();
            }
        }
    }
}
