package com.example.choppingmobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakePostFragment extends Fragment implements IGetData{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MakePostFragment() {
        // Required empty public constructor
    }
    public MakePostFragment(boolean _isCommersial)
    {
        isCommercial = _isCommersial;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MakePostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MakePostFragment newInstance(String param1, String param2) {
        MakePostFragment fragment = new MakePostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        init();
    }
    public boolean isCommercial=false;
    private EditText titleEdit;
    private EditText contentEdit;
    private EditText costEdit;
    private Spinner categorySpinner;
    private Button galleryBtn;
    private Button submitBtn;
    private MainActivity mainActivity;
    private ServiceActivity serviceActivity;
    private FirebaseFirestore db;
    private ViewPager viewPager;
    private ArrayList<Uri> tempImageList;
    private ArrayList<String> category;
    private ImageAdapter adapter;
    private ArrayAdapter categoryAdapter;
    private CommentField currentPostCommentField;
    private Post postInstance =null;
    private void init()
    {
        mainActivity=MainActivity.mainActivity;
        serviceActivity=ServiceActivity.serviceActivity;
        db=mainActivity.db;
        tempImageList=new ArrayList<>();
        adapter=new ImageAdapter(getContext(),tempImageList);
        category=new ArrayList<>();
        currentPostCommentField=new CommentField();
        getCategoryListFromDB();
        postInstance = new Post();
    }

    private void initWidget(ViewGroup vg)
    {
        costEdit = vg.findViewById(R.id.itemCost);
        titleEdit=vg.findViewById(R.id.makePostTitleEdit);
        categorySpinner=vg.findViewById(R.id.categorySpinner);
        contentEdit=vg.findViewById(R.id.postContent);
        submitBtn = vg.findViewById(R.id.postSubmitBtn);
        galleryBtn = vg.findViewById(R.id.addImage);
        viewPager=vg.findViewById(R.id.makePostViewPager);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_make_post, container, false);
        initWidget(vg);

        if(isCommercial)
           costEdit.setVisibility(View.VISIBLE);
        else
            costEdit.setVisibility(View.GONE);

        viewPager.setAdapter(adapter);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, MainActivity.galleryCode);
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(formatValid()) {
                    if(!isCommercial)
                        makePost();
                    else
                        makeCommercialItem();
                }
            }
        });
        return vg;
    }
    /*
    * formatValid: confirm this post is valid or not
    * @param None
    * @turn is valid or not
     */
    public boolean formatValid()
    {
        if(titleEdit.getText().length()==0)
            return false;
        if(contentEdit.getText().length()==0)
            return false;
        if(isCommercial)
        {
            if(costEdit.getText().length()==0) {
                Toast.makeText(getContext(), "가격을 입력해주세요", Toast.LENGTH_SHORT).show();
                return false;
            }
            String cost=costEdit.getText().toString();
            if(!isStringDouble(cost)) {
                Toast.makeText(getContext(), "숫자를 입력해주세요", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    /*
    * validation: confirm is valid which get from intent or db
    * @param None
    * @turn is valid or not
     */
    public boolean validation()
    {
        if(postInstance.urlList.size()<adapter.getImages().size())
            return false;
        if(postInstance.commentId.equals("null"))
            return false;
        return true;
    }
    /*
    * makeCommercialItem: make Item and upload to database
    * @param None
    * @turn None
     */
    public void makeCommercialItem()
    {
        postInstance.cost = costEdit.getText().toString();
        postInstance.title=titleEdit.getText().toString();
        postInstance.content=contentEdit.getText().toString();
        postInstance.time = FieldValue.serverTimestamp();
        postInstance.category=categorySpinner.getSelectedItem().toString();

        ArrayList<Uri> images = adapter.getImages();

        makeCommentField();
        for(int i=0;i<images.size();i++)
        {
            serviceActivity.uploadUriToStorage(this, images.get(i));
        }
        ValidThread thread = new ValidThread(200);
        thread.isDaemon();
        thread.start();
    }
    /*
     * makePost: start making post
     * @param None
     * @turn None
     */
    public void makePost()
    {
        postInstance.title=titleEdit.getText().toString();
        postInstance.content=contentEdit.getText().toString();
        postInstance.time = FieldValue.serverTimestamp();
        postInstance.category=categorySpinner.getSelectedItem().toString();

        ArrayList<Uri> images = adapter.getImages();

        makeCommentField();
        for(int i=0;i<images.size();i++)
        {
            serviceActivity.uploadUriToStorage(this, images.get(i));
        }
        ValidThread thread = new ValidThread(200);
        thread.isDaemon();
        thread.start();
    }
    /*
     * submitPost: submit post Object to database
     * @param None
     * @turn None
     */
    public void submitPost()
    {
        CollectionReference dbReference;
        if(isCommercial)
            dbReference = db.collection("Item");
        else
            dbReference = db.collection("Post");
        dbReference.add(postInstance).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(getContext(),"Post가 등록되었습니다.",Toast.LENGTH_SHORT).show();
                endTask();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error: Post가 등록되지 않았습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    * endTask: exit from this fragment
    * @param None
    * @turn None
     */
    public void endTask()
    {
        serviceActivity.endTask(this);
    }

    /*
    * makeCommentField: make comment field of this post and upload to database
    * @param None
    * @turn None
     */
    public void makeCommentField()
    {
        currentPostCommentField.writerId=mainActivity.assign.id;
        db.collection("CommentList").add(currentPostCommentField)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String id = documentReference.getId();
                        postInstance.setCommentId(id);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception","Comment Field Compose Failure");
            }
        });
    }
    /*
    * getCategoryListFromDB: get categoryList from db and apply to spinner
    * @param None
    * @turn None
     */
    public void getCategoryListFromDB()
    {
        Query categoryQuery;
        if(!isCommercial)
            categoryQuery = db.collection("Category").whereEqualTo("name","postCategory");
        else
            categoryQuery = db.collection("Category").whereEqualTo("name","productCategory");
        categoryQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document:task.getResult())
                {
                    getData("category",new ArrayList<String>((List)document.get("category")));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error: 카테고리를 받아올 수 없습니다",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void getData(String id, Object data) {
        if(id.equals("category")){
            category=(ArrayList<String>) data;
            if(category.contains("All"))
                category.remove("All");
            categoryAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,category);
            categorySpinner.setAdapter(categoryAdapter);
            Log.e("categoryLength",Integer.toString(category.size()));
        }else if(id.equals("commentId")){
            Log.e("commentId",data.toString());
        }
    }

    @Override
    public void getUri(String url) {
        Log.e("Theuri",url);
        //string으로 받은 url을 저장, post화
        postInstance.urlList.add(url);
        Log.e("url",url);
        Log.e("url",Integer.toString(postInstance.urlList.size()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==MainActivity.galleryCode)
        {
            if(resultCode==-1) {
                Uri imageUri = data.getData();
                try {
                    adapter.appendBitmap(imageUri);
                    viewPager.setVisibility(View.VISIBLE);
                    //serviceActivity.uploadUriToStorage(this,imageUri);
                } catch (Exception ex)
                {
                    Log.e("exception",ex.toString());
                }
            }
        }
    }
    public class ValidThread extends Thread
    {
        int time;
        public ValidThread(int t)
        {
            t=time;
        }

        @Override
        public void run() {
            super.run();
            try {
                    while(!validation())
                    {
                        sleep(time);
                    }
                    submitPost();
            }catch (Exception ex)
            {
                Log.e("testing","exception");
            }
        }
    }

    public static boolean isStringDouble(String s)
    {
        try{
            Double.parseDouble(s);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
}
