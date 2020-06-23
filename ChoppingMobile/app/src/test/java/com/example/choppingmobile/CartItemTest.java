package com.example.choppingmobile;

import com.google.firebase.Timestamp;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CartItemTest {
    CartItem item = new CartItem();
    @Test
    public void fromMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("buyer","b");
        map.put("itemID","i");
        map.put("itemName","c");
        map.put("cost","t");
        map.put("time",Timestamp.now());
        item.fromMap(map);
        assertEquals(Timestamp.now().getSeconds(),item.time.getSeconds());
    }
}