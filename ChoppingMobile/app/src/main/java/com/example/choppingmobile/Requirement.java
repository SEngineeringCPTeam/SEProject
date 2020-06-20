package com.example.choppingmobile;

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
}
