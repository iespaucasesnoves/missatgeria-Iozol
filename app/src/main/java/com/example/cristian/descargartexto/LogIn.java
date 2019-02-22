package com.example.cristian.descargartexto;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity {


    private TextView user;
    private TextView pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
    }

    public void preLogIn(View view){

        if ((user.getText() != null && !user.getText().toString().trim().isEmpty())&&(pass.getText() != null && !pass.getText().toString().trim().isEmpty())){
            HashMap<String, String> parm = new HashMap<>();
            parm.put("nom",user.getText().toString().toUpperCase());
            parm.put("password",pass.getText().toString().toUpperCase());
            String result = logIn(parm);
            if(!result.trim().isEmpty()) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getBoolean("correcta")) {
                        Intent intent = new Intent();
                        intent.putExtra("user", json.getString("dades"));
                        intent.putExtra("pass", pass.getText().toString().toUpperCase());
                        setResult(Activity.RESULT_OK, intent);
                    }
                } catch (JSONException e) {
                    Log.d("JSON_ERROR", e.getMessage());
                }
            }
        }
        Toast.makeText(this, "Usuario o contraseña Incorrecto", Toast.LENGTH_LONG).show();
    }

    public String logIn(HashMap<String, String> params){
        StringBuilder text = new StringBuilder();
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
            writer.write(montaParametres(params));
            writer.flush();
            writer.close();
            out.close();
            // Codi de la resposta
            int responseCode = httpURLConnection.getResponseCode();
            Log.d("RUN", "Descarrega "+responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Recollim texte
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
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

    private static String montaParametres(HashMap<String, String> params) throws
            UnsupportedEncodingException {
        // A partir d'un hashmap clau-valor cream
        // clau1=valor1&clau2=valor2&...
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) { first = false;} else {result.append("&");}
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
