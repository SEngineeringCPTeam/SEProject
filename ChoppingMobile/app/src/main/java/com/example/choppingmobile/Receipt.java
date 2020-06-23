package com.example.choppingmobile;


import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Receipt {
    public ArrayList<String> items;
    public String buyer;
    public Timestamp time;
    public long cost;
    public String id;
    public HashMap<String, Object> toMap()
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("items",items);
        map.put("buyer",buyer);
        map.put("time",time);
        map.put("cost",cost);
        return map;
    }
    public void fromMap(Map<String, Object> map)
    {
        items = (ArrayList<String>)map.get("items");
        buyer = (String)map.get("buyer");
        time = (Timestamp)map.get("time");
        cost = (long)map.get("cost");
        id  =(String)map.get("id");
    }
}
