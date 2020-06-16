package com.example.choppingmobile;

import java.util.Map;

public class Comment implements IDatabaseObject{
    public String commentId;
    public String writerId;
    public String content;

    public Comment()
    {
        commentId="null";
        writerId="null";
        content="null";
    }
    public Comment(String _cid, String _wid, String _content)
    {
        commentId=_cid;
        writerId=_wid;
        content=_content;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        writerId = map.get("writerId").toString();
        content = map.get("content").toString();
    }
}
