package com.example.choppingmobile;

import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Post implements IDatabaseObject{
    public String title;
    public String content;
    public String writer;
    public ArrayList<String> urlList;
    public String commentId;

    public Post(String _title, String con, String _commentId)
    {
        title=_title;
        content=con;
        commentId=_commentId;
        urlList=new ArrayList<>();
        urlList.add("test");
        writer=MainActivity.mainActivity.assign.id;
    }

    public Post()
    {
        title="Null Title";
        content="Empty";
        commentId="NULL";
        writer=MainActivity.mainActivity.assign.id;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String,Object> map)
    {
        title = (String) map.get("title");
        content = (String)map.get("content");
        urlList = (ArrayList<String>) map.get("urlList");
    }
}
