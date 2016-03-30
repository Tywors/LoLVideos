package com.tywors.lolvideos.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tywors.lolvideos.R;
import com.tywors.lolvideos.objects.MyVideo;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Lenovo on 14/12/2015.
 */
public class MyVideosAdapter extends BaseAdapter {
    private static final int REPRODUCIR_VIDEO = 10018;
    private Activity mcontext;
    private List<MyVideo> list_myvideos;
    private static LayoutInflater inflater=null;
    private String url_general = "http://tywors.com/apps/lolvideos/videos/";

    public MyVideosAdapter(Activity a, List<MyVideo> list_myvideos){
        this.mcontext = a;
        this.list_myvideos = list_myvideos;
        inflater =
                (LayoutInflater)mcontext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list_myvideos.size();
    }

    @Override
    public Object getItem(int i) {
        return list_myvideos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View vi = view;
        final ViewHolder holder;

        MyVideo myvid = new MyVideo();
        myvid = list_myvideos.get(i);

        if(view==null){
            vi = inflater.inflate(R.layout.listview_row_myvideos,null);
            holder = new ViewHolder();

            holder.img_video = (ImageView)vi.findViewById(R.id.img_row_myvideos);
            holder.visitas = (TextView)vi.findViewById(R.id.txt_myvideos_visitas);
            holder.indicador = (RelativeLayout)vi.findViewById(R.id.indicador_activo);
            holder.txt_indicador = (TextView)vi.findViewById(R.id.txt_myvideos_status);
            holder.fecha = (TextView)vi.findViewById(R.id.txt_myvideos_fecha);
            holder.favoritos = (TextView)vi.findViewById(R.id.txt_myvideos_faboritos);
            holder.rating = (RatingBar)vi.findViewById(R.id.ratingBar_myvideos);
            holder.visitas_unicas = (TextView)vi.findViewById(R.id.txt_myvideos_unique_visitas);
            holder.bt_delete = (ImageView)vi.findViewById(R.id.bt_myvideos_delete);
            holder.bt_share = (ImageView)vi.findViewById(R.id.icon_myvideos_share);
            holder.votos = (TextView)vi.findViewById(R.id.txt_myvideos_votos);





            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
        }

        //si no tiene un thumbnail elegido
        holder.thumbnail = myvid.getThumbnail();
        holder.ruta = myvid.getRuta();
        holder.activo = myvid.getActivo();
        holder.visitas.setText(myvid.getVisitas());
        //giramos la fecha
        StringTokenizer tokens = new StringTokenizer(myvid.getFecha(), " ");
        String first = tokens.nextToken();
        StringTokenizer tokens2 = new StringTokenizer(first, "-");
        String one = tokens2.nextToken();
        String two = tokens2.nextToken();
        String three = tokens2.nextToken();

        holder.fecha.setText(three+"-"+two+"-"+one);
        holder.favoritos.setText(myvid.getFavoritos());
        holder.visitas_unicas.setText(myvid.getVisitas_unicas());
        holder.votos.setText(myvid.getVotos());
        try {
            holder.rating.setRating(Float.parseFloat(myvid.getNota()) / 2);
        }catch (Exception e){

        }

        //ponemos el thumbnail
        Picasso.with(mcontext).load("http://tywors.com/apps/lolvideos/thumbnails/" + holder.thumbnail).into(holder.img_video);
        //marcamos si esta activos
        switch (holder.activo){
            case "0":
                holder.indicador.setBackgroundColor(Color.parseColor("#FFBF00"));
                holder.txt_indicador.setText(mcontext.getResources().getText(R.string.alaespera));
                holder.txt_indicador.setTextColor(Color.parseColor("#FFBF00"));
                holder.bt_delete.setVisibility(View.INVISIBLE);
                holder.bt_share.setVisibility(View.GONE);
                break;
            case "1":
                holder.indicador.setBackgroundColor(Color.parseColor("#298A08"));
                holder.txt_indicador.setText(mcontext.getResources().getText(R.string.activo));
                holder.txt_indicador.setTextColor(Color.parseColor("#298A08"));
                holder.bt_delete.setVisibility(View.INVISIBLE);
                break;
            case "2":
                holder.indicador.setBackgroundColor(Color.parseColor("#FF0000"));
                holder.txt_indicador.setText(mcontext.getResources().getText(R.string.rechazado));
                holder.txt_indicador.setTextColor(Color.parseColor("#FF0000"));
                holder.bt_delete.setVisibility(View.VISIBLE);
                holder.bt_share.setVisibility(View.GONE);
                break;
        }

        holder.img_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url_general + holder.ruta), "video/mp4");
                mcontext.startActivityForResult(intent, REPRODUCIR_VIDEO);
            }
        });


        holder.bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DownloadVideo downloadTask = new DownloadVideo(mcontext);
                downloadTask.execute(url_general+holder.ruta,holder.ruta);
            }
        });


        return vi;
    }

    public void setData(List<MyVideo> thumbnails){
        this.list_myvideos.addAll(thumbnails);
        this.notifyDataSetChanged();
    }

    public class ViewHolder{
        ImageView img_video;
        String thumbnail;
        String ruta;
        String activo;

        RelativeLayout indicador;
        TextView visitas;
        TextView txt_indicador;
        TextView visitas_unicas;
        TextView favoritos;
        TextView fecha;
        TextView votos;
        ImageView bt_delete;
        ImageView bt_share;
        RatingBar rating;
    }
}
