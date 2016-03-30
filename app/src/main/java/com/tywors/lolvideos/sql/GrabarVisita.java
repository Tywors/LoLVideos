package com.tywors.lolvideos.sql;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tywors.lolvideos.data.GetUniqueID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 08/02/2015.
 */
public class GrabarVisita extends AsyncTask<String,Void,String>{
    private Activity mContext;
    private String id_device;

    public GrabarVisita(Activity context) {
        mContext = context;
    }
    private String line;

    @Override
    protected void onPreExecute() {
        id_device = GetUniqueID.getDeviceId(mContext);
    }


    protected void onPostExecute(String result) {

    }

    @Override
    protected String doInBackground(String... params) {
        StringBuffer chaine = new StringBuffer("");
        java.net.URL url = null;


        try {
            url = new java.net.URL("http://tywors.com/" +
            "apps/lolvideos/xxxxxx.php?id_video=" + params[0] +"&id_device="+id_device);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        //connection.setRequestProperty("User-Agent", "");
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();
        InputStream inputStream = connection.getInputStream();

        BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

        while ((line = rd.readLine()) != null) {
            chaine.append(line);
        }
    } catch (IOException e) {
        // writing exception to log
        e.printStackTrace();
    }
    return chaine.toString();

    }


    //Funcion para 'limpiar' el codigo recibido
    public String StreamToString(InputStream is) {
        //Creamos el Buffer
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            //Bucle para leer todas las líneas
            //En este ejemplo al ser solo 1 la respuesta
            //Pues no haría falta
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //retornamos el codigo límpio
        return sb.toString();
    }

}
