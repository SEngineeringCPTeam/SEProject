package com.example.choppingmobile;

import android.widget.LinearLayout;

import com.google.firebase.firestore.FieldValue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//데이터베이스에 올릴 때 이용되는 Post객체
public class Post implements IDatabaseObject{
    public String title;
    public String content;
    public String writer;
    public String cost;
    public String category;
    public ArrayList<String> urlList;
    public String commentId;
    public FieldValue time;

    public Post(String _title, String con, String _commentId)
    {
        title=_title;
        content=con;
        category="null";
        commentId=_commentId;
        urlList=new ArrayList<>();
        writer=MainActivity.mainActivity.assign.id;
    }

    public Post()
    {
        title="Null Title";
        content="Empty";
        commentId="null";
        category="null";
        writer=MainActivity.mainActivity.assign.id;
        urlList=new ArrayList<>();
    }

    public void setCommentId(String cid)
    {
        commentId = cid;
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
        category=(String)map.get("category");
        urlList = (ArrayList<String>) map.get("urlList");
        cost = (String)map.get("cost");
    }
}
