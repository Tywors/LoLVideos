package com.tywors.lolvideos.sql;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tywors.lolvideos.R;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 08/02/2015.
 */
public class GrabarFavorito extends AsyncTask<String,Void,String>{
    private String android_id;
    private Activity mContext;

    public GrabarFavorito(Activity context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        android_id = Secure.getString(mContext.getContentResolver(),Secure.ANDROID_ID);
    }


    protected void onPostExecute(String result) {
        switch (result.trim()){
            case "1":
                //Añadido a favoritos
                Toast toast = Toast.makeText(mContext, R.string.info_favorito_anyadido, Toast.LENGTH_LONG);
                View view = toast.getView();
                view.setBackgroundResource(R.drawable.style_toast);
                TextView text = (TextView) view.findViewById(android.R.id.message);
                text.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, mContext.getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, mContext.getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics()));
                text.setTypeface(Typeface.DEFAULT_BOLD);
                text.setTextColor(Color.WHITE);
                toast.show();

                toast.show();
                break;
            case "2":
                //Ya existe en favoritos
                Toast toast2 = Toast.makeText(mContext, R.string.info_favorito_existente, Toast.LENGTH_LONG);
                View view2 = toast2.getView();
                view2.setBackgroundResource(R.drawable.style_toast);
                TextView text2 = (TextView) view2.findViewById(android.R.id.message);
                text2.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, mContext.getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, mContext.getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics()));
                text2.setTypeface(Typeface.DEFAULT_BOLD);
                text2.setTextColor(Color.WHITE);
                toast2.show();
                break;
            case "3":
                //Borrado de faboritos
                Toast toast3 = Toast.makeText(mContext, R.string.info_favorito_borrado, Toast.LENGTH_LONG);
                View view3 = toast3.getView();
                view3.setBackgroundResource(R.drawable.style_toast);
                TextView text3 = (TextView) view3.findViewById(android.R.id.message);
                text3.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, mContext.getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, mContext.getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mContext.getResources().getDisplayMetrics()));
                text3.setTypeface(Typeface.DEFAULT_BOLD);
                text3.setTextColor(Color.WHITE);
                toast3.show();
                break;
        }
        ;
    }

    @Override
    protected String doInBackground(String... params) {

        //Utilizamos la clase Httpclient para conectar
        HttpClient httpclient = new DefaultHttpClient();
        //Utilizamos la HttpPost para enviar lso datos
        //A la url donde se encuentre nuestro archivo receptor
        HttpPost httppost = new HttpPost("http://tywors.com/" +
                "apps/lolvideos/andro_add_favoritos.php");
            //Añadimos los datos a enviar en este caso solo uno
            //que le llamamos de nombre 'a'
            //La segunda linea podría repetirse tantas veces como queramos
            //siempre cambiando el nombre ('a')
            List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
            postValues.add(new BasicNameValuePair("id_video", params[0]));
            postValues.add(new BasicNameValuePair("id_user", android_id));
            postValues.add(new BasicNameValuePair("id_device", GetUniqueID.getDeviceId(mContext)));
            postValues.add(new BasicNameValuePair("faboritos", params[1]));
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
        Log.d("LoLVideos","enviado---R: "+ resultado);
        Log.d("LoLVideos","id dispositivo ---"+ android_id);

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
