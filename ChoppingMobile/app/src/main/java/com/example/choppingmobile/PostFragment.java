package com.example.choppingmobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment implements ICallbackTask{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public PostFragment() {
        // Required empty public constructor
    }
    public PostFragment(PostItem instance, boolean _com) {
        // Required empty public constructor
        currentPost = instance;
        isCommercial = _com;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    MainActivity mainActivity=null;
    FirebaseFirestore db=null;
    PostItem currentPost=null;
    ImageAdapter imageSlider=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void init()
    {
        mainActivity = MainActivity.mainActivity;
        postInstance=new Post();
        db = mainActivity.db;
    }
    private Post postInstance;
    private Button submitBtn;
    private EditText commentEdit;
    private ListView commentList;
    private ViewPager imageField;
    private TextView content;
    private boolean isCommercial;
    private LinearLayout commentBody;
    private RelativeLayout commercialBody;
    private ArrayList<String> downloadURLList;
    private void initWidget(ViewGroup vg)
    {
        content = vg.findViewById(R.id.postPageContent);
        submitBtn = vg.findViewById(R.id.commentSubmitBtn);
        commentEdit = vg.findViewById(R.id.commentField);
        commentList = vg.findViewById(R.id.commentList);
        imageField = vg.findViewById(R.id.postPageImage);
        commentBody = vg.findViewById(R.id.postPageCommentBody);
        commercialBody = vg.findViewById(R.id.postPageCommercial);
        commentList = vg.findViewById(R.id.commentList);
        if(currentPost!=null)
        {
            if(currentPost.downloadURL!=null)
            {
                imageField.setVisibility(View.VISIBLE);
            }
            if(isCommercial)
            {
                commercialBody.setVisibility(View.VISIBLE);
                commentBody.setVisibility(View.GONE);
            }
            else
            {
                commercialBody.setVisibility(View.GONE);
                commentBody.setVisibility(View.VISIBLE);
            }
        }
    }
    public void composePage()
    {
        if(currentPost!=null)
        {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_post, container, false);
        init();
        initWidget(vg);
        setPostInstance();
        return vg;
    }

    public void getImageFromDB()
    {

    }

    public void setPostInstance()
    {
        CollectionReference dbReference;
        Log.e("content",currentPost.id);
        Log.e("content",currentPost.title);
        if(isCommercial)
            dbReference = db.collection("Item");
        else
            dbReference = db.collection("Post");
        if(currentPost!=null) {
            dbReference.document(currentPost.id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.e("content",documentSnapshot.getData().toString());
                            postInstance.fromMap(documentSnapshot.getData());
                            Log.e("content",postInstance.content);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("exception",e.toString());
                        }
                    });
        }
    }

    @Override
    public void GetData(Map<String, Object> data) {

    }

    @Override
    public void GetData(Object obj) {
        //비트맵을 받는 Task

    }
}
