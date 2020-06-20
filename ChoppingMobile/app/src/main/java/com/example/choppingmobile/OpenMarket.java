package com.example.choppingmobile;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OpenMarket#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenMarket extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OpenMarket() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OpenMarket.
     */
    // TODO: Rename and change types and number of parameters
    public static OpenMarket newInstance(String param1, String param2) {
        OpenMarket fragment = new OpenMarket();
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

    private FirebaseFirestore db;
    private MainActivity mainActivity;
    private ServiceActivity serviceActivity;
    private Button uploadBtn;

    private ListAdapter imgAdapter;
    private Timestamp firstTimeStamp=null;
    private Timestamp lastTimeStamp = null;

    private Spinner searchSpinner;
    private ListView openMarketList;
    private ArrayAdapter searchOptAdapter;
    ArrayList<String> categoryList;
    public void init()
    {
        mainActivity=MainActivity.mainActivity;
        serviceActivity = ServiceActivity.serviceActivity;

        db=mainActivity.db;

        imgAdapter = new ListAdapter(getContext(),true);
        categoryList = new ArrayList<>();
        searchOptAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item,categoryList);
    }

    public void initWidget(ViewGroup vg)
    {
        uploadBtn=vg.findViewById(R.id.openMarketMakePostBtn);
        if(mainActivity.assign.authority.equals("Basic"))
            uploadBtn.setVisibility(View.GONE);
        searchSpinner = vg.findViewById(R.id.marketSearchOptSpinner);
        openMarketList = vg.findViewById(R.id.openMarketBodyList);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_open_market, container, false);
        init();
        initWidget(vg);
        getCategoryList();
        searchSpinner.setAdapter(searchOptAdapter);
        openMarketList.setAdapter(imgAdapter);


        return vg;
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
                searchSpinner.setAdapter(searchOptAdapter);
                searchOptAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception","Load Category Failure");
            }
        });
    }

    public void composeScreen(QuerySnapshot queryDocumentSnapshots)
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
                firstTimeStamp = (Timestamp)data.get("time");//첫 글의 timestamp
            }
            if(num==queryDocumentSnapshots.size()-1)
            {
                lastTimeStamp = (Timestamp) data.get("time");//timestamp가져옴.
                Log.e("lastTime",lastTimeStamp.toString());
            }
            if(previewURL.size()>0) {
                Log.e("image", previewURL.get(0));
                serviceActivity.setDownloadURL(previewURL.get(0),temp,imgAdapter);
            }
            else
            {
                Resources resources = getContext().getResources();
                imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+resources.getResourcePackageName(R.drawable.defaultimg)+'/'+
                        resources.getResourceTypeName(R.drawable.defaultimg)+'/'+resources.getResourceEntryName(R.drawable.defaultimg));
                temp.image = imageUri;
            }
            num++;
            imgAdapter.addItem(temp);
        }
    }
}
