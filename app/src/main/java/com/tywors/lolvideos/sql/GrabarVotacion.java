package com.tywors.lolvideos.sql;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 08/02/2015.
 */
public class GrabarVotacion extends AsyncTask<String,Void,String>{
    private Activity mContext;

    public GrabarVotacion(Activity context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
    }


    protected void onPostExecute(String result) {

    }

    @Override
    protected String doInBackground(String... params) {

        //Utilizamos la clase Httpclient para conectar
        HttpClient httpclient = new DefaultHttpClient();
        //Utilizamos la HttpPost para enviar lso datos
        //A la url donde se encuentre nuestro archivo receptor
        HttpPost httppost = new HttpPost("http://tywors.com/" +
                "apps/lolvideos/xxxxx.php");
            //Añadimos los datos a enviar en este caso solo uno
            //que le llamamos de nombre 'a'
            //La segunda linea podría repetirse tantas veces como queramos
            //siempre cambiando el nombre ('a')
            List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
            postValues.add(new BasicNameValuePair("id_video", params[0]));
            postValues.add(new BasicNameValuePair("num_estrellas", params[1]));
            //Encapsulamos
            try {
                httppost.setEntity(new UrlEncodedFormEntity(postValues));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //Lanzamos la petición
            HttpResponse respuesta = null;
            try {
                respuesta = httpclient.execute(httppost);
                Log.d("lolvideos","@@@@@-----------------VOTADO----------------@@@@@@");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Conectamos para recibir datos de respuesta
            HttpEntity entity = respuesta.getEntity();
            InputStream is = null;
            try {
                is = entity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        String resultado= StreamToString(is);
        //Toast.makeText(mContext,"Video favorito 2",Toast.LENGTH_LONG).show();

       // Log.d("LoLVideos","id dispositivo ---"+ android_id);

            return resultado;
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
