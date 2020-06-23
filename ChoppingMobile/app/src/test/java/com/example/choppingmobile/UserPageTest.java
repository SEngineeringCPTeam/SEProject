package com.example.choppingmobile;

import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserPageTest {
    UserPage user = new UserPage();

    FirebaseFirestore db =  FirebaseFirestore.getInstance();
    @Test
    public void add() {
        int result = user.add(1,2);
        assertTrue(result==3);
    }

    @Test
    public void init(){
        boolean result = user.init();
        assertTrue(result);
    }
}