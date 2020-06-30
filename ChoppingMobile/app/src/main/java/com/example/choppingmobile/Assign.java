package com.example.choppingmobile;

public class Assign {
    String id;
    String authority;
    /*
    * Assign Generator: Initialize Assign ID and Authority
    * @param: None
    * @turn: None
     */
    public Assign()
    {
        id=null;
        authority=null;
    }
    /*
    * Assign Generator: Initialize Assign with id and authority value
    * @param: _id: id value(String)
    * @param: _a: authority value(String)
    * @turn: None
     */
    public Assign(String _id, String _a)
    {
        id=_id;
        authority = _a;
    }
    /*
    * setId: set Id value with string input
    * @param: id value(String)
    * @turn: None
     */
    public void setId(String _id)
    {
        id=_id;
    }
    /*
     * setAuthority: set Authority value with string input
     * @param: authority value(String)
     * @turn: None
     */
    public void setAuthority(String _authority){authority=_authority;}
}
