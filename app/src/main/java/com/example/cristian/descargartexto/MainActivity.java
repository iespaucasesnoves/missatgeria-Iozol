package com.example.cristian.descargartexto;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    private ReceptorXarxa receptor;
    private final int LOGIN = 1;
    private Intent intent;
    private Preferencies pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new Preferencies(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (connection(pref.getUser(), pref.getPassword())) {
            intent = new Intent(this, LogIn.class);
            startActivityForResult(intent, LOGIN);
        }

        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        receptor = new ReceptorXarxa();
        this.registerReceiver(receptor, filter);
    }

    protected void mostrar(View view) {
        new Mensajeria(lv, this).execute();
    }

    public void onDestroy() {
        super.onDestroy();
        //Donam de baixa el receptor de broadcast quan es destrueix l’aplicació
        if (receptor != null) {
            this.unregisterReceiver(receptor);
        }
    }

    private Boolean connection(String user, String pass) {
        StringBuilder text = new StringBuilder();
        Boolean result = false;
        try {
            // Agafam la URL que s'ha passat com argument
            URL url = new URL("http://iesmantpc.000webhostapp.com/public/login/");
            // Feim la connexió a la URL
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setChunkedStreamingMode(25000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            OutputStream out = httpURLConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(user + "&" + pass);
            writer.flush();
            writer.close();
            out.close();
            // Codi de la resposta
            int responseCode = httpURLConnection.getResponseCode();
            Log.d("RUN", "Descarrega " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Recollim texte
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String liniatxt;
                while ((liniatxt = in.readLine()) != null) {
                    text.append(liniatxt);
                }
                in.close();
            }
            if (!text.toString().trim().isEmpty()) {
                JSONObject json = new JSONObject(text.toString());
                result = json.getBoolean("correcta");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d("JSON_ERROR", e.getMessage());
        }

        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOGIN) {
            if (resultCode == Activity.RESULT_OK) {

                try {
                    JSONObject user = new JSONObject(data.getStringExtra("user"));
                    pref.setCodiusuari(Integer.parseInt(user.getString("codiusuari")));
                    pref.setUser(user.getString("nom"));
                    pref.setToken(user.getString("token"));
                    pref.setPassword(data.getStringExtra("pass"));
                } catch (JSONException e) {
                    Log.d("JSON_ERROR", e.getMessage());
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                intent = new Intent(this, LogIn.class);
            }
        }
    }//onActivityResult

}

