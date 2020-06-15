package com.example.choppingmobile;

import java.util.Map;

public interface IDatabaseObject {
    public Map<String,Object> toMap();
    public void fromMap(Map<String,Object> map);
}
