package com.example.choppingmobile;


import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Map;

public class Receipt {
    public ArrayList<String> items;
    public String buyer;
    public Timestamp time;
    public int cost;
    public void fromMap(Map<String, Object> map)
    {
        items = (ArrayList<String>)map.get("items");
        buyer = (String)map.get("buyer");
        time = (Timestamp)map.get("time");
        cost = (int)map.get("cost");
    }
}
