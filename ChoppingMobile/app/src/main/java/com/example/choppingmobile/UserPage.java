package com.example.choppingmobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserPage extends Fragment implements ICallbackTask{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserPage.
     */
    // TODO: Rename and change types and number of parameters
    public static UserPage newInstance(String param1, String param2) {
        UserPage fragment = new UserPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    ListAdapter itemAdapter;
    ListAdapter postAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private MainActivity mainActivity;
    private ServiceActivity serviceActivity;
    private User u;
    private FirebaseFirestore db;

    private TextView nameText;
    private TextView idText;
    private TextView authorityText;
    private Button editBtn;
    private Button manageBtn;

    private ListView postList;
    private ListView itemList;

    private void init()
    {
        u=new User();
        mainActivity = MainActivity.mainActivity;
        serviceActivity =ServiceActivity.serviceActivity;
        db = mainActivity.db;
    }

    private void initWidget(ViewGroup vg)
    {
        nameText = vg.findViewById(R.id.userPage_nameText);
        idText = vg.findViewById(R.id.userPage_idText);
        authorityText = vg.findViewById(R.id.userPage_authority);

        editBtn = vg.findViewById(R.id.userPage_Edit);
        manageBtn = vg.findViewById(R.id.userPage_Manage);
        if(!mainActivity.assign.authority.equals("Admin"))
            manageBtn.setVisibility(View.GONE);
        else
            manageBtn.setVisibility(View.VISIBLE);
        postList = vg.findViewById(R.id.userPostView);
        itemList = vg.findViewById(R.id.userItemView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity.mainActivity.getUserData(this);
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_user_page, container, false);
        init();
        mainActivity.getAuthority();
        initWidget(vg);
        getUserData();
        manageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageFragment manageFragment = new ManageFragment();
                serviceActivity.setVolatileScreen(manageFragment);
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEditFragment userEditFragment = new UserEditFragment();
                serviceActivity.setVolatileScreen(userEditFragment);
            }
        });

        return vg;
    }
    public void getUserData()
    {
        String id = mainActivity.assign.id;
        Query userQuery = db.collection("User").whereEqualTo("id",id);
        userQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot task : queryDocumentSnapshots)
                {
                    Map<String, Object> userData=task.getData();
                    GetData(userData);
                    Log.e("task",task.getData().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception","getUserData Failure");
            }
        });
    }
    @Override
    public void GetData(Map<String, Object> data) {
        u.setUser(data);
        setWidget(u);
    }

    @Override
    public void GetData(Object obj) {
        //null
    }

    public void setWidget(User _u)
    {
        nameText.setText("이름: "+_u.info.get("name"));
        idText.setText("ID: "+_u.id);
        authorityText.setText("권한: "+_u.authority);
    }
}
