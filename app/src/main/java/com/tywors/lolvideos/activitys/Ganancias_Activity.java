package com.tywors.lolvideos.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tywors.lolvideos.R;
import com.tywors.lolvideos.adapter.MyHistorialPagosAdapter;
import com.tywors.lolvideos.adapter.MyVideosAdapter;
import com.tywors.lolvideos.data.GetUniqueID;
import com.tywors.lolvideos.objects.MyGananciasHistorial;
import com.tywors.lolvideos.objects.MyVideo;
import com.tywors.lolvideos.sql.DownloadMyVideos;
import com.tywors.lolvideos.utils.CustomMultiPartEntity;
import com.tywors.lolvideos.utils.MultipartUtility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 14/12/2015.
 */
public class Ganancias_Activity extends AppCompatActivity {

    private String URL_ = "http://tywors.com/apps/lolvideos/";
    private AlertDialog builder;
    private TextView txt_visitas_unicas;
    private TextView txt_btc_ganados;
    private TextView txt_btc_totalapp;
    private Button bt_send_btc;
    private EditText ed_dir_btc;
    private String dir_btc;

    private MyHistorialPagosAdapter adapter;
    private ListView listview;
    private List<MyGananciasHistorial> lst_myganancias;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ganancias);

        txt_visitas_unicas = (TextView)findViewById(R.id.txt_pagos_visitas_disp);
        txt_btc_totalapp = (TextView)findViewById(R.id.txt_pagos_total_app);

        bt_send_btc = (Button)findViewById(R.id.bt_pagos_btc);
        ed_dir_btc = (EditText)findViewById(R.id.ed_pagos_btc);
        txt_btc_ganados = (TextView)findViewById(R.id.txt_btc_ganados);
        listview = (ListView)findViewById(R.id.lst_historial_pagos);

        loading_();
        new EstadisticasBtc().execute();

        bt_send_btc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed_dir_btc.getText().toString().length() > 27 && ed_dir_btc.getText().toString().length() < 35 ){
                    dir_btc = ed_dir_btc.getText().toString();
                    new PedirPago().execute();
                }else{
                    _Toast(getApplicationContext().getResources().getString(R.string.dialog_dir_invalida));
                }
            }
        });



    }



    public class PedirPago extends AsyncTask<String,Void,String> {
        private Activity mContext;
        private String id_device;

        public PedirPago(){
        }
        private String line;

        @Override
        protected void onPreExecute() {
            id_device = GetUniqueID.getDeviceId(mContext);
            loading_();
        }

        protected void onPostExecute(String result) {
            builder.dismiss();
            if(result.equals("0")){
                _Toast(getApplicationContext().getResources().getString(R.string.dialog_pago_minimo));
            }else if(result.equals("1")){
                _Toast(getApplicationContext().getResources().getString(R.string.dialog_btc_exito));
                finish();
            }else{
                _Toast(getApplicationContext().getResources().getString(R.string.dialog_btc_error)+"---------");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer chaine = new StringBuffer("");
            java.net.URL url = null;

            try {
                url = new java.net.URL(URL_ +"andro_anyadir_pago.php?id_device="+ GetUniqueID.getDeviceId(getApplicationContext())+"&dir_btc="+dir_btc);

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
            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();
            }
            return chaine.toString();
        }
    }


    private void CargarStadisticas(String json) {
        try {
            JSONObject j1 = new JSONObject(json);
            txt_visitas_unicas.setText(j1.getString("visitas_unicas"));
            txt_btc_totalapp.setText(getApplicationContext().getResources().getString(R.string.btc_total_app)+" "+String.format("%.8f",(Double.parseDouble(j1.getString("total_app"))/100000000)).toString()+" \u0E3F");
            //no hay suficientes
            //if(Integer.parseInt(j1.getString("visitas_unicas")) < 10000){
            //    _Toast(getApplicationContext().getResources().getString(R.string.dialog_pago_minimo));
            //    finish();
           // }
           // _Toast(getApplicationContext().getResources().getString(R.string.dialog_btc_asegurese));
            String btc_win;
            btc_win = String.format("%.8f",(Double.parseDouble(j1.getString("visitas_unicas"))/100000000)).toString() + " \u0E3F";


            txt_btc_ganados.setText(btc_win);


            lst_myganancias = new ArrayList<MyGananciasHistorial>();

            JSONObject js1 = new JSONObject(j1.getString("historial"));

            for(int a=0;a<Integer.parseInt(j1.getString("total_historial"));a++){
                MyGananciasHistorial my = new MyGananciasHistorial();
                String fecha = new String(js1.getString("fecha" + a));
                String[] parts = fecha.split(" ");
                String[] parts2 = parts[0].split("-");
                my.setDate(parts2[2]+"/"+parts2[1]);
                my.setAmount(String.format("%.8f", (Double.parseDouble(js1.getString("amount" + a)) / 100000000)).toString() + "\u0E3F");
                my.setDir_btc(js1.getString("dir_btc" + a));

                //le escribimos la direccion para que no tenga que escribir
                ed_dir_btc.setText(js1.getString("ultima_dir"));

                switch (js1.getString("status"+a)){
                    case "0":
                        my.setStatus(getApplicationContext().getResources().getString(R.string.btc_pago_pendiente));
                        break;
                    case "1":
                        my.setStatus(getApplicationContext().getResources().getString(R.string.btc_pago_realizado));
                        break;
                    case "2":
                        my.setStatus(getApplicationContext().getResources().getString(R.string.btc_pago_error));
                        break;
                }
                lst_myganancias.add(my);
            }


            adapter = new MyHistorialPagosAdapter(Ganancias_Activity.this, lst_myganancias);
            listview.setAdapter(adapter);
            listview.invalidate();




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void _Toast(String sms) {
        Toast toast2 = Toast.makeText(getApplicationContext(), sms, Toast.LENGTH_LONG);
        View view2 = toast2.getView();
        view2.setBackgroundResource(R.drawable.style_toast);
        TextView text2 = (TextView) view2.findViewById(android.R.id.message);
        text2.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getApplicationContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getApplicationContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getApplicationContext().getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getApplicationContext().getResources().getDisplayMetrics()));
        text2.setTypeface(Typeface.DEFAULT_BOLD);
        text2.setTextColor(Color.WHITE);
        toast2.show();
    }


    class EstadisticasBtc extends AsyncTask<String, Void, String> {

        private String line;

        public EstadisticasBtc() {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer chaine = new StringBuffer("");
            try {

                java.net.URL url = new java.net.URL(URL_ + "xxxxxxxx.php?id_device=" + GetUniqueID.getDeviceId(getApplicationContext()));
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
            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();
            }

            return chaine.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("lolvideos",result);
            builder.dismiss();
            CargarStadisticas(result);
        }
    }


    public void loading_(){
        builder = new AlertDialog.Builder(this).create();

        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.layout_loading, null));
        builder.setCancelable(false);

        builder.show();
    }
}


