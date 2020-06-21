package com.example.choppingmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ServiceActivity extends AppCompatActivity {

    public static ServiceActivity serviceActivity=null;
    public static String imgPath="ImageCollection/";

    public FirebaseFirestore db=null;
    private Query dataQuery=null;
    private HashMap<State, Fragment> pageList=null;

    public StorageReference mStorageRef;


    public Bitmap defaultBitmap=null;

    private MainActivity mainActivity;
    private Button marketBtn;
    private Button communityBtn;
    private Button userPageBtn;
    private Community communityFragment=null;
    private ServiceMain serviceMainFragment=null;
    private OpenMarket marketFragment=null;
    private UserPage userPageFragment=null;
    private State currentState=null;
    enum State{
        Community, OpenMarket,User, mainPage,idle
    };
    private void initPages()
    {
        communityFragment=new Community();
        serviceMainFragment = new ServiceMain();
        marketFragment=new OpenMarket();
        userPageFragment=new UserPage();

        pageList.put(State.Community,communityFragment);
        pageList.put(State.mainPage,serviceMainFragment);
        pageList.put(State.OpenMarket,marketFragment);
        pageList.put(State.User,userPageFragment);
    }
    private void initBtn()
    {
        marketBtn=findViewById(R.id.onenMarketBtn);
        communityBtn=findViewById(R.id.communityBtn);
        userPageBtn=findViewById(R.id.userTabBtn);
    }
    private void init()
    {
        serviceActivity=this;
        mainActivity=MainActivity.mainActivity;
        mainActivity.getAuthority();
        db=FirebaseFirestore.getInstance();
        pageList= new HashMap<>();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        initPages();
        initBtn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        Log.e("url",ServerValue.TIMESTAMP.get(".sv"));
        init();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.serviceMainLayout,serviceMainFragment).commit();
        currentState=State.mainPage;
        userPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreen(State.User);
            }
        });

        communityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreen(State.Community);
            }
        });

        marketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreen(State.OpenMarket);
            }
        });
    }
    public void setVolatileScreen(Fragment fragment)
    {
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.serviceMainLayout,fragment).commit();
        transaction.addToBackStack(currentState.toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        currentState=State.idle;
    }

    public void setScreen(State state)
    {
        Log.e("fragment",state.toString());
        getSupportFragmentManager().popBackStack(state.toString(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if(currentState!=state){
            Fragment fragment = pageList.get(state);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if(fragment!=null){
                Log.e("fragment","Change");
                transaction.addToBackStack(currentState.toString());
                transaction.replace(R.id.serviceMainLayout,fragment).commit();
                currentState=state;
            }
        }
    }
    public void setDefaultBitmap(Bitmap b)
    {
        defaultBitmap=b;
    }

    public void getBitmapFromStorage(String url, final ICallbackTask callbackTask)
    {
        mStorageRef.child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                callbackTask.GetData(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception","Image Loading Failure");
            }
        });
    }

    public void setDownloadURL(final String url, final PostItem item, final ListAdapter adapter)
    {
        mStorageRef.child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                item.downloadURL=uri.toString();
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception","Image Loading Failure");
            }
        });
    }
    public void uploadUriToStorage(final IGetData iGetData,Uri uri)
    {
        final StorageReference ref= mStorageRef.child(imgPath+uri.getLastPathSegment());
        UploadTask uploadTask = ref.putFile(uri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Theuri","TaskFailure");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                iGetData.getUri(imgPath+taskSnapshot.getMetadata().getName());
                Log.e("Theuri",taskSnapshot.getMetadata().getName());
            }
        });
    }
    //current Not used
    public void uploadBitmapToStorageByte(Bitmap bitmap)
    {
        bitmap = Bitmap.createScaledBitmap(bitmap,300,300,true);
        String img_url ="";
        img_url = imgPath+getDate()+mainActivity.assign.id;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mStorageRef.child(img_url).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception","imageUploadFailure");
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                }
            });
    }

    public String getDate()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        return format.format(new Date());
    }


}
