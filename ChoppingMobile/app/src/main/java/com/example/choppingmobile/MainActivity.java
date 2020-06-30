package com.example.choppingmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.model.value.TimestampValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    public static MainActivity mainActivity;
    public FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public DatabaseReference databaseReference = firebaseDatabase.getReference();

    public static int galleryCode = 102;
    HashMap<String, Object> childUpdates = null;
    Map<String, Object> userValue = null;
    User user;
    Info info;
    public Assign assign;
    Button getMessage;
    LoginFragment loginScreen;
    SignupFragment signupScreen;

    enum Screen{
        Login, SignUp,OpenMarket,Community
    }
    private void init()
    {
        mainActivity =this;
        assign=new Assign();
        loginScreen=new LoginFragment();
        signupScreen=new SignupFragment();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentLayout, loginScreen).commit();
    }
    public static Map<String, Object> JSONtoMap(DataSnapshot dataSnapshot)
    {
        Map<String, Object> map = new HashMap<>();
        for(DataSnapshot ds: dataSnapshot.getChildren())
        {
            String key = ds.getKey();
            Object val = ds.getValue();
            map.put(key,val);
        }
        return map;
    }
    /*
    * renewalAssign: modify current Assignment
    * @param assign value
    * @turn success or not
     */
    public boolean renewalAssign(Assign _assign)
    {
        if(_assign!=null)
        {
            assign.id = _assign.id;
            assign.authority = _assign.authority;
            return true;
        }
        return false;
    }
    /*
    * getAuthority: get Authority from database
    * @param None
    * @turn None
     */
    public void getAuthority()
    {
        if(assign!=null)
        {
            db.collection("User")
                    .whereEqualTo("id",assign.id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot task:queryDocumentSnapshots)
                            {
                                assign.setAuthority(task.get("authority").toString());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("exception","Load Assignment failure");
                        }
                    });
        }
    }
    /*
    * getUserData: get User data from databaase
    * @param callback class which want get a data
    * @turn None
     */
    public void getUserData(final ICallbackTask callback)
    {
        if(assign!=null)
        {
            Query userDate=db.collection("User").whereEqualTo("id",assign.id);
            userDate.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot document:task.getResult())
                    {
                        callback.GetData(document.getData());
                    }
                }
            });
        }
    }
    /*
    * setScreen: sec Main Activity fragment
    * @param screen which flate on screen
    * @turn None
     */
    public void setScreen(Screen screen)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(screen==Screen.Login) {
            transaction.replace(R.id.fragmentLayout, loginScreen).commit();
        }
        else if(screen==Screen.SignUp) {
            signupScreen=new SignupFragment();
            transaction.replace(R.id.fragmentLayout, signupScreen).commit();
        }
        if(screen!=Screen.Login)
            transaction.addToBackStack(null);
    }

}
