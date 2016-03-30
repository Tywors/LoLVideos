package com.tywors.lolvideos.adapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;
import com.tywors.lolvideos.R;
import com.tywors.lolvideos.data.UltimoVideoVisto;
import com.tywors.lolvideos.objects.Thumbnail;
import com.tywors.lolvideos.sql.GrabarFavorito;
import com.tywors.lolvideos.sql.GrabarVisita;
import com.vinayrraj.flipdigit.lib.Flipmeter;

/**
 * Created by W7 on 18/01/2015.
 */
public class ThumbListAdapter  extends BaseAdapter {


    private static final int REPRODUCIR_VIDEO = 10002;
    private Activity activity;
    private List<Thumbnail> thumbnails;
    private static LayoutInflater inflater=null;
    private ViewHolder viewInfoAbierta;
    private int tipo;
    private String url_general = "http://tywors.com/apps/lolvideos/videos/";

    private final int flip_value = 000000;
    private static int STATIC_UPLOAD_VIDEO = 10001;
    private InterstitialAd interstitial;
    private int contador_publi = 0;


    //private Tinthumbnail thumb;



    public ThumbListAdapter(Activity a, List<Thumbnail> movies, int tipo) {
        activity = a;
        this.tipo = tipo;
        this.thumbnails = movies;
        inflater =
                (LayoutInflater)activity.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return thumbnails.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        //variables de cada video info

        Thumbnail thumb = new Thumbnail();
        thumb = thumbnails.get(position);


        //final String es_icono = thumb.getRuta_video_1();


        if(convertView==null){
            vi = inflater.inflate(R.layout.listview_row,null);
            holder = new ViewHolder();

            holder.thumbnail_1 = (ImageView)vi.findViewById(R.id.thumb_1);
            holder.thumbnail_2 = (ImageView)vi.findViewById(R.id.thumb_2);
            holder.bt_add_favorite_1 = (ImageView)vi.findViewById(R.id.bt_add_favorite_1);
            holder.bt_add_favorite_2 = (ImageView)vi.findViewById(R.id.bt_add_favorite_2);
            holder.bt_play_1 = (ImageView)vi.findViewById(R.id.bt_play_1);
            holder.bt_play_2 = (ImageView)vi.findViewById(R.id.bt_play_2);
            holder.bt_share_1 = (ImageView)vi.findViewById(R.id.bt_share_1);
            holder.bt_share_2 = (ImageView)vi.findViewById(R.id.bt_share_2);

            holder.txt_nota_1 = (TextView)vi.findViewById(R.id.nota_1);
            holder.txt_nota_2 = (TextView)vi.findViewById(R.id.nota_2);

            holder.flip_contador = (Flipmeter)vi.findViewById(R.id.flip_contador);
            holder.flip_contador.setValue(00000,false);
            holder.bt_share_app = (ImageView)vi.findViewById(R.id.bt_share_app);

            holder.lyt_list_info = (RelativeLayout)vi.findViewById(R.id.lyt_list_info);
            holder.lyt_list_info.setVisibility(View.GONE);

            holder.lyt_list_info_1 = (RelativeLayout)vi.findViewById(R.id.lyt_list_info_1);
            holder.lyt_list_info_2 = (RelativeLayout)vi.findViewById(R.id.lyt_list_info_2);
            holder.lyt_list_icono = (RelativeLayout)vi.findViewById(R.id.lyt_list_icono);


            holder.txt_visita_1 = (TextView)vi.findViewById(R.id.txt_info_visitas_1);
            holder.txt_visita_2 = (TextView)vi.findViewById(R.id.txt_info_visitas_2);

            if(tipo!=1) {
                holder.bt_add_favorite_1.setImageResource(R.drawable.estrella);
                holder.bt_add_favorite_2.setImageResource(R.drawable.estrella);
                holder.txt_nota_1.setVisibility(View.VISIBLE);
                holder.txt_nota_2.setVisibility(View.VISIBLE);
            }else{
                holder.bt_add_favorite_1.setImageResource(R.drawable.estrella2);
                holder.bt_add_favorite_2.setImageResource(R.drawable.estrella2);
                holder.txt_nota_1.setVisibility(View.INVISIBLE);
                holder.txt_nota_2.setVisibility(View.INVISIBLE);
            }

            //holder.txt_borrar_1 = (TextView)vi.findViewById(R.id.txt_borrar_1);



//
                holder.thumbnail_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    //Miramos que no sea el icono
                    if(!holder.es_icono_1.equals("--") & !holder.es_icono_1.equals("||")){
                        if (viewInfoAbierta != null) {
                            RelativeLayout.LayoutParams mparam = new RelativeLayout.LayoutParams((int) (RelativeLayout.LayoutParams.WRAP_CONTENT), (int) (0));
                            mparam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()),
                                    0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()), 0);

                            mparam.addRule(RelativeLayout.BELOW, R.id.lyt_uno);

                            viewInfoAbierta.lyt_list_info.setLayoutParams(mparam);

                            viewInfoAbierta.lyt_list_info.setVisibility(View.GONE);
                            viewInfoAbierta.lyt_list_info_2.setVisibility(View.GONE);
                            viewInfoAbierta.lyt_list_icono.setVisibility(View.GONE);
                            viewInfoAbierta.lyt_list_info_1.setVisibility(View.GONE);
                            viewInfoAbierta = null;



                        }




                            Animacion_list_info anim = new Animacion_list_info(holder.lyt_list_info, RelativeLayout.LayoutParams.MATCH_PARENT, 0,
                                    RelativeLayout.LayoutParams.MATCH_PARENT, 150);

                            viewInfoAbierta = holder;
                            RelativeLayout.LayoutParams mparam = new RelativeLayout.LayoutParams((int) (RelativeLayout.LayoutParams.WRAP_CONTENT), (int) (0));
                            mparam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()),
                                    0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()), 0);

                            mparam.addRule(RelativeLayout.BELOW, R.id.lyt_uno);

                            holder.lyt_list_info.setLayoutParams(mparam);
                            holder.lyt_list_info.setAnimation(anim);
                            holder.lyt_list_info.setVisibility(View.VISIBLE);

                            holder.lyt_list_info_2.setVisibility(View.GONE);
                            holder.lyt_list_icono.setVisibility(View.GONE);
                            holder.lyt_list_info_1.setVisibility(View.VISIBLE);

                       // Toast.makeText(activity,thumb.getFavorito_1()+"",Toast.LENGTH_LONG).show();

                        //si es icono
                    }else if(holder.es_icono_1.equals("--") ){

                        if (viewInfoAbierta != null) {
                            RelativeLayout.LayoutParams mparam = new RelativeLayout.LayoutParams((int) (RelativeLayout.LayoutParams.WRAP_CONTENT), (int) (0));
                            mparam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()),
                                    0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()), 0);

                            mparam.addRule(RelativeLayout.BELOW, R.id.lyt_uno);

                            viewInfoAbierta.lyt_list_info.setLayoutParams(mparam);

                            viewInfoAbierta.lyt_list_info.setVisibility(View.GONE);
                            viewInfoAbierta.lyt_list_info_2.setVisibility(View.GONE);
                            viewInfoAbierta.lyt_list_info_1.setVisibility(View.GONE);
                            viewInfoAbierta.lyt_list_icono.setVisibility(View.GONE);

                            viewInfoAbierta = null;
                        }


                        holder.flip_contador.setValue(Integer.parseInt(holder.cont_videos),true);


                        Animacion_list_info anim = new Animacion_list_info(holder.lyt_list_info, RelativeLayout.LayoutParams.MATCH_PARENT, 0,
                                RelativeLayout.LayoutParams.MATCH_PARENT, 150);

                        viewInfoAbierta = holder;
                        RelativeLayout.LayoutParams mparam = new RelativeLayout.LayoutParams((int) (RelativeLayout.LayoutParams.WRAP_CONTENT), (int) (0));
                        mparam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()),
                                0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()), 0);

                        mparam.addRule(RelativeLayout.BELOW, R.id.lyt_uno);

                        holder.lyt_list_info.setLayoutParams(mparam);
                        holder.lyt_list_info.setAnimation(anim);
                        holder.lyt_list_info.setVisibility(View.VISIBLE);

                        holder.lyt_list_info_2.setVisibility(View.GONE);
                        holder.lyt_list_icono.setVisibility(View.VISIBLE);
                        holder.lyt_list_info_1.setVisibility(View.GONE);


                    }else if(holder.es_icono_1.equals("||")){
                        if (viewInfoAbierta != null) {
                            RelativeLayout.LayoutParams mparam = new RelativeLayout.LayoutParams((int) (RelativeLayout.LayoutParams.WRAP_CONTENT), (int) (0));
                            mparam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()),
                                    0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()), 0);

                            mparam.addRule(RelativeLayout.BELOW, R.id.lyt_uno);

                            viewInfoAbierta.lyt_list_info.setLayoutParams(mparam);

                            viewInfoAbierta.lyt_list_info.setVisibility(View.GONE);
                            viewInfoAbierta.lyt_list_info_2.setVisibility(View.GONE);
                            viewInfoAbierta.lyt_list_info_1.setVisibility(View.GONE);
                            viewInfoAbierta.lyt_list_icono.setVisibility(View.GONE);

                            viewInfoAbierta = null;
                        }
                        _Toast(activity.getResources().getString(R.string.info_upload_subir));
                        /////Upload video
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("video/*");
                        activity.startActivityForResult(photoPickerIntent, STATIC_UPLOAD_VIDEO);

                    }

                        //////////////////////////publi//////////////////////////////
                        ////////////////////////////////////////////////////////////
                        switch (contador_publi){
                            case 1:
                                // Crear el intersticial.
                                interstitial = new InterstitialAd(activity);
                                interstitial.setAdUnitId("ca-app-pub-1070959751820382/9361959108");
                                // Crear la solicitud de anuncio.
                                AdRequest adRequest = new AdRequest.Builder().build();
                                // Comenzar la carga del intersticial.
                                interstitial.loadAd(adRequest);

                                contador_publi++;
                                break;

                            case 3:
                                displayInterstitial();
                                contador_publi = 0;
                                break;

                            default:
                                contador_publi++;
                                break;
                        }


                    }
                });



                holder.thumbnail_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!holder.es_icono_2.equals("||") & !holder.es_icono_2.equals("-|")){
                            if (viewInfoAbierta != null) {

                                RelativeLayout.LayoutParams mparam = new RelativeLayout.LayoutParams((int) (RelativeLayout.LayoutParams.WRAP_CONTENT), (int) (0));

                                mparam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()),
                                        0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()), 0);

                                mparam.addRule(RelativeLayout.BELOW, R.id.lyt_uno);

                                viewInfoAbierta.lyt_list_info.setLayoutParams(mparam);

                                viewInfoAbierta.lyt_list_info.setVisibility(View.GONE);
                                viewInfoAbierta.lyt_list_info_2.setVisibility(View.GONE);
                                viewInfoAbierta.lyt_list_info_1.setVisibility(View.GONE);
                                viewInfoAbierta.lyt_list_icono.setVisibility(View.GONE);

                                viewInfoAbierta = null;

                            }

                            Animacion_list_info anim = new Animacion_list_info(holder.lyt_list_info, RelativeLayout.LayoutParams.MATCH_PARENT, 0,
                                    RelativeLayout.LayoutParams.MATCH_PARENT, 150);

                            viewInfoAbierta = holder;
                            RelativeLayout.LayoutParams mparam = new RelativeLayout.LayoutParams((int) (RelativeLayout.LayoutParams.WRAP_CONTENT), (int) (0));
                            mparam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()),
                                    0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()), 0);

                            mparam.addRule(RelativeLayout.BELOW, R.id.lyt_uno);

                            holder.lyt_list_info.setLayoutParams(mparam);
                            holder.lyt_list_info.setAnimation(anim);
                            holder.lyt_list_info.setVisibility(View.VISIBLE);
                            holder.lyt_list_icono.setVisibility(View.GONE);

                            holder.lyt_list_info_2.setVisibility(View.VISIBLE);
                            holder.lyt_list_info_1.setVisibility(View.GONE);



                        }else if(holder.es_icono_2.equals("-|")) {
                            if (viewInfoAbierta != null) {
                                RelativeLayout.LayoutParams mparam = new RelativeLayout.LayoutParams((int) (RelativeLayout.LayoutParams.WRAP_CONTENT), (int) (0));

                                mparam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()),
                                        0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()), 0);

                                mparam.addRule(RelativeLayout.BELOW, R.id.lyt_uno);

                                viewInfoAbierta.lyt_list_info.setLayoutParams(mparam);

                                viewInfoAbierta.lyt_list_info.setVisibility(View.GONE);
                                viewInfoAbierta.lyt_list_info_2.setVisibility(View.GONE);
                                viewInfoAbierta.lyt_list_info_1.setVisibility(View.GONE);
                                viewInfoAbierta.lyt_list_icono.setVisibility(View.GONE);

                                viewInfoAbierta = null;
                            }
                            _Toast(activity.getResources().getString(R.string.info_gracias));
                            Uri uri = Uri.parse("market://details?id=" + activity.getApplicationContext().getPackageName());
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            try {
                                activity.startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getApplicationContext().getPackageName())));
                            }

                        }else{
                            if (viewInfoAbierta != null) {
                                RelativeLayout.LayoutParams mparam = new RelativeLayout.LayoutParams((int) (RelativeLayout.LayoutParams.WRAP_CONTENT), (int) (0));

                                mparam.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()),
                                        0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()), 0);

                                mparam.addRule(RelativeLayout.BELOW, R.id.lyt_uno);

                                viewInfoAbierta.lyt_list_info.setLayoutParams(mparam);

                                viewInfoAbierta.lyt_list_info.setVisibility(View.GONE);
                                viewInfoAbierta.lyt_list_info_2.setVisibility(View.GONE);
                                viewInfoAbierta.lyt_list_info_1.setVisibility(View.GONE);
                                viewInfoAbierta.lyt_list_icono.setVisibility(View.GONE);
                                holder.flip_contador.setValue(00000,false);
                                viewInfoAbierta = null;
                            }
                            /////Upload video
                            _Toast(activity.getResources().getString(R.string.info_upload_subir));
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("video/*");
                            activity.startActivityForResult(photoPickerIntent, STATIC_UPLOAD_VIDEO);
                        }

                        //////////////////////////publi//////////////////////////////
                        ////////////////////////////////////////////////////////////
                        switch (contador_publi){
                            case 1:
                                // Crear el intersticial.
                                interstitial = new InterstitialAd(activity);
                                interstitial.setAdUnitId("ca-app-pub-1070959751820382/9361959108");
                                // Crear la solicitud de anuncio.
                                AdRequest adRequest = new AdRequest.Builder().build();
                                // Comenzar la carga del intersticial.
                                interstitial.loadAd(adRequest);

                                contador_publi++;
                                break;

                            case 3:
                                displayInterstitial();
                                contador_publi = 0;
                                break;

                            default:
                                contador_publi++;
                                break;
                        }
                }});

            //botonoes faborito
            holder.bt_add_favorite_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GrabarFavorito(activity).execute(holder.favorite_1,tipo+"");
                    if(tipo!=1) {
                        holder.bt_add_favorite_1.setImageResource(R.drawable.estrella2);
                    }else{
                        holder.bt_add_favorite_1.setImageResource(R.drawable.estrella);
                    }


                }
            });


            holder.bt_add_favorite_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GrabarFavorito(activity).execute(holder.favorite_2,tipo+"");
                    if(tipo!=1) {
                        holder.bt_add_favorite_2.setImageResource(R.drawable.estrella2);
                    }else{
                        holder.bt_add_favorite_2.setImageResource(R.drawable.estrella);
                    }
                }
            });

            holder.bt_share_app.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.info_share_app));
                    activity.startActivity(whatsappIntent);

                }
            });

            holder.bt_play_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GrabarVisita(activity).execute(holder.url_1);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url_general + holder.url_1), "video/mp4");
                    UltimoVideoVisto.id_video_ultimo = holder.favorite_1;
                    UltimoVideoVisto.url_ultimo_video = holder.url_1;

                    activity.startActivityForResult(intent, REPRODUCIR_VIDEO);
                }
            });

            holder.bt_play_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GrabarVisita(activity).execute(holder.url_2);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url_general + holder.url_2), "video/mp4");
                    UltimoVideoVisto.id_video_ultimo = holder.favorite_2;
                    UltimoVideoVisto.url_ultimo_video = holder.url_2;

                    activity.startActivityForResult(intent, REPRODUCIR_VIDEO);
                }
            });

            holder.bt_share_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DownloadVideo downloadTask = new DownloadVideo(activity);
                    downloadTask.execute(url_general+holder.url_1,holder.url_1);
                }

            });

            holder.bt_share_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DownloadVideo downloadTask = new DownloadVideo(activity);
                    downloadTask.execute(url_general+holder.url_2,holder.url_2);
                }

            });




            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
            RelativeLayout.LayoutParams mparam = new RelativeLayout.LayoutParams((int)(RelativeLayout.LayoutParams.WRAP_CONTENT),(int) (0));
            mparam.setMargins(10,0,10,0);
            mparam.addRule(RelativeLayout.BELOW, R.id.lyt_uno);

            if(tipo!=1) {
                holder.bt_add_favorite_1.setImageResource(R.drawable.estrella);
                holder.bt_add_favorite_2.setImageResource(R.drawable.estrella);
            }else{
                holder.bt_add_favorite_1.setImageResource(R.drawable.estrella2);
                holder.bt_add_favorite_2.setImageResource(R.drawable.estrella2);
            }

            holder.lyt_list_info.setLayoutParams(mparam);
            holder.lyt_list_info.setVisibility(View.GONE);
            holder.lyt_list_info_2.setVisibility(View.GONE);
            holder.lyt_list_info_1.setVisibility(View.GONE);
            holder.lyt_list_icono.setVisibility(View.GONE);
            /**if(holder.nota_1.equals("--")){
                holder.txt_nota_1.setVisibility(View.INVISIBLE);
            }else{
                holder.txt_nota_1.setVisibility(View.VISIBLE);
            }*/
        }




        holder.es_icono_1 = thumb.getRuta_video_1();
        holder.es_icono_2 = thumb.getRuta_video_2();

        Picasso.with(activity).load(thumb.getImage_url_1()).into(holder.thumbnail_1);
        Picasso.with(activity).load(thumb.getImage_url_2()).into(holder.thumbnail_2);

        holder.id_1 = thumb.getId_1();
        holder.id_2 = thumb.getId_2();


        holder.favorite_1 = thumb.getId_1();
        holder.favorite_2 = thumb.getId_2();
        holder.visita_1 = thumb.getVisita_1();
        holder.visita_2 = thumb.getVisita_2();
        holder.url_1 = thumb.getRuta_video_1();
        holder.url_2 = thumb.getRuta_video_2();
        holder.nota_1 = thumb.getNota_1();
        holder.nota_2 = thumb.getNota_2();
        holder.cont_videos = thumb.getCont_videos_1();
        holder.txt_visita_1.setText(holder.visita_1);
        holder.txt_visita_2.setText(holder.visita_2);


            if (holder.nota_1.equals("null")) {
                holder.txt_nota_1.setText("?");
            } else {
                holder.txt_nota_1.setText(holder.nota_1);
            }

            if (holder.nota_2.equals("null")) {
                holder.txt_nota_2.setText("?");
            } else {
                holder.txt_nota_2.setText(holder.nota_2);
            }

       // if(faboritos==0) {

            if (!holder.es_icono_1.equals("--") & !holder.es_icono_1.equals("||") & !holder.es_icono_1.equals("-|") & !holder.es_icono_1.equals("null")) {
                holder.txt_nota_1.setVisibility(View.VISIBLE);
            } else {
                holder.txt_nota_1.setVisibility(View.INVISIBLE);
            }

            if (!holder.es_icono_2.equals("--") & !holder.es_icono_2.equals("||") & !holder.es_icono_2.equals("-|") & !holder.es_icono_2.equals("null")) {
                holder.txt_nota_2.setVisibility(View.VISIBLE);
            } else {
                holder.txt_nota_2.setVisibility(View.INVISIBLE);
            }
       // }else{
       //     holder.txt_nota_1.setVisibility(View.INVISIBLE);
      //      holder.txt_nota_2.setVisibility(View.INVISIBLE);
      //  }

        //holder.txt_nota_1.setText(holder.);

        /**if(holder.nota_1.equals("null")){
            holder.txt_nota_1.setText("?");
        }else

        if(holder.nota_1.equals("--")){
            holder.txt_nota_1.setVisibility(View.GONE);
        }
        else
        if(holder.nota_1.equals("||")){
            holder.txt_nota_1.setVisibility(View.GONE);
        }else
        if(holder.nota_1.equals("-|")){
            holder.txt_nota_1.setVisibility(View.GONE);
        }else{
            holder.txt_nota_1.setText(holder.nota_1);
        }


        if(holder.nota_2.equals("null")){
            holder.txt_nota_2.setText("?");
        }else

        if(holder.nota_2.equals("||")){
            holder.txt_nota_2.setVisibility(View.GONE);
        }else
        if(holder.nota_2.equals("-|")){
            holder.txt_nota_2.setVisibility(View.GONE);
        }else{
            holder.txt_nota_2.setText(holder.nota_2);
        }*/

        holder.flip_contador.setValue(00000,false);


        //vi.setTag(holder);


        return vi;
    }


    public void setData(List<Thumbnail> thumbnails){
        this.thumbnails.addAll(thumbnails);
        this.notifyDataSetChanged();
    }

    public class ViewHolder{
        ImageView thumbnail_1;
        ImageView thumbnail_2;
        RelativeLayout lyt_list_info;
        RelativeLayout lyt_list_info_1;
        RelativeLayout lyt_list_info_2;
        RelativeLayout lyt_list_icono;
        ImageView bt_add_favorite_1;
        ImageView bt_add_favorite_2;
        ImageView bt_play_1;
        ImageView bt_play_2;
        TextView txt_visita_1;
        TextView txt_visita_2;
        ImageView bt_share_1;
        ImageView bt_share_2;
        String cont_videos;
        TextView txt_nota_1;
        TextView txt_nota_2;

        Flipmeter flip_contador;
        ImageView bt_share_app;





       // Integer Resource_estrella;

        String id_1;
        String id_2;
        String es_icono_1;
        String es_icono_2;
        String favorite_1;
        String favorite_2;
        String visita_1;
        String visita_2;
        String url_1;
        String url_2;
        String nota_1;
        String nota_2;

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }





    public void _Toast(String sms){
        Toast toast2 = Toast.makeText(activity, sms, Toast.LENGTH_LONG);
        View view2 = toast2.getView();
        view2.setBackgroundResource(R.drawable.style_toast);
        TextView text2 = (TextView) view2.findViewById(android.R.id.message);
        text2.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, activity.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, activity.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, activity.getResources().getDisplayMetrics()));
        text2.setTypeface(Typeface.DEFAULT_BOLD);
        text2.setTextColor(Color.WHITE);
        toast2.show();
    }


    public void displayInterstitial() {
        Log.d("LoLVideos", "ADS---> Hacemos la llamada");
        if (interstitial.isLoaded()) {
            interstitial.show();
            Log.d("LoLVideos","ADS---> Mostramos el ADS");
        }
    }





}



//Animacion al expandir INFO
class Animacion_list_info extends Animation {
    private View mView;
    private float mToHeight;
    private float mFromHeight;

    private float mToWidth;
    private float mFromWidth;

    public Animacion_list_info(View v, float fromWidth, float fromHeight, float toWidth, float toHeight) {
        mToHeight = toHeight;
        mToWidth = toWidth;
        mFromHeight = fromHeight;
        mFromWidth = fromWidth;
        mView = v;
        setDuration(300);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height =
                (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
        float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mView.getLayoutParams();
        p.height = (int) height;
        p.width = (int) width;
        mView.requestLayout();
    }





}



//////////////////////////////
//////////////////////////////
////////////////////////////////
/////////////////////////////////
//////DESCARGAR VIDEO SHARE//////
////////////////////////////////
////////////////////////////////
/////////////////////////////


class DownloadVideo extends AsyncTask<String, Integer, String> {
    Context context;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog mProgressDialog;
    private String root;

    public DownloadVideo(Context context) {
        this.context = context;
    }


    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/LoLvideos/" + sUrl[1]);
            myDir.getParentFile().mkdirs();
            output = new FileOutputStream(myDir);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return "--";
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return "--";
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
                return "--";
            }

            if (connection != null)
                connection.disconnect();
        }
        return root + "/LoLvideos/" + sUrl[1];
        //return root + "/Twitter/" + "IMG_20140929_130920.jpeg";
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.info_share_download_video));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result.equals("--")) {

        }else {
            Toast toast_a = Toast.makeText(context, context.getResources().getString(R.string.info_share_selecciona), Toast.LENGTH_SHORT);
            View view2 = toast_a.getView();
            view2.setBackgroundResource(R.drawable.style_toast);
            TextView text2 = (TextView) view2.findViewById(android.R.id.message);
            text2.setPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics()));
            text2.setTypeface(Typeface.DEFAULT_BOLD);
            text2.setTextColor(Color.WHITE);
            toast_a.show();
               //String prueba =
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            //shareIntent.setPackage("com.whatsapp");
            shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.info_share_app));
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(result)));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("video/*");
            context.startActivity(Intent.createChooser(shareIntent, context.getResources().getText(R.string.info_share_share)));


        }
    }




}