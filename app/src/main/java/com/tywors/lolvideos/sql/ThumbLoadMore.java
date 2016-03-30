package com.tywors.lolvideos.sql;

import android.app.Activity;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.tywors.lolvideos.Main_Principal;
import com.tywors.lolvideos.R;
import com.tywors.lolvideos.adapter.ThumbListAdapter;
import com.tywors.lolvideos.objects.Thumbnail;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by W7 on 18/01/2015.
 */
public class ThumbLoadMore extends AsyncTask<Integer, Void, Boolean> {

    private Activity activity;
    private ThumbListAdapter adapter;
    private List<Thumbnail> thumbnails = new ArrayList<Thumbnail>();
    ////////////////////////////////
    ////////////////////////////
    ///////////////////////////////
    private String URL = "http://tywors.com/apps/lolvideos/andro_get_thumbnails_videos.php?pagina=";
    private String URL_img_web = "http://tywors.com/apps/lolvideos/thumbnails/";
    private JSONObject jsonObj;
    private JSONArray jArray;
    private boolean error = false;
    HttpClient httpclient = new DefaultHttpClient();
    HttpResponse response;
    private ProgressBar progressBar;

    public ThumbLoadMore(ProgressBar progressBar, Main_Principal activity, ThumbListAdapter adapter){
        this.progressBar = progressBar;
        this.activity = activity;
        this.adapter = adapter;
    }


    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d("LoLVideos", "Empieza el PRE de descarga de thumbnails");
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        int npagina = params[0];
        String url_api = "";
        //miramos si quiere los faboritos

        switch(params[1]) {
            case 0:
                url_api = URL + String.valueOf(npagina) + "&tipo="+params[1];
                break;
            case 1:
                String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                url_api = URL + String.valueOf(npagina) + "&android_id=" + android_id + "&tipo="+params[1] ;
                break;
            case 2:
                url_api = URL + String.valueOf(npagina) + "&tipo="+params[1];
                break;
            case 3:
                url_api = URL + String.valueOf(npagina) + "&tipo="+params[1];
                break;
        }




        Log.d("LoLVideos", "Empieza el DoBackground de descarga de thumbnails");
        Log.d("LoLVideos", "---Num PAgina ----------- :"+npagina);
        try {
            Log.d("LoLVideos", "Empieza el DoBackground de descarga de thumbnails (dentro del try 1)");
            response = httpclient.execute(new HttpGet(url_api));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                jsonObj = new JSONObject(EntityUtils.toString(response.getEntity()));
                jArray = jsonObj.getJSONArray("c");

                for(int i=0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String img_url_1 = jObject.getString("thumbnail_1");
                    String img_url_2 = jObject.getString("thumbnail_2");
                    String visita_1 = jObject.getString("visita_1");
                     String visita_2 = jObject.getString("visita_2");
                    String ruta_video_1 = jObject.getString("ruta_1");
                    String ruta_video_2 = jObject.getString("ruta_2");
                     String fecha_1 = jObject.getString("fecha_1");
                    String fecha_2 = jObject.getString("fecha_2");
                    String id_1 = jObject.getString("id_1");
                    String id_2 = jObject.getString("id_2");
                    String nota_1 = jObject.getString("nota_1");
                    String nota_2 = jObject.getString("nota_2");

                    String cont_videos_1 = jObject.getString("cont_videos_1");
                    String cont_videos_2 = jObject.getString("cont_videos_2");
                    Thumbnail p = new Thumbnail();
                    p.setImage_url_1(URL_img_web + img_url_1);
                    Log.d("LoLVideos", "Empieza el DoBackground de descarga de thumbnails 1" + URL_img_web + img_url_1);
                    p.setImage_url_2(URL_img_web + img_url_2);
                    Log.d("LoLVideos", "Empieza el DoBackground de descarga de thumbnails 2" + URL_img_web + img_url_2);
                    p.setFecha_1(fecha_1);
                    p.setFecha_2(fecha_2);
                    p.setRuta_video_1(ruta_video_1);
                    p.setRuta_video_2(ruta_video_2);
                    p.setFecha_1(fecha_1);
                    p.setFecha_2(fecha_2);
                    p.setId_1(id_1);
                    p.setId_2(id_2);
                    p.setNota_1(nota_1);
                    p.setNota_2(nota_2);
                    p.setVisita_1(visita_1);
                    p.setVisita_2(visita_2);
                    p.setCont_videos_1(cont_videos_1);
                    p.setCont_videos_2(cont_videos_2);

                    ////
                    p.setFavorito_1(R.drawable.estrella);
                    //p.setFavorito_2(false);

                    this.thumbnails.add(p);
                }
                return true;
            }else{
                Log.d("LoLVideos", "FALLA JSON al recibir los thumbnails 1");

                return false;

            }
        }catch (Exception e){
                Log.d("LoLVideos", "FALLA JSON al recibir los thumbnails 2");
            Log.d("LoLVideos", "ddd" + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result){
            Log.d("LoLVideos", "Ya se ha descargado al menos un Thumbnail");

            adapter.setData(thumbnails);
            Log.d("LoLVideos", "---> 1 - La respuesta de faboritos: "+result.toString());
        }else{
            Log.d("LoLVideos", "---> 2 - La respuesta de faboritos: "+result.toString());


        }

        Log.d("LoLVideos", "---> 3 - La respuesta de faboritos: "+jsonObj);


        progressBar.setVisibility(View.INVISIBLE);
    }

}
