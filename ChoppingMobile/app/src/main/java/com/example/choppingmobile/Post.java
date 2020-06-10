package com.example.choppingmobile;

import java.util.HashMap;

public class Post {
    public String title;
    public HashMap<String,Object> content;
    public String commentId;

    public Post(String _title, HashMap<String, Object> map, String _commentId)
    {
        title=_title;
        content=map;
        commentId=_commentId;
    }

    public Post()
    {
        title="Null Title";
        content=new HashMap<>();
        commentId="NULL";
    }
}
