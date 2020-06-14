package com.example.choppingmobile;

import java.util.Map;

public class Comment implements IDatabaseObject{
    public String writerId;
    public String content;

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }
}
