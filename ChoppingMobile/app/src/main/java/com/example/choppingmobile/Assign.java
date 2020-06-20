package com.example.choppingmobile;

public class Assign {
    String id;
    String authority;
    public Assign()
    {
        id=null;
        authority=null;
    }
    public Assign(String _id, String _a)
    {
        id=_id;
        authority = _a;
    }
    public void setId(String _id)
    {
        id=_id;
    }
    public void setAuthority(String _authority){authority=_authority;}
}
