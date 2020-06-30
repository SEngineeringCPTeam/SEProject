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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    /*
    * removeComment: remove comment from database
    * @param comment which get from database
    * @turn success or not
     */
    public boolean removeComment(Comment comment)
    {
        final String commentField = comment.commentId;
        Date date = comment.getTimestamp().toDate();
        db.collection("Comment")
                .whereEqualTo("time",date)
                .whereEqualTo("writerId",comment.writerId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot task:queryDocumentSnapshots)
                        {
                            String id = task.getId();
                            removeFromDB(id,"Comment");
                            removeFromCommentList(commentField,id);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
        return true;
    }
    /*
    * removeFromCommentList: remove comment From comment List
    * @param commentfieldid(String)
    * @param comment key(String)
    * @turn None
     */
    public void removeFromCommentList(final String commentId,final String comment)
    {
        Log.e("comment",commentId);
        db.collection("CommentList")
                .document(commentId)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception", e.toString());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> commentList = (ArrayList<String>)documentSnapshot.get("commentList");
                        commentList.remove(comment);
                        modifyList(documentSnapshot.getId(),commentList);
                    }
                });
    }

    public void modifyList(String id, ArrayList<String> comment)
    {
        db.collection("CommentList").document(id)
                .update("commentList",comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("update","modifySuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
    }


    public int setDefaultBitmap(Bitmap b)
    {
        defaultBitmap=b;
        return defaultBitmap.getWidth();
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
    public void setImageFromGlide(ImageView _imgView, String url)
    {
        Log.e("glide","glide...");
        Glide.with(getApplicationContext()).load(url).into(_imgView);
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

    public String getDate()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        return format.format(new Date());
    }

    public void changeAuthority(final Requirement req)
    {
        db.collection("User")
                .whereEqualTo("id",req.writer)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot task:queryDocumentSnapshots)
                        {
                           String id=task.getId();
                           setAuthority(id, req.requirementID);
                           removeRequirement(req.writer);
                        }
                    }
                });
    }
    public void removeRequirement(String id)
    {
        db.collection("Requirement")
                .whereEqualTo("writer",id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot task:queryDocumentSnapshots)
                        {
                            removeFromDB(task.getId(),"Requirement");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
    }
    public void removeFromDB(String id, String field)
    {
        db.collection(field).document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("success","deleteSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
    }

    public void setAuthority(String id, String authority)
    {
        DocumentReference reference = db.collection("User").document(id);
        reference.update("authority",authority)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("success","success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
    }

    /*
    * endTask: end current Fragment end load prev thing
    * @param fragment instance
    * @turn None
     */
    public void endTask(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(fragment).commit();
        fragmentManager.popBackStack();
    }

    /*
    * foundId: foundId From CartItem instance
    * @param cartitem instance
    * @param action for cart item
    * @turn None
     */
    public void foundId(CartItem item, final String action)
    {
        Log.e("num","num");
        db.collection("Cart")
                .whereEqualTo("itemID",item.itemID)
                .whereEqualTo("buyer",mainActivity.assign.id)
                .whereEqualTo("time",item.time.toDate())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot task:queryDocumentSnapshots)
                        {
                            String cartId = task.getId();
                            if(action.equals("remove"))
                            {
                                removeCart(cartId);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
    }

    /*
    * removeCart: remove Item from cary
    * @param item id
    * @turn None
     */
    public void removeCart(String itemID)
    {
        Log.e("id",itemID);
        db.collection("Cart")
                .document(itemID)
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("success","removeCartSuccess");
                    }
                });
    }
}
