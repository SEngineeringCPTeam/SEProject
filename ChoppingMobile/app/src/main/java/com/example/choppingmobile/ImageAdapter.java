package com.example.choppingmobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends PagerAdapter {
    private ArrayList<Uri> images;
    private LayoutInflater inflater;
    private Context context;
    public ImageAdapter(Context c)
    {
        context=c;
        images=new ArrayList<>();
    }
    public ImageAdapter(Context c,ArrayList<Uri> bitmaps)
    {
        context=c;
        images=bitmaps;
    }
    public void appendBitmap(Uri img)
    {
        if(images!=null) {
            images.add(img);
            Log.e("images",Integer.toString(images.size()));
            notifyDataSetChanged();
        }
    }
    public void setImageAdapter(ArrayList<Uri> list)
    {
        images=list;
    }
    public ArrayList<Uri> getImages()
    {
        return images;
    }
    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.slider, container,false);
        ImageView imageView = v.findViewById(R.id.sliderImage);
        imageView.setImageURI(images.get(position));
        Log.e("images",images.get(position).toString());
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
    }
}
