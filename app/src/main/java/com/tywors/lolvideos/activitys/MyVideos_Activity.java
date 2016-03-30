package com.tywors.lolvideos.activitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tywors.lolvideos.Main_Principal;
import com.tywors.lolvideos.R;
import com.tywors.lolvideos.adapter.MyVideosAdapter;
import com.tywors.lolvideos.data.GetUniqueID;
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
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 14/12/2015.
 */
public class MyVideos_Activity extends AppCompatActivity {

    private static final int STATIC_UPLOAD_VIDEO = 10001;
    private ListView listview;
    private MyVideosAdapter adapter;
    private static final int STATIC_IMAGE_SELECT = 10004;


    private String URL_ = "http://tywors.com/apps/lolvideos/";
    private List<MyVideo> lst_myvideos;
    private ImageView img_profile;
    private TextView txt_nick;
    private TextView txt_total_videos;
    private TextView txt_total_visitas;
    private TextView txt_visitas_unicas;
    private Button bt_eleminar_rechazados;
    private RatingBar rating_profile;
    private Bitmap selectedBitmap;
    private Button bt_upload;
    private ProgressBar progres_my;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_videos);

        listview = (ListView) findViewById(R.id.listView_myvideos);
        lst_myvideos = new ArrayList<MyVideo>();
        adapter = new MyVideosAdapter(MyVideos_Activity.this, lst_myvideos);
        listview.setAdapter(adapter);

        img_profile = (ImageView) findViewById(R.id.img_myvideos_profile);
        txt_nick = (TextView) findViewById(R.id.txt_myvideos_nick);
        txt_total_videos = (TextView) findViewById(R.id.txt_myvideos_totalvideos);
        txt_total_visitas = (TextView) findViewById(R.id.txt_myvideos_totalvisitas);
        txt_visitas_unicas = (TextView) findViewById(R.id.txt_myvideos_visitasunicas);
        rating_profile = (RatingBar)findViewById(R.id.ratingBar_myvideos_profile);
        bt_upload = (Button) findViewById(R.id.bt_myvideos_upload);




        //cargando en listview
        LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.footer_myvideos_listview, null);
        LinearLayout footerLinearLayout = new LinearLayout(this);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                150);
        footerLinearLayout.setGravity(Gravity.CENTER);
        footerLinearLayout.setLayoutParams(layoutParams);
        footerLinearLayout.addView(myView);

        progres_my = (ProgressBar)footerLinearLayout.findViewById(R.id.progressBar_myvideos);
        progres_my.setVisibility(View.VISIBLE);

        listview.addFooterView(footerLinearLayout);

        //ejecutamos
        new EstadisticasUser().execute();
        new DownloadMyVideos(MyVideos_Activity.this, adapter,progres_my).execute();

        bt_eleminar_rechazados = (Button)footerLinearLayout.findViewById(R.id.bt_footer_eliminar);
        bt_eleminar_rechazados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeleteFailVideos().execute();
            }
        });


        //Al clicar en la foto la cambiamos
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _Toast(getApplicationContext().getResources().getString(R.string.cambiar_la_imagen));
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                photoPickerIntent.putExtra("crop", "true");
                photoPickerIntent.putExtra("aspectX", 1);
                photoPickerIntent.putExtra("aspectY", 1);
                photoPickerIntent.putExtra("outputX", 280);
                photoPickerIntent.putExtra("outputY", 280);
                photoPickerIntent.putExtra("scaleUpIfNeeded", true);
                photoPickerIntent.putExtra("scale", true);
                startActivityForResult(photoPickerIntent, STATIC_IMAGE_SELECT);
            }
        });

        bt_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _Toast(getApplicationContext().getResources().getString(R.string.info_upload_subir));
                /////Upload video
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("video/*");
                startActivityForResult(photoPickerIntent, STATIC_UPLOAD_VIDEO);
            }
        });

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

    private void CargarStadisticas(String json) {
        try {
            JSONObject j1 = new JSONObject(json);
            Log.d("lolvideos", "nick: "+j1.getString("nick"));
            txt_nick.setText(j1.getString("nick"));
            txt_visitas_unicas.setText(j1.getString("visitas_unicas"));
            txt_total_visitas.setText(j1.getString("visitas_total"));
            txt_total_videos.setText(j1.getString("videos_total"));
            try{
                rating_profile.setRating(Float.parseFloat(j1.getString("nota_profile"))/2);
            }catch (Exception e){

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case STATIC_IMAGE_SELECT:
                if (resultCode == RESULT_OK) {
                    if (imageReturnedIntent!=null) {
                        try{
                            Bundle extras = imageReturnedIntent.getExtras();
                            selectedBitmap = (Bitmap)extras.get("data");
                            img_profile.setImageBitmap(getCircleBitmap(selectedBitmap));
                            SaveImageProfile(selectedBitmap);
                        }catch (Exception e){
                            Uri selectedImageURI = imageReturnedIntent.getData();
                            InputStream input = null;
                            try {
                                input = getApplicationContext().getContentResolver().openInputStream(selectedImageURI);
                                Bitmap d = BitmapFactory.decodeStream(input, null, null);
                                img_profile.setImageBitmap(getCircleBitmap(d));
                                SaveImageProfile(d);
                            } catch (FileNotFoundException w) {
                                w.printStackTrace();
                            }
                        }
                    }
                }
                break;
            case STATIC_UPLOAD_VIDEO:
                    if (resultCode == RESULT_OK) {
                        //diferentes versiones
                        //new UploadVideo().execute(getRealPathFromURI(data.getData()));
                        //File f= getApplicationContext().getFileStreamPath(data.getDataString());
                        try {
                            FileInputStream fis = new FileInputStream(new File(imageReturnedIntent.getDataString()));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        new UploadVideo().execute(getRealFilePath(getApplicationContext(),imageReturnedIntent.getData()));
                        _Toast(getApplicationContext().getResources().getString(R.string.info_upload_finish));
                    } else {
                        _Toast(getApplicationContext().getResources().getString(R.string.info_colabora));
                    }
                break;
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if
                ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {

            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Video.VideoColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {

                    int index = cursor.getColumnIndex( MediaStore.Video.VideoColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private void SaveImageProfile(Bitmap btm){
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOutputStream = null;
        File myDir = new File(path + "/LoLvideos");
        myDir.mkdirs();
        File file = new File(path + "/LoLvideos/", "profile.jpg");
        try {
            fOutputStream = new FileOutputStream(file);

            btm.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);
            fOutputStream.flush();
            fOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ModifierImage().execute(selectedBitmap);
    }

    //cargamos la nueva imagen
    class ModifierImage extends AsyncTask<Bitmap,Void,String> {

        private String line;

        public ModifierImage(){
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            MultipartUtility multipart = null;
            List<String> response = null;
            try {
                multipart = new MultipartUtility(URL_+"andro_modificate_avatar_user.php", "UTF-8");
                //multipart.addHeaderField("User-Agent", "CodeJava");
                //multipart.addHeaderField("Test-Header", "Header-Value");

                multipart.addFormField("id_device", GetUniqueID.getDeviceId(getApplicationContext()));

                String path = Environment.getExternalStorageDirectory().toString();
                File f = new File(path + "/LoLvideos/", "profile.jpg");
                multipart.addFilePart("uploadedfile", f);
                //multipart.addFilePart("fileUpload", uploadFile2);

                response = multipart.finish();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return response.get(0);
        }

        @Override
        protected void onPostExecute(String result) {
            //_Toast(result+"ww");
            try {
                img_profile.setImageBitmap(getCircleBitmap(selectedBitmap));
                img_profile.invalidate();
            }catch (Exception e){}
            finish();

        }

    }


    /**
     * Comprobamos si existe el usuario
     */
    class EstadisticasUser extends AsyncTask<String, Void, String> {

        private String line;

        public EstadisticasUser() {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer chaine = new StringBuffer("");
            try {

                java.net.URL url = new java.net.URL(URL_ + "andro_get_static_profile.php?id_device=" + GetUniqueID.getDeviceId(getApplicationContext()));
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
            Picasso.with(MyVideos_Activity.this).load(URL_ + "profiles/" + GetUniqueID.getDeviceId(MyVideos_Activity.this) + ".jpg").skipMemoryCache().into(img_profile, new Callback() {
                        @Override
                        public void onSuccess() {

                            Bitmap bitmap = ((BitmapDrawable) img_profile.getDrawable()).getBitmap();
                            img_profile.setImageBitmap(getCircleBitmap(bitmap));
                        }

                        @Override
                        public void onError() {
                        }

                    }
            );

           CargarStadisticas(result);
        }
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int radius = Math.min(h / 2 - 4, w / 2 - 4);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        c.drawBitmap(bitmap, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.rgb(255, 153, 0));
        p.setStrokeWidth(5);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        return output;
    }

    /**eliminamos los videos rechazados
     *
     */

    public class DeleteFailVideos extends AsyncTask<String,Void,String> {
        private Activity mContext;
        private String id_device;

        public DeleteFailVideos(){
        }

        private String line;

        @Override
        protected void onPreExecute() {
            id_device = GetUniqueID.getDeviceId(mContext);
        }


        protected void onPostExecute(String result) {
            lst_myvideos.clear();
            new DownloadMyVideos(MyVideos_Activity.this, adapter, progres_my).execute();

        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer chaine = new StringBuffer("");
            java.net.URL url = null;


            try {
                url = new java.net.URL(URL_ +"android_delete_myvideos_fail.php?id_device="+ GetUniqueID.getDeviceId(getApplicationContext()));

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


    ////////////////////////////////////////
    ///////////////upload////////////////////////
    /////////////////////////////
    private class UploadVideo extends AsyncTask<String, Integer, String> {
        ProgressDialog pd;
        long totalSize;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(MyVideos_Activity.this);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage(MyVideos_Activity.this.getString(R.string.info_share_upload_video));
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext httpContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost("http://tywors.com/apps/lolvideos/andro_upload_video.php?id_device="+GetUniqueID.getDeviceId(getApplicationContext()));

            try {
                CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(new CustomMultiPartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });

                // We use FileBody to transfer an image
                multipartContent.addPart("uploadedfile", new FileBody(new File(params[0])));
                totalSize = multipartContent.getContentLength();

                // Send it
                httpPost.setEntity(multipartContent);
                HttpResponse response = httpClient.execute(httpPost, httpContext);
                String serverResponse = EntityUtils.toString(response.getEntity());

                //ResponseFactory rp = new ResponseFactory(serverResponse);
                return "w";
            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pd.setProgress((int) (progress[0]));
        }

        @Override
        protected void onPostExecute(String ui) {
            pd.dismiss();
        }
    }

}
