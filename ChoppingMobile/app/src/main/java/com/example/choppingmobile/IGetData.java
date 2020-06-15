package com.example.choppingmobile;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public interface IGetData {
    public void getData(String id,Object data);
    public void getUri(String url);
}
