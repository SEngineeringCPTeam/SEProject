package com.example.choppingmobile;

import java.util.Map;

public interface IDatabaseObject {
    /*
     * toMap: compose Map Class which include Object's data
     * @param: None
     * @turn: map_include object data
     */
    public Map<String,Object> toMap();
    /*
     * fromMap: compose Object from Map class
     * @param: map_include object data
     * @return: None
     */
    public void fromMap(Map<String,Object> map);
}
