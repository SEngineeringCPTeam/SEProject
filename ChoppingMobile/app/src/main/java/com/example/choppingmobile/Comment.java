package com.example.choppingmobile;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.model.value.TimestampValue;

import java.util.Date;
import java.util.Map;

public class Comment implements IDatabaseObject{
    public String commentId;
    public String writerId;
    public String content;
    public FieldValue time;
    private Timestamp _timestamp;
    public Comment()
    {
        commentId="null";
        writerId="null";
        content="null";
        time=FieldValue.serverTimestamp();
    }
    public Comment(String _cid, String _wid, String _content)
    {
        commentId=_cid;
        writerId=_wid;
        content=_content;
        time=FieldValue.serverTimestamp();
    }
    public Timestamp getTimestamp()
    {
        return _timestamp;
    }
    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        writerId = (String) map.get("writerId");
        content = (String) map.get("content");
        commentId = (String) map.get("commentId");
        _timestamp = (Timestamp) map.get("time");
    }
}
