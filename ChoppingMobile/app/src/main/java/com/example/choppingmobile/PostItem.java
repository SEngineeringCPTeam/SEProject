package com.example.choppingmobile;

import android.net.Uri;
//ListView에 출력될 때 사용되는 PostItem객체
public class PostItem {
    public Uri image;
    public String title;
    public String writer;
    public String cost;
    public String id;
    public String downloadURL=null;
    public boolean isCom =false;
    public void setId(String _id)
    {
        id=_id;
    }

    public PostItem()
    {
        image=null;
        title="null";
        writer="null";
        cost = "null";
        isCom=false;
    }

    public PostItem(Uri img, String _title, String _writer, String _cost, boolean _com)
    {
        image=img;
        title=_title;
        writer=_writer;
        cost=_cost;
        isCom=_com;
    }
}
