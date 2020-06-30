package com.example.choppingmobile;

import java.util.HashMap;
import java.util.Map;

public class User implements IDatabaseObject{
    public String id;
    public String password;
    public String authority="Basic";
    //public Info userInfo;
    public Map<String, String> info;
    public User()
    {
        id="null";
        password="null";
        info = new HashMap<>();
        //userInfo=new Info();
    }
    public User(String _id, String _password, HashMap<String,String> _info)
    {
        id=_id;
        password=_password;
        info=_info;
        //userInfo = _info;
    }
    public void setUser(String _id, String _password, HashMap<String,String> _info, String aut)
    {
        id=_id;
        password=_password;
        //userInfo = _info;
        info=_info;
        authority=aut;
    }
    public boolean setUser(String _id, String _password)
    {
        id=_id;
        password=_password;
        return true;
        //userInfo=new Info();
    }
    public boolean setUser(Map<String, Object> data)
    {
        id=(String)data.get("id");
        password=(String) data.get("password");
        authority = (String)data.get("authority");
        info = (HashMap<String,String>)data.get("info");
        //userInfo.setInfo((HashMap<String,Object>)data.get("userInfo"));
        return true;
    }

    public void setInfo(String _name, String _gender, String _birth, String _pNumber, String _address)
    {
        info.put("name",_name);
        info.put("gender",_gender);
        info.put("birth",_birth);
        info.put("pNumber",_pNumber);
        info.put("address",_address);
    }
    /*
     * fromMap: compose Object from Map class
     * @param: map_include object data
     * @return: None
     */
    @Override
    public void fromMap(Map<String, Object> map)
    {
        id=(String)map.get("id");
        password=(String) map.get("password");
        authority = (String)map.get("authority");
        info = (HashMap<String,String>)map.get("info");
        //userInfo.setInfo((HashMap<String,Object>)map.get("userInfo"));
    }

    /*
     * toMap: compose Map Class which include Object's data
     * @param: None
     * @turn: map_include object data
     */
    @Override
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("password",password);
        result.put("authority",authority);
        result.put("info",info);
        //result.put("userInfo",userInfo.toMap());
        return result;
    }

}
