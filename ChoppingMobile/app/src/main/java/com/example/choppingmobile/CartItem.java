package com.example.choppingmobile;


import com.google.firebase.Timestamp;

import java.util.Map;

public class CartItem {
    public String buyer;
    public String itemID;
    public String itemName;
    public String cost;
    public Timestamp time;
    public CartItem() {

    }

    public CartItem(String _b, String _i, String _n, String _c) {
        buyer = _b;
        itemID = _i;
        itemName = _n;
        cost = _c;
    }
    /*
    * fromMap: compose Object from Map class
    * @param: map_include object data
    * @return: None
     */
    public void fromMap(Map<String, Object> map)
    {
        buyer = (String)map.get("buyer");
        itemID = (String)map.get("itemID");
        itemName = (String)map.get("itemName");
        cost = (String)map.get("cost");
        time =(Timestamp)map.get("time");
    }
}
