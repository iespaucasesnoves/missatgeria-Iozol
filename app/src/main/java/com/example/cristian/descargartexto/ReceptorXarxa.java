package com.example.cristian.descargartexto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ReceptorXarxa extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Actualitzar l’estat de la xarxa
        ActualitzaEstatXarxa(context);
    }

    public void ActualitzaEstatXarxa(Context context) {
        //Obtenim un gestor de les connexions de xarxa
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Obtenim l’estat de la xarxa
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //Si està connectat

        if (networkInfo != null && networkInfo.isConnected()) {
            //Xarxa OK
            Toast.makeText(context, "Xarxa ok", Toast.LENGTH_LONG).show();
        } else {
            //Xarxa no disponible
            Toast.makeText(context, "Xarxa no disponible", Toast.LENGTH_LONG).show();
        }

        //Obtenim l’estat de la xarxa mòbil
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean connectat3G = networkInfo.isConnected();
        //Obtenim l’estat de la xarxa Wifi
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean connectatWifi = networkInfo.isConnected();
    }
}
