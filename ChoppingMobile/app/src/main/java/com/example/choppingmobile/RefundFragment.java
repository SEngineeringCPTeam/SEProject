package com.example.choppingmobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RefundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RefundFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RefundFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RefundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RefundFragment newInstance(String param1, String param2) {
        RefundFragment fragment = new RefundFragment();
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
    MainActivity mainActivity;
    ServiceActivity serviceActivity;
    public void init()
    {
        mainActivity = MainActivity.mainActivity;
        serviceActivity = ServiceActivity.serviceActivity;
        db = serviceActivity.db;
    }
    FirebaseFirestore db;
    ListView refundList;
    RefundAdapter refundAdapter;
    public void initWidget(ViewGroup vg)
    {
        refundList = vg.findViewById(R.id.refundList);
        refundAdapter = new RefundAdapter(getContext());
        readData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_refund, container, false);

        init();
        initWidget(vg);
        refundList.setAdapter(refundAdapter);

        return vg;
    }
    public boolean readData()
    {
        final ArrayList<Receipt> r_list = new ArrayList<>();
        db.collection("Order")
                .whereEqualTo("buyer",mainActivity.assign.id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot task:queryDocumentSnapshots)
                        {
                            Receipt r = new Receipt();
                            r.fromMap(task.getData());
                            r_list.add(r);
                        }
                        refundAdapter.setList(r_list);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                });
        if(refundAdapter.getCount()>0)
            return true;
        else return false;
    }

}
