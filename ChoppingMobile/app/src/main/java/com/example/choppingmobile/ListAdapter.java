package com.example.choppingmobile;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private ArrayList<PostItem> itemList;
    private Context context;
    private boolean com=false;//상업용인지
    public ListAdapter(Context c, boolean _com)
    {
        context = c;
        itemList=new ArrayList<>();
        com=_com;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.new_item_layout, parent, false);
        }
        LinearLayout layout = convertView.findViewById(R.id.itemLayout);
        layout.setOnClickListener(new View.OnClickListener() {//listView 클릭했을 때 이벤트
            @Override
            public void onClick(View v) {
                if (com) {//상업용일 때
                    Toast.makeText(context, "커머셜" + position, Toast.LENGTH_SHORT).show();
                } else//상업용 아닐때
                {
                    Toast.makeText(context, "커머셜X" + position, Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageView imgView = convertView.findViewById(R.id.itemImg);
        TextView titleView = convertView.findViewById(R.id.item_titleText);
        TextView writerView = convertView.findViewById(R.id.item_writerText);
        TextView costView = convertView.findViewById(R.id.item_costText);
        if(!com)
            costView.setVisibility(View.GONE);
        Log.e("exception_t",Integer.toString(position));
        PostItem item = itemList.get(position);
        if(item.downloadURL!=null)
        {
            setImageFromGlide(imgView,item.downloadURL);
        }else{
            if(item.image!=null)
                imgView.setImageURI(item.image);
            else
                imgView.setBackgroundColor(Color.GRAY);
        }
        Log.e("exception_t",Integer.toString(position));
        titleView.setText("Title: "+item.title);
        writerView.setText("Writer: "+item.writer);
        costView.setText("Cost: "+item.cost);
        return convertView;
    }
    public void setImageFromGlide(ImageView _imgView, String url)
    {
        Log.e("glide","glide...");
        Glide.with(context).load(url).into(_imgView);
    }
    public void addItem(PostItem item)
    {
        if(item!=null)
            itemList.add(item);
        notifyDataSetChanged();
    }
    public void pushItem(PostItem item)
    {

    }
    public void resetItem()
    {
        if(itemList!=null)
            itemList.clear();
        notifyDataSetChanged();
    }
}
