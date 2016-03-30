package com.tywors.lolvideos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.*;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tywors.lolvideos.activitys.CreateUser_Activity;
import com.tywors.lolvideos.activitys.Ganancias_Activity;
import com.tywors.lolvideos.activitys.MyVideos_Activity;
import com.tywors.lolvideos.adapter.ThumbListAdapter;
import com.tywors.lolvideos.data.GetUniqueID;
import com.tywors.lolvideos.data.UltimoVideoVisto;
import com.tywors.lolvideos.objects.Thumbnail;
import com.tywors.lolvideos.sql.DbHelper;
import com.tywors.lolvideos.sql.GrabarVotacion;
import com.tywors.lolvideos.sql.ThumbLoadMore;
import com.tywors.lolvideos.utils.CustomMultiPartEntity;
import com.tywors.lolvideos.utils.ListViewScroll;


public class Main_Principal extends AppCompatActivity {

    private static final String NAV_ITEM_ID = "10008";
    private static final int MISVIDEOS = 10009;
    private static final int GANANCIAS = 10010;
    private ListView listView;
    private ThumbListAdapter adapter;
    private ImageView bt_show_faborites;
    private ImageView bt_random;
    private ImageView bt_top;
    private ImageView img_profile;
    private ImageView bt_main_upload;
    private ImageView img_flecha_registro;
    //private int faboritos = 0;
    // private int random = 0;
    private int tipo = 0;
    private String URL_ = "http://tywors.com/apps/lolvideos/";
    private boolean user_registrado = false;


    private int contador_publi = 0;

    private InterstitialAd interstitial;
    private String url_general = "http://tywors.com/apps/lolvideos/videos/";
    private String url_app = "http://tywors.com/apps/lolvideos/";
    private static int STATIC_UPLOAD_VIDEO = 10001;
    private static final int REPRODUCIR_VIDEO = 10002;
    private static final int REPRODUCIR_VIDEO_REPLAY = 10003;
    private static final int REGISTRAR_USUARIO = 10004;


    List<Thumbnail> thumbnail;
    private SQLiteDatabase db;
    private DbHelper dbHelper;
    //private String id_device;
    private NavigationView navigationView;
    private final Handler mDrawerActionHandler = new Handler();
    private ActionBarDrawerToggle mDrawerToggle;

    private int mNavItemId;

    /**menu
     *
     */
    DrawerLayout drawerLayout;
    private RelativeLayout lyt_votar;
    ListView listview_menu;
    private String item_menu_select;
    private RatingBar ratingBar;
    private Button bt_votar;
    /**fin menu
     *
     */

    /**
     * tipo
     * <p/>
     * 0: normal
     * 1: faboritos
     * 2: random
     * 3: top_0 - top de siempre
     */

    //notificaciones
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DbHelper(getBaseContext());

        //miramos si queremos ver faboritos o no
        Bundle bundle = getIntent().getExtras();

        try {
            tipo = bundle.getInt("tipo");
        } catch (Exception e) {
        }
        ;
        /** try {
         random = bundle.getInt("random");
         } catch (Exception e) {
         }
         ;*/

        Log.d("LoLVideos", "Arranca la APP");
        //id_device = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        switch (tipo) {
            case 0:
                setContentView(R.layout.activity_main_principal);
                setupNavigationView();
                if (null == savedInstanceState) {
                    mNavItemId = R.id.navigation_item_1;
                } else {
                    mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
                }

                break;
            case 1:
                setContentView(R.layout.activity_main_principal_fav);
                break;
            case 2:
                setContentView(R.layout.activity_main_principal);
                setupNavigationView();
                if (null == savedInstanceState) {
                    mNavItemId = R.id.navigation_item_1;
                } else {
                    mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
                }
                break;
            case 3:
                setContentView(R.layout.activity_main_principal_top);
                break;
            case 4:
                //si viene del intent-upload_filter
                setContentView(R.layout.activity_main_principal);
                setupNavigationView();
                if (null == savedInstanceState) {
                    mNavItemId = R.id.navigation_item_1;
                } else {
                    mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
                }
                break;
        }

        listView = (ListView) findViewById(R.id.listview_main);
        bt_show_faborites = (ImageView) findViewById(R.id.bt_show_faborites);
        bt_random = (ImageView) findViewById(R.id.bt_random);
        bt_top = (ImageView) findViewById(R.id.bt_top);
        img_profile = (ImageView) findViewById(R.id.img_profile_main);
        bt_main_upload = (ImageView)findViewById(R.id.bt_main_uploadvide);
        lyt_votar = (RelativeLayout)findViewById(R.id.lyt_votar_video);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar_votos);
        bt_votar = (Button)findViewById(R.id.bt_votar_main);

        switch (tipo) {
            case 0:
                MainListeners();
                bt_random.setImageResource(R.drawable.icon_random_1);
                break;
            case 1:
                Faborite_Listeners();
                break;
            case 2:
                MainListeners();
                bt_random.setImageResource(R.drawable.icon_random_2);
                break;
            case 3:
                Top_Listeners();
                break;
            case 4:
                //viene del intent_filter
                MainListeners();
                bt_random.setImageResource(R.drawable.icon_random_1);
                _Toast(getApplicationContext().getResources().getString(R.string.info_upload_finish));
                //estado normal
                tipo=0;
                break;
        }


        thumbnail = new ArrayList<Thumbnail>();
        listView.setFriction(10);


        final ProgressBar progressBar = new ProgressBar(this, null,
                android.R.attr.progressBarStyle);
        LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(progressBarParams);
        progressBar.setPadding(6, 6, 6, 6);
        progressBar.setVisibility(View.INVISIBLE);

        LinearLayout footerLinearLayout = new LinearLayout(this);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        footerLinearLayout.setGravity(Gravity.CENTER);
        footerLinearLayout.setLayoutParams(layoutParams);
        footerLinearLayout.addView(progressBar);


        listView.setOnScrollListener(new ListViewScroll() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //new ThumbLoadMore(progressBar, Main_Principal.this, adapter).execute(page, faboritos, random);
                new ThumbLoadMore(progressBar, Main_Principal.this, adapter).execute(page, tipo);
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    lyt_votar.setVisibility(View.GONE);
                }catch (Exception e){}
                return false;
            }
        });


        listView.addFooterView(footerLinearLayout);

        //adapter = new ThumbListAdapter(this, thumbnail, faboritos);
        adapter = new ThumbListAdapter(this, thumbnail, tipo);
        listView.setAdapter(adapter);

        //new ThumbLoadMore(progressBar, this, adapter).execute(0, faboritos, random);
        new ThumbLoadMore(progressBar, this, adapter).execute(0, tipo);


        // Crear el intersticial.
        interstitial = new InterstitialAd(Main_Principal.this);
        interstitial.setAdUnitId("ca-app-pub-1070959751820382/9361959108");

        // Crear la solicitud de anuncio.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Comenzar la carga del intersticial.
        interstitial.loadAd(adRequest);
    }

    private void MainListeners() {
        Random_Listeners();
        Faborite_Listeners();
        Top_Listeners();

        img_profile.invalidate();

        //imagen de perfil
        new ExistUser().execute();
        String uni = GetUniqueID.getDeviceId(Main_Principal.this);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_profile);
        img_profile.setImageBitmap(getCircleBitmap(largeIcon));
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_registrado) {
                    //Le mostramos el menu
                    drawerLayout.openDrawer(GravityCompat.START);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    //cargamos su imagen
                    //Picasso.with(Main_Principal.this).load(url_general + "profiles/" + GetUniqueID.getDeviceId(Main_Principal.this) + ".jpg").into(img_profile);
                    //_Toast(url_general+"profiles/"+GetUniqueID.getDeviceId(getApplicationContext())+".jpg");
                } else {

                    //lo llevamos al panel de registro
                    Intent go_registro = new Intent(Main_Principal.this, CreateUser_Activity.class);
                    //startActivity(go_registro);
                    startActivityForResult(go_registro, REGISTRAR_USUARIO);
                }
            }
        });

        bt_main_upload.setOnClickListener(new View.OnClickListener() {
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

    private void Random_Listeners() {
        bt_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tipo != 2) {
                    Intent i = new Intent(Main_Principal.this, Main_Principal.class);
                    i.putExtra("tipo", 2);
                    Toast toast3 = Toast.makeText(getApplicationContext().getApplicationContext(), R.string.info_random_conectado, Toast.LENGTH_LONG);
                    View view3 = toast3.getView();
                    view3.setBackgroundResource(R.drawable.style_toast);
                    TextView text3 = (TextView) view3.findViewById(android.R.id.message);
                    text3.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getApplicationContext().getApplicationContext().getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getApplicationContext().getApplicationContext().getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getApplicationContext().getApplicationContext().getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getApplicationContext().getApplicationContext().getResources().getDisplayMetrics()));
                    text3.setTypeface(Typeface.DEFAULT_BOLD);
                    text3.setTextColor(Color.WHITE);
                    toast3.show();
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(Main_Principal.this, Main_Principal.class);
                    i.putExtra("tipo", 0);
                    Toast toast3 = Toast.makeText(getApplicationContext().getApplicationContext(), R.string.info_random_desconectado, Toast.LENGTH_LONG);
                    View view3 = toast3.getView();
                    view3.setBackgroundResource(R.drawable.style_toast);
                    TextView text3 = (TextView) view3.findViewById(android.R.id.message);
                    text3.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getApplicationContext().getApplicationContext().getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getApplicationContext().getApplicationContext().getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getApplicationContext().getApplicationContext().getResources().getDisplayMetrics()),
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getApplicationContext().getApplicationContext().getResources().getDisplayMetrics()));
                    text3.setTypeface(Typeface.DEFAULT_BOLD);
                    text3.setTextColor(Color.WHITE);
                    toast3.show();
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void Faborite_Listeners() {
        bt_show_faborites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tipo != 1) {
                    Intent i = new Intent(Main_Principal.this, Main_Principal.class);
                    i.putExtra("tipo", 1);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(Main_Principal.this, Main_Principal.class);
                    i.putExtra("tipo", 0);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void Top_Listeners() {
        bt_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tipo != 3) {
                    Intent i = new Intent(Main_Principal.this, Main_Principal.class);
                    i.putExtra("tipo", 3);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(Main_Principal.this, Main_Principal.class);
                    i.putExtra("tipo", 0);
                    startActivity(i);
                    finish();
                }
            }
        });
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

    ///////////////////////////////////
    //////////////////////////////////
    ////onActivityResult//////////////////
    //////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // Check which request we're responding to
        if (requestCode == STATIC_UPLOAD_VIDEO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //diferentes versiones
                //new UploadVideo().execute(getRealPathFromURI(data.getData()));
                //File f= getApplicationContext().getFileStreamPath(data.getDataString());
                try {
                    FileInputStream fis = new FileInputStream(new File(data.getDataString()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                new UploadVideo().execute(getRealFilePath(getApplicationContext(),data.getData()));
                _Toast(getApplicationContext().getResources().getString(R.string.info_upload_finish));
            } else {
                _Toast(getApplicationContext().getResources().getString(R.string.info_colabora));

            }
        }
        if (requestCode == REPRODUCIR_VIDEO && tipo != 1) {
            db = dbHelper.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT id_video FROM video_votado WHERE id_video=" + UltimoVideoVisto.id_video_ultimo, null);
            try {
                if (!(c.getCount() > 0)) {
                    //mostramos
                    lyt_votar.setVisibility(View.VISIBLE);
                    //al pulsar el boton de votar
                    bt_votar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ratingBar.getRating() != 0) {
                                new GrabarVotacion(Main_Principal.this).execute(UltimoVideoVisto.id_video_ultimo, ratingBar.getRating() * 2 + "");
                                //insertamos la votacion en sqlitev
                                ContentValues values = new ContentValues();
                                values.put("id_video", UltimoVideoVisto.id_video_ultimo);
                                db.insert("video_votado", null, values);
                                ratingBar.setRating(0);
                                lyt_votar.setVisibility(View.GONE);
                            } else {
                                _Toast(getApplicationContext().getResources().getString(R.string.fail_votar));
                            }

                        }
                    });
                } else {
                    lyt_votar.setVisibility(View.GONE);
                }
            }catch (Exception e){}

        }
        //al volver del registro miramos si se ha registrado
        if(requestCode == REGISTRAR_USUARIO){
            new ExistUser().execute();
        }
        //Al volver de mis_videos
        if(requestCode == MISVIDEOS){
            try {
                String path = Environment.getExternalStorageDirectory().toString();
                File f = new File(path + "/LoLvideos/", "profile.jpg");
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                img_profile.setImageBitmap(getCircleBitmap(bitmap));
                img_profile.invalidate();
            }catch (Exception e){};

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



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (tipo == 0) {
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(getApplicationContext().getString(R.string.info_exit))
                        .setMessage(getApplicationContext().getString(R.string.info_exit_2))
                        .setNegativeButton(android.R.string.cancel, null)//sin listener
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Salir
                                Main_Principal.this.finish();
                            }
                        })
                        .show();

                // Si el listener devuelve true, significa que el evento esta procesado, y nadie debe hacer nada mas
                return true;
            } else {
                Intent i = new Intent(Main_Principal.this, Main_Principal.class);
                i.putExtra("tipo", 0);
                startActivity(i);
                finish();
            }
        }
//para las demas cosas, se reenvia el evento al listener habitual
        return super.onKeyDown(keyCode, event);
    }


    ////////////////////////////////////////
    ///////////////upload////////////////////////
    /////////////////////////////
    private class UploadVideo extends AsyncTask<String, Integer, String> {
        ProgressDialog pd;
        long totalSize;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(Main_Principal.this);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setMessage(Main_Principal.this.getString(R.string.info_share_upload_video));
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


    @Override
    protected void onDestroy() {

        super.onDestroy();

        try {
            trimCache(this);
            // Toast.makeText(this,"onDestroy " ,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    ////////cachee
    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }



    /**
     * Comprobamos si existe el usuario
     */
    class ExistUser extends AsyncTask<String,Void,String> {

       private String line;

        public ExistUser(){
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer chaine = new StringBuffer("");
            try{

                java.net.URL url = new java.net.URL(URL_+"andro_try_exist_user.php?id_device=" + GetUniqueID.getDeviceId(getApplicationContext()));
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

        @Override
        protected void onPostExecute(String result) {
            // _Toast(result);
            if (result.equals("0")) {
                // _Toast("no existe");
                img_flecha_registro = (ImageView)findViewById(R.id.img_flecha_registro);
                img_flecha_registro.setVisibility(View.VISIBLE);
                user_registrado = false;
            } else if (result.equals("1")) {
                // _Toast("existe");
                user_registrado = true;


                Picasso.with(Main_Principal.this).load(url_app + "profiles/" + GetUniqueID.getDeviceId(Main_Principal.this) + ".jpg").skipMemoryCache().into(img_profile, new Callback() {
                            @Override
                            public void onSuccess() {

                                Bitmap bitmap = ((BitmapDrawable)img_profile.getDrawable()).getBitmap();
                                img_profile.setImageBitmap(getCircleBitmap(bitmap));
                            }

                            @Override
                            public void onError() {
                            }

                        }
                );
            }
        }

    }



    /**menu
     *
     */
    private void setupNavigationView(){

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                menuItem.setChecked(true);
                mNavItemId = menuItem.getItemId();
                mDrawerActionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navigate(menuItem);
                    }
                }, 200);
                return false;
            }
        });
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


    }
    private void navigate(final MenuItem item) {
        item_menu_select = item.getTitle().toString();
        item.setChecked(false);
        drawerLayout.closeDrawer(GravityCompat.START);
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //GO TO
               String nav_1 = getApplicationContext().getResources().getString(R.string.nav_1);
               if(nav_1.equals(item_menu_select)){
                   Intent go_to = new Intent(Main_Principal.this, MyVideos_Activity.class);
                   startActivityForResult(go_to,MISVIDEOS);
                   item_menu_select = "";
               }

                //ganancias
                String nav_2 = getApplicationContext().getResources().getString(R.string.nav_2);
                if(nav_2.equals(item_menu_select)){
                    Intent go_to = new Intent(Main_Principal.this, Ganancias_Activity.class);
                    startActivityForResult(go_to,MISVIDEOS);
                    item_menu_select = "";
                }




               String nav_a = getApplicationContext().getResources().getString(R.string.rateme);
                if(nav_a.equals(item_menu_select)){
                    _Toast(getApplicationContext().getResources().getString(R.string.info_gracias));
                    Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                    }
                    item_menu_select = "";
                }


                String nav_a2 = getApplicationContext().getResources().getString(R.string.share_app);
                if(nav_a2.equals(item_menu_select)){
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, getApplicationContext().getString(R.string.info_share_app));
                    startActivity(whatsappIntent);
                    item_menu_select = "";
                }



                //finish();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

/**
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }
}*/

}




