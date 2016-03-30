package com.tywors.lolvideos.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tywors.lolvideos.R;
import com.tywors.lolvideos.objects.MyGananciasHistorial;
import com.tywors.lolvideos.objects.MyVideo;

import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Lenovo on 14/12/2015.
 */
public class MyHistorialPagosAdapter extends BaseAdapter {
    private Activity mcontext;
    private List<MyGananciasHistorial> list_myhistorial;
    private static LayoutInflater inflater=null;

    public MyHistorialPagosAdapter(Activity a, List<MyGananciasHistorial> list_myhistorial){
        this.mcontext = a;
        this.list_myhistorial = list_myhistorial;
        inflater =
                (LayoutInflater)mcontext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list_myhistorial.size();
    }

    @Override
    public Object getItem(int i) {
        return list_myhistorial.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View vi = view;
        final ViewHolder holder;

        MyGananciasHistorial myhis = new MyGananciasHistorial();
        myhis = list_myhistorial.get(i);

        if(view==null){
            vi = inflater.inflate(R.layout.listview_row_historialpagos,null);
            holder = new ViewHolder();


            holder.date = (TextView)vi.findViewById(R.id.txt_row_historial_date);
            holder.amount = (TextView)vi.findViewById(R.id.txt_row_historial_amount);
            holder.dir_btc = (TextView)vi.findViewById(R.id.txt_row_historial_dirbtc);
            holder.status = (TextView)vi.findViewById(R.id.txt_row_historial_status);






            vi.setTag(holder);
        }else{
            holder = (ViewHolder)vi.getTag();
        }

        //si no tiene un thumbnail elegido
        //holder.thumbnail = myvid.getThumbnail();
       // holder.ruta = myvid.getRuta();
       // holder.activo = myvid.getActivo();
       // holder.visitas.setText(myvid.getVisitas());
        //giramos la fecha
     /**   StringTokenizer tokens = new StringTokenizer(myhis.getDate(), " ");
        String first = tokens.nextToken();
        StringTokenizer tokens2 = new StringTokenizer(first, "-");
        String one = tokens2.nextToken();
        String two = tokens2.nextToken();
        String three = tokens2.nextToken();*/

        holder.date.setText(myhis.getDate());
        holder.amount.setText(myhis.getAmount());
        holder.dir_btc.setText(myhis.getDir_btc());
        holder.status.setText(myhis.getStatus());

        return vi;
    }

    public void setData(List<MyGananciasHistorial> myHis){
        this.list_myhistorial.addAll(myHis);
        this.notifyDataSetChanged();
    }

    public class ViewHolder{
        TextView date;
        TextView amount;
        TextView dir_btc;
        TextView status;
    }
}
