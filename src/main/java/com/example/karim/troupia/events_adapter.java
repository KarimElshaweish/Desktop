package com.example.karim.troupia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Karim on 3/8/2018.
 */

public class events_adapter extends BaseAdapter {
    String images[];
    String Name[];
    String Street[];
    private Context ctx;
    private LayoutInflater layoutInflater;

    public events_adapter(String[] images, String[] name, String[] street, Context ctx) {
        this.images = images;
        Name = name;
        Street = street;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {

        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View ListView=view;
        if(ListView==null){
            layoutInflater= (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ListView=layoutInflater.inflate(R.layout.events_layout_items,null);
        }
        TextView NameTxt= ListView.findViewById(R.id.event_Name);
        TextView StreetTxt=ListView.findViewById(R.id.event_Street);
        CircleImageView eventImage=ListView.findViewById(R.id.event_image);
        NameTxt.setText(Name[i]);
        StreetTxt.setText(Street[i]);
        Glide.with(ctx).load(images[i]).into(eventImage);
        return ListView;
    }
}
