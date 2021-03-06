package com.example.choppingmobile;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Community#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Community extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Community() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Community.
     */
    // TODO: Rename and change types and number of parameters
    ViewPager viewPager;

    public static Community newInstance(String param1, String param2) {
        Community fragment = new Community();
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
    }
    ServiceActivity parentActivity;
    FirebaseFirestore db;
    Query postQuery;
    Button makePostBtn;
    RequestOptions requestOptions;//;glide option
    String currentCategory="All";
    Spinner searchOptSpinner;
    ArrayAdapter searchOptAdapter;

    private int screenPostNum =5;

    EditText searchEdit;
    Button searchBtn;
    Button prevBtn;
    Button nextBtn;
    Button categoryBtn;
    Timestamp firstTimeStamp;
    Timestamp lastTimeStamp;
    ListView listView;
    ListAdapter imgAdapter;
    ArrayList<String> categoryList;
    String[] builderCategory;
    AlertDialog.Builder builder;
    /*
    * init:initialize Community Fragment Objects, Lists
    * @param viewgroup
    * @turn None
     */
    private void init(ViewGroup vg)
    {
        parentActivity=(ServiceActivity) getActivity();
        db = ServiceActivity.serviceActivity.db;
        postQuery = db.collection("Post");
        requestOptions = new RequestOptions()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.defaultimg)
                .error(R.drawable.defaultimg);
        imgAdapter = new ListAdapter(getContext(),false);

        searchOptSpinner = vg.findViewById(R.id.searchOptSpinner);
        categoryList = new ArrayList<>();
        searchOptAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item,categoryList);
        searchOptSpinner.setAdapter(searchOptAdapter);
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Category");
    }

    /*
     * init_widget:initialize Community Fragment UI Object
     * @param viewgroup
     * @turn None
     */
    private void init_widget(ViewGroup vg)
    {
        listView = vg.findViewById(R.id.communityBodyList);
        makePostBtn=vg.findViewById(R.id.communityMakePostBtn);
        categoryBtn = vg.findViewById(R.id.communityCategoryBtn);
        prevBtn = vg.findViewById(R.id.prevBtn);
        nextBtn = vg.findViewById(R.id.nextBtn);
        viewPager=vg.findViewById(R.id.viewPager);
        searchBtn = vg.findViewById(R.id.communitySearchBtn);
        searchEdit = vg.findViewById(R.id.communitySearchEdit);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg =  (ViewGroup) inflater.inflate(R.layout.fragment_community, container, false);
        init(vg);
        init_widget(vg);
        getSearchList();
        getCategoryList();
        listView.setAdapter(imgAdapter);
        getPost();


        makePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.setVolatileScreen(new MakePostFragment(false));
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchEdit.getText().length()>0&&!searchOptSpinner.getSelectedItem().toString().equals("검색")) {
                    String field = translator(searchOptSpinner.getSelectedItem().toString());
                    boolean cat = false;
                    if(!currentCategory.equals("All"))
                        cat = true;
                    getSearchList(field,searchEdit.getText().toString(),cat);
                }
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentCategory.equals("All"))
                {
                    getPrevList();
                }
                else if(searchEdit.getText().length()==0)
                {
                    getCategoricalPrevList();
                }
                else if(!searchOptSpinner.getSelectedItem().toString().equals("검색")){
                    String field = translator(searchOptSpinner.getSelectedItem().toString());
                    boolean cat = false;
                    if(!currentCategory.equals("All"))
                        cat = true;
                    //getSearchPrev(field,searchEdit.getText().toString(),cat);
                }
                else if(searchOptSpinner.getSelectedItem().toString().equals("검색"))
                    searchEdit.setText("");
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentCategory.equals("All"))
                    getNextList();
                else if(searchEdit.getText().length()==0)
                    getCategoricalNextList();
                else if(!searchOptSpinner.getSelectedItem().toString().equals("검색")){
                    String field = translator(searchOptSpinner.getSelectedItem().toString());
                    boolean cat = false;
                    if(!currentCategory.equals("All"))
                        cat = true;
                    //getSearchNext(field, searchEdit.getText().toString(),cat);
                }
                else if(searchOptSpinner.getSelectedItem().toString().equals("검색"))
                    searchEdit.setText("");
            }
        });
        categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });
        return vg;
    }
    /*
    * translator: convert input to special form
    * @param input
    * @turn converted result
     */
    public String translator(String input)
    {
        if(input.equals("제목"))
        {
            return "title";
        }
        else if(input.equals("작성자"))
        {
            return "writer";
        }
        return "null";
    }

    /*
    * getSearchList: get Category List from Database
    * @param field--> search field, word --> search word, category --> search categroy
    * @turn None
     */
    public void getSearchList(String field, String word, boolean category)
    {
         Query query = db.collection("Post")
                .orderBy("time", Query.Direction.DESCENDING)
                .whereEqualTo(field, word);
         if(category)
             query = query.whereEqualTo("category",currentCategory);
         query = query.limit(screenPostNum);
         query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        composeScreen(queryDocumentSnapshots, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
    }

    public void getSearchNext(String field, String word, boolean category)
    {
        Query query = db.collection("Post")
                .orderBy("time", Query.Direction.DESCENDING)
                .whereEqualTo(field, word);
        if(category)
            query = query.whereEqualTo("category",currentCategory);
        query = query.whereLessThan("time",lastTimeStamp.toDate()).limit(screenPostNum);
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        composeScreen(queryDocumentSnapshots, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
    }

    public void getSearchPrev(String field, String word, boolean category)
    {
        Query query = db.collection("Post")
                .orderBy("time", Query.Direction.ASCENDING)
                .whereEqualTo(field, word);
        if(category)
            query = query.whereEqualTo("category",currentCategory);
        query = query.whereGreaterThan("time",lastTimeStamp.toDate()).limit(screenPostNum);
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        composeScreen(queryDocumentSnapshots, false);
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
    * getSearchList: get object list which in search option
    * @param None
    * @turn None
     */
    public void getSearchList()
    {
        DocumentReference query = db.collection("Category").document("searchOption");
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.e("result",documentSnapshot.get("category").toString());
                categoryList = (ArrayList<String>) documentSnapshot.get("category");
                Log.e("result",Integer.toString(categoryList.size()));
                searchOptAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item,categoryList);
                searchOptSpinner.setAdapter(searchOptAdapter);
                searchOptAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception","Load Category Failure");
            }
        });
    }

    /*
     * getCategoryList: get Category List from database
     * @param None
     * @turn None
     */
    public void getCategoryList()
    {
        DocumentReference query = db.collection("Category").document("postCategory");
        query.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> temp = (ArrayList<String>) documentSnapshot.get("category");
                        builderCategory = new String[temp.size()];
                        int pos = 0;
                        for (String str:temp)
                        {
                            builderCategory[pos++] = str;
                        }
                        setBuilder();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception","Error: load Category Failure");
            }
        });
    }

    /*
     * setBuilder: set search option builder
     * @param None
     * @turn None
     */
    public void setBuilder()
    {
        if(builderCategory!=null) {
            builder.setItems(builderCategory, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    UICategory(builderCategory[which]);
                }
            });
        }
    }

    /*
     * UICategory: set UI category
     * @param current category
     * @turn None
     */
    public void UICategory(String category)
    {
        Log.e("category",category);
        categoryBtn.setText(category);
        currentCategory=category;
        searchOptSpinner.setSelection(0);
        searchEdit.setText("");
        getPost();
    }

    /*
    * getPost: get Post action by search option
    * @param None
    * @turn None
     */
    public void getPost()
    {
        if(currentCategory.equals("All"))
        {
            getPostList();
        }
        else
        {
            getCategoricalPost();
        }
    }
    /*
    * getCategoricalPost: get Data from database which fit in current category
    * @param None
    * @turn None
     */
    public void getCategoricalPost()
    {
        db.collection("Post")
                //
                .whereEqualTo("category",currentCategory)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(screenPostNum)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        composeScreen(queryDocumentSnapshots,true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception","postLoading Failure");
                    }
                });
    }
    /*
    * getCategoricalPrevList: get data from database which fit in current category and before current first Object
    * @param None
    * @turn None
     */
    public void getCategoricalPrevList()
    {
        postQuery.orderBy("time",Query.Direction.ASCENDING)
                .whereEqualTo("category",currentCategory)
                .whereGreaterThan("time",firstTimeStamp.toDate())
                .limit(screenPostNum)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()==0)
                            Toast.makeText(getContext(),"맨 앞 페이지 입니다.",Toast.LENGTH_SHORT).show();
                        else
                        {
                            Log.e("time",Integer.toString(queryDocumentSnapshots.size()));
                            composeScreen(queryDocumentSnapshots,false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception","Load PrevPage Failure");
                    }
                });
    }
    /*
    * getPrevList: get data from database which submitted before the current first Object
    * @param None
    * @turn None
     */
    public void getPrevList()
    {
        Log.e("time",firstTimeStamp.toDate().toString());
        postQuery.orderBy("time",Query.Direction.ASCENDING)
                .whereGreaterThan("time",firstTimeStamp.toDate())
                .limit(screenPostNum)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()==0)
                            Toast.makeText(getContext(),"맨 앞 페이지 입니다.",Toast.LENGTH_SHORT).show();
                        else
                        {
                            Log.e("time",Integer.toString(queryDocumentSnapshots.size()));
                            composeScreen(queryDocumentSnapshots,false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception","Load PrevPage Failure");
                    }
                });
    }

    /*
     * getCategoricalNextList: get data from database which fit in current category and after current last Object
     * @param None
     * @turn None
     */
    public void getCategoricalNextList()
    {
        postQuery.orderBy("time",Query.Direction.DESCENDING)
                .whereEqualTo("category",currentCategory)
                .whereLessThan("time",lastTimeStamp.toDate())//whereLessThan("time",lastTimeStamp)
                .limit(screenPostNum)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()==0)
                            Toast.makeText(getContext(),"마지막 페이지 입니다.",Toast.LENGTH_SHORT).show();
                        else
                        {
                            Log.e("time",Integer.toString(queryDocumentSnapshots.size()));
                            composeScreen(queryDocumentSnapshots,true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception","load nextPage failure");
            }
        });
    }

    /*
     * getNextList: get data from database which submitted after current last Object
     * @param None
     * @turn None
     */
    public void getNextList()
    {
        Log.e("time",lastTimeStamp.toDate().toString());
        postQuery.orderBy("time",Query.Direction.DESCENDING)
                .whereLessThan("time",lastTimeStamp.toDate())//whereLessThan("time",lastTimeStamp)
                .limit(screenPostNum)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()==0)
                            Toast.makeText(getContext(),"마지막 페이지 입니다.",Toast.LENGTH_SHORT).show();
                        else
                        {
                            Log.e("time",Integer.toString(queryDocumentSnapshots.size()));
                            composeScreen(queryDocumentSnapshots,true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception","load nextPage failure");
            }
        });
    }

    /*
     * getPrevList: get data from database which submitted after the current last Object
     * @param None
     * @turn None
     */
    public void getPostList()
    {
        postQuery.orderBy("time", Query.Direction.DESCENDING)
                .limit(screenPostNum)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        composeScreen(queryDocumentSnapshots,true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception","postLoading Failure");
                    }
                });
    }

    /*
     * composeScreen: compose Screen list view from query Document
     * @param query result
     * @param sorting direction true --> descending, false not
     * @turn None
     */
    public void composeScreen(QuerySnapshot queryDocumentSnapshots, boolean direction)
    {
        int num=0;
        imgAdapter.resetItem();
        for(QueryDocumentSnapshot task : queryDocumentSnapshots)
        {
            Map<String, Object> data = task.getData();
            Log.e("testing",data.get("title").toString());
            PostItem temp = new PostItem();
            ArrayList<String> previewURL = (ArrayList<String>) data.get("urlList");
            Uri imageUri =null;
            temp.setId(task.getId());
            temp.writer = data.get("writer").toString();
            temp.title = data.get("title").toString();
            if(num==0)
            {
                if(direction)
                    firstTimeStamp = (Timestamp)data.get("time");//첫 글의 timestamp
                else
                    lastTimeStamp = (Timestamp)data.get("time");
            }
            if(num==queryDocumentSnapshots.size()-1)
            {
                if(direction)
                    lastTimeStamp = (Timestamp) data.get("time");//timestamp가져옴.
                else
                    firstTimeStamp = (Timestamp)data.get("time");
                Log.e("lastTime",lastTimeStamp.toString());
            }
            if(previewURL.size()>0) {
                Log.e("image", previewURL.get(0));
                parentActivity.setDownloadURL(previewURL.get(0),temp,imgAdapter);
            }
            else
            {
                Resources resources = getContext().getResources();
                imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+resources.getResourcePackageName(R.drawable.defaultimg)+'/'+
                        resources.getResourceTypeName(R.drawable.defaultimg)+'/'+resources.getResourceEntryName(R.drawable.defaultimg));
                temp.image = imageUri;
            }
            num++;
            if(direction)
                imgAdapter.addItem(temp);
            else
                imgAdapter.pushItem(temp);
        }
    }
    public void addItemToAdapter(PostItem item)
    {
        imgAdapter.addItem(item);
    }
}
