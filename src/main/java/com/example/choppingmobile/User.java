package com.example.choppingmobile;

import java.util.HashMap;
import java.util.Map;

public class User implements IDatabaseObject{
    public String id;
    public String password;
    public Info userInfo;
    public User()
    {
        id="null";
        password="null";
        userInfo=new Info();
    }
    public User(String _id, String _password, Info _info)
    {
        id=_id;
        password=_password;
        userInfo = _info;
    }
    public void setUser(String _id, String _password, Info _info)
    {
        id=_id;
        password=_password;
        userInfo = _info;
    }
    public void setUser(String _id, String _password)
    {
        id=_id;
        password=_password;
        userInfo=new Info();
    }
    public void setUser(Map<String, Object> data)
    {
        id=(String)data.get("id");
        password=(String) data.get("password");
        userInfo.setInfo((HashMap<String,Object>)data.get("userInfo"));
    }

    @Override
    public void fromMap(Map<String, Object> map)
    {
        id=(String)map.get("id");
        password=(String) map.get("password");
        userInfo.setInfo((HashMap<String,Object>)map.get("userInfo"));
    }

    @Override
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("password",password);
        result.put("userInfo",userInfo.toMap());
        return result;
    }

}
