package com.example.choppingmobile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentField implements IDatabaseObject {
    public String writerId;
    public ArrayList<String> commentList;

    public CommentField()
    {
        writerId = "null";
        commentList = new ArrayList<>();
    }

    public CommentField(String _id, ArrayList<String> _commentList)
    {
        writerId = _id;
        commentList=_commentList;
    }

    public void setCommentList(ArrayList<String> _commentList)
    {
        commentList=_commentList;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        writerId = map.get("writerId").toString();
        commentList = new ArrayList<>((List)map.get("commentList"));
    }
}
