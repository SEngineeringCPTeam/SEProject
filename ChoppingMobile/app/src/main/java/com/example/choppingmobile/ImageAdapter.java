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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends PagerAdapter {
    private ArrayList<Uri> images;
    private ArrayList<String> downloadUris;
    private boolean onBitmap=true;
    private LayoutInflater inflater;
    private Context context;
    ServiceActivity serviceActivity;
    public ImageAdapter(Context c)
    {
        context=c;
        serviceActivity = (ServiceActivity)context;
        images=new ArrayList<>();
        onBitmap=false;
    }
    public ImageAdapter(Context c, ArrayList<String> uri, boolean _onBitmap)
    {
        context = c;
        serviceActivity = (ServiceActivity)context;
        onBitmap=_onBitmap;
    }
    public ImageAdapter(Context c,ArrayList<Uri> bitmaps)
    {
        context=c;
        serviceActivity = (ServiceActivity)context;
        images=bitmaps;
        onBitmap=true;
    }
    public void appendBitmap(Uri img)
    {
        if(images!=null) {
            images.add(img);
            Log.e("qwerty",img.toString());
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
        if(onBitmap)
            imageView.setImageURI(images.get(position));
        else
        {
            //다운로드 받는 부분.
            serviceActivity.setImageFromGlide(imageView, images.get(position).toString());
        }
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
    }
}
