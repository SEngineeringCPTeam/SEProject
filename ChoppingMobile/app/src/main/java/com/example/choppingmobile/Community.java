package com.example.choppingmobile;

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
import android.widget.ListView;
import android.widget.Spinner;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    Spinner searchOptSpinner;
    ArrayAdapter searchOptAdapter;

    EditText searchEdit;
    Button searchBtn;

    ListView listView;
    ListAdapter imgAdapter;

    ArrayList<String> categoryList;
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
    }
    private void init_widget(ViewGroup vg)
    {
        listView = vg.findViewById(R.id.communityBodyList);
        makePostBtn=vg.findViewById(R.id.communityMakePostBtn);
        viewPager=vg.findViewById(R.id.viewPager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg =  (ViewGroup) inflater.inflate(R.layout.fragment_community, container, false);
        init(vg);
        init_widget(vg);
        getCategoryList();
        listView.setAdapter(imgAdapter);

        makePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.setVolatileScreen(new MakePostFragment());
            }
        });
        getPostList();
        return vg;
    }
    public void loadImage(String url)
    {

    }

    public void getCategoryList()
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

    public void getPostList()
    {
        postQuery.orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot task : queryDocumentSnapshots)
                        {
                            Map<String, Object> data = task.getData();
                            Log.e("testing",data.get("title").toString());
                            PostItem temp = new PostItem();
                            ArrayList<String> previewURL = (ArrayList<String>) data.get("urlList");
                            temp.setId(task.getId());
                            temp.writer = data.get("writer").toString();
                            temp.title = data.get("title").toString();
                            imgAdapter.addItem(temp);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception","postLoading Failure");
                    }
                });
    }
}
