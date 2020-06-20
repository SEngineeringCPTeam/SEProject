package com.example.choppingmobile;

import android.net.Uri;

public class PostItem {
    public Uri image;
    public String title;
    public String writer;
    public String cost;
    public String id;
    public String downloadURL=null;
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
    }

    public PostItem(Uri img, String _title, String _writer, String _cost)
    {
        image=img;
        title=_title;
        writer=_writer;
        cost=_cost;
    }
}
