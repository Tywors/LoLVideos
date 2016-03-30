package com.tywors.lolvideos.sql;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tywors.lolvideos.adapter.MyVideosAdapter;
import com.tywors.lolvideos.data.GetUniqueID;
import com.tywors.lolvideos.objects.MyVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 15/12/2015.
 */
public class DownloadMyVideos extends AsyncTask<String,Integer,String> {

    private String line;
    private MyVideosAdapter adapter;
    private String URL_ = "http://tywors.com/apps/lolvideos/";
    private Activity a;
    private ProgressBar progres;
    private List<MyVideo> lst_myvideos;

    public DownloadMyVideos(Activity a, MyVideosAdapter adapter, ProgressBar progres){
        this.adapter = adapter;
        this.a = a;
        this.progres = progres;
        lst_myvideos = new ArrayList<MyVideo>();
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuffer chaine = new StringBuffer("");
        Log.d("lolvideos","UI: "+GetUniqueID.getDeviceId(a.getApplicationContext()));
        try {

            java.net.URL url = new java.net.URL(URL_ + "andro_get_myvideos.php?id_device=" + GetUniqueID.getDeviceId(a.getApplicationContext()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }

            JSONObject jsonObj = new JSONObject(chaine.toString());
            JSONArray jArray = jsonObj.getJSONArray("c");

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String id = jObject.getString("id");
                String ruta = jObject.getString("ruta");
                String thumbnail = jObject.getString("thumbnail");
                String visitas = jObject.getString("visitas");
                String activo = jObject.getString("activo");
                String fecha = jObject.getString("fecha");
                String favoritos = jObject.getString("favoritos");
                String nota = jObject.getString("nota");
                String visitas_unicas = jObject.getString("visitas_unicas");
                if(visitas_unicas.equals("null"))visitas_unicas="0";
                String votos = jObject.getString("votos");

                MyVideo my = new MyVideo();
                my.setVisitas(visitas);
                my.setActivo(activo);
                my.setVisitas_unicas(visitas_unicas);
                my.setFecha(fecha);
                my.setId(id);
                my.setThumbnail(thumbnail);
                my.setRuta(ruta);
                my.setFavoritos(favoritos);
                my.setNota(nota);
                my.setVotos(votos);
                lst_myvideos.add(my);

            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return chaine.toString();
    }
    @Override
    protected void onPostExecute(String result) {
        //Toast.makeText(getApplicationContext(), "size array: " + lst_myvideos.size(), Toast.LENGTH_LONG).show();
        // AddInListView(lst_myvideos);
        adapter.setData(lst_myvideos);
        progres.setVisibility(View.GONE);
        Log.d("lolvideos", "RESULTADO MISVIDEOS: " + result);
    }
}