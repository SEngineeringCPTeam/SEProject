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
    public void setUser(String _id, String _password)
    {
        id=_id;
        password=_password;
        //userInfo=new Info();
    }
    public void setUser(Map<String, Object> data)
    {
        id=(String)data.get("id");
        password=(String) data.get("password");
        authority = (String)data.get("authority");
        info = (HashMap<String,String>)data.get("info");
        //userInfo.setInfo((HashMap<String,Object>)data.get("userInfo"));
    }

    public void setInfo(String _name, String _gender, String _birth, String _pNumber, String _address)
    {
        info.put("name",_name);
        info.put("gender",_gender);
        info.put("birth",_birth);
        info.put("pNumber",_pNumber);
        info.put("address",_address);
    }

    @Override
    public void fromMap(Map<String, Object> map)
    {
        id=(String)map.get("id");
        password=(String) map.get("password");
        authority = (String)map.get("authority");
        info = (HashMap<String,String>)map.get("info");
        //userInfo.setInfo((HashMap<String,Object>)map.get("userInfo"));
    }

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
