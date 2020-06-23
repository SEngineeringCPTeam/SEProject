package com.example.choppingmobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BuyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BuyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public BuyFragment() {
        init();
        // Required empty public constructor
    }
    public BuyFragment(String _id) {
        // Required empty public constructor
        id=_id;
        init();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BuyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BuyFragment newInstance(String param1, String param2) {
        BuyFragment fragment = new BuyFragment();
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
    ServiceActivity serviceActivity;
    MainActivity mainActivity;
    String id;
    ArrayList<String> items;
    FirebaseFirestore db;

    TextView costView;
    Button submitBtn;
    int cost;
    public void init()
    {
        items=new ArrayList<>();
        serviceActivity=ServiceActivity.serviceActivity;
        mainActivity=MainActivity.mainActivity;
        db = serviceActivity.db;
    }

    public void initWidget(ViewGroup vg)
    {
        costView = vg.findViewById(R.id.resultPage_cost);
        submitBtn = vg.findViewById(R.id.resultPage_submitBtn);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_buy, container, false);
        initWidget(vg);
        costView.setText("Cost: "+Integer.toString(cost));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeOrder();
            }
        });
        return vg;
    }

    public void makeOrder()
    {
        Receipt order = new Receipt();
        order.time= Timestamp.now();
        order.buyer=mainActivity.assign.id;
        order.items =items;
        order.cost=cost;

        db.collection("Order")
                .add(order)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentReference.update("id",documentReference.getId());
                    }
                });
    }
    public int appendItems(String id)
    {
        items.add(id);
        return items.size();
    }
    public int appendCost(String _cost)
    {
        try {
            int c = Integer.parseInt(_cost);
            cost+=c;
        }
        catch (Exception e)
        {
            Log.e("exception",e.toString());
            serviceActivity.endTask(this);
        }
        return cost;
    }
}
