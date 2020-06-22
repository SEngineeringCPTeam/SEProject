package com.example.choppingmobile;

import java.util.Map;

public class Requirement {
    public String writer;
    public String requirementID;
    public String content;
    public Requirement()
    {
        writer="null";
        requirementID="null";
        content="null";
    }

    public Requirement(String _w, String _r, String _c)
    {
        writer= _w;
        requirementID = _r;
        content=_c;
    }

    public void fromMap(Map<String, Object> map)
    {
        writer = (String) map.get("writer");
        requirementID = (String) map.get("requirementID");
        content = (String) map.get("content");
    }
}
