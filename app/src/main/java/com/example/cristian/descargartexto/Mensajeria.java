package com.example.cristian.descargartexto;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Mensajeria extends AsyncTask<String, Integer, String>{

    private BufferedReader in;
    private int responseCode = -1;
    private ListAdapter adapter;
    private ListView lv;
    private Context context;

    public Mensajeria(ListView lv, Context context){
        this.lv = lv;
        this.context = context;
    }

    @Override
    protected  void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder text = new StringBuilder();
        try {
            // Agafam la URL que s'ha passat com argument
            URL url = new URL("http://iesmantpc.000webhostapp.com/public/usuari/");
            // Feim la connexió a la URL
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            // Codi de la resposta
            responseCode = httpURLConnection.getResponseCode();
            Log.d("RUN", "Descarrega "+responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Recollim texte
                in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String liniatxt;
                while ((liniatxt = in.readLine()) != null) {
                    text.append(liniatxt);
                }
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    @Override
    protected  void onProgressUpdate(Integer... values){

    }

    @Override
    protected void onPostExecute(String result) {
        // Quan ha acabat la tasca, Agafam string que es un JSON
        // Parse
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jArray = json.getJSONArray("dades");
            // Llista de descarregues
            ArrayList<HashMap<String, String>> llista = new ArrayList<>();
            // Guarda a la llista
            for (int i = 0; i < jArray.length(); i++) {
                HashMap<String, String> map = new HashMap<>();
                JSONObject jObject = jArray.getJSONObject(i);
                map.put("ID",jObject.getString("id"));
                map.put("NOM",jObject.getString("nom"));
                map.put("EMAIL",jObject.getString("email"));
                map.put("ROL",jObject.getString("fk_role"));
                llista.add(map);
            }

            adapter = new SimpleAdapter(context, llista, R.layout.lista_json,
                    new String[]{"ID", "NOM", "EMAIL", "ROL"},
                    new int[]{R.id.id, R.id.nom, R.id.email, R.id.rol});
            lv.setAdapter(adapter);

        } catch (JSONException e){
            e.printStackTrace();
        }
    }


}
