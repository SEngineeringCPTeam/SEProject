package com.example.choppingmobile;

import android.net.Uri;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

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
    ServiceActivity serviceActivity =null;
    FirebaseFirestore db=null;
    PostItem currentPost=null;
    ImageAdapter imageSlider=null;
    Post post =null;
    String id;
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
        serviceActivity = ServiceActivity.serviceActivity;
        postInstance=new Post();
        post = new Post();
        db = mainActivity.db;
        storageReference = serviceActivity.mStorageRef;
        imageSlider = new ImageAdapter(getContext());
        commentAdapter = new CommentAdapter(getContext());
    }
    private Post postInstance;
    private Button submitBtn;
    private Button buyBtn;
    private Button cartBtn;
    private EditText commentEdit;
    private ListView commentList;
    private CommentAdapter commentAdapter;
    private ViewPager imageField;
    private TextView content;
    private TextView costText;
    private boolean isCommercial;
    private LinearLayout commentBody;
    private RelativeLayout commercialBody;
    private ArrayList<String> downloadURLList;
    private StorageReference storageReference;

    private void initWidget(ViewGroup vg)
    {
        buyBtn = vg.findViewById(R.id.buyBtn);
        cartBtn = vg.findViewById(R.id.cartBtn);
        costText = vg.findViewById(R.id.postPageCostField);
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
            content.setText(postInstance.content);
        }
    }

    public void getDownloadURL(String url)
    {
        Log.e("downloadUri",url);
        storageReference.child(url)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("downloadUri",uri.toString());
                        imageSlider.appendBitmap(uri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_post, container, false);
        init();
        initWidget(vg);
        setPostInstance();
        imageField.setAdapter(imageSlider);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentEdit.getText().length()!=0)
                {
                    //makeComment
                    Comment _com = new Comment();
                    _com.commentId = postInstance.commentId;
                    _com.writerId=mainActivity.assign.id;
                    _com.content=commentEdit.getText().toString();
                    uploadComment(_com);
                    commentEdit.setText("");
                }
            }
        });
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //구매 시 발생하는 event
                BuyFragment buyFragment = new BuyFragment();
                buyFragment.id=id;
                serviceActivity.setVolatileScreen(buyFragment);
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //장바구니 이벤트
            }
        });
        if(!isCommercial)
        {
            commentList.setAdapter(commentAdapter);
        }
        return vg;
    }
    public void updateCommentField(ArrayList<String> comments)
    {
        if(postInstance!=null)
        {
            String commentField = postInstance.commentId;
            db.collection("CommentList")
                    .document(commentField)
                    .update("commentList",comments)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("update","commentFieldUpdated");
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
    public boolean uploadComment(Comment comment)
    {
        if(postInstance!=null)
        {
            final String commentField = postInstance.commentId;
            final DocumentReference reference = db.collection("CommentList").document(commentField);
            db.collection("Comment")
                    .add(comment)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(final DocumentReference documentReference) {
                            final String id = documentReference.getId();
                            reference.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            ArrayList<String> commentList = (ArrayList<String>) documentSnapshot.get("commentList");
                                            commentList.add(commentList.size(),id);
                                            updateCommentField(commentList);
                                            getComments(postInstance.commentId);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("exception",e.toString());
                                        }
                                    });
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
        else
            return false;
    }
    public void getComments(String id)
    {
        final ArrayList<Comment> comments = new ArrayList<>();
        Log.e("comments",postInstance.commentId);
        db.collection("Comment")
                .whereEqualTo("commentId",id)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception", e.toString());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot task:queryDocumentSnapshots)
                        {
                            Comment nc  =new Comment();
                            nc.fromMap(task.getData());
                            comments.add(nc);
                        }
                        commentAdapter.getList(comments);
                        Log.e("comments",Integer.toString(comments.size()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
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
                            id = documentSnapshot.getId();
                            composePage();

                            if(postInstance!=null)
                            {
                                for(String uri:postInstance.urlList)
                                {
                                    getDownloadURL(uri);
                                }
                                if(imageSlider.getCount()>0) {
                                    Log.e("image",Integer.toString(imageSlider.getCount()));
                                    imageField.setVisibility(View.VISIBLE);
                                }
                                else
                                    Log.e("image",Integer.toString(imageSlider.getCount()));
                                if(!isCommercial)
                                {
                                    getComments(postInstance.commentId);
                                }
                                else
                                {
                                    costText.setText(postInstance.cost);
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
    }

    @Override
    public void GetData(Map<String, Object> data) {

    }

    @Override
    public void GetData(Object obj) {
        //비트맵을 받는 Task

    }
}
