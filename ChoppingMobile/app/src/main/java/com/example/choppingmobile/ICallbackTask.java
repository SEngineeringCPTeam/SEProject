package com.example.choppingmobile;

import java.util.Map;

public interface ICallbackTask {
    /*
    * GetData: get data from map object
    * @param1: hash data which contain whole data of Object
    * @turn: None
     */
    public void GetData(Map<String,Object> data);
    /*
     * GetData: get data from Object
     * @param1: Object
     * @turn: None
     */
    public void GetData(Object obj);
}
