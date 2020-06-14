package com.example.choppingmobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
    private Button signUpBtn;
    private Button loginBtn;
    private Query getID;
    private EditText idEdit;
    private EditText pwEdit;
    DatabaseReference mDBReference = null;
    FirebaseFirestore db=null;
    String inputData;
    private void init(ViewGroup vg)
    {
        mainActivity=MainActivity.mainActivity;
        signUpBtn=vg.findViewById(R.id.signUpBtn);
        loginBtn=vg.findViewById(R.id.loginBtn);
        idEdit=vg.findViewById(R.id.idEdit);
        pwEdit=vg.findViewById(R.id.pwEdit);
        db=MainActivity.mainActivity.db;
        getID=db.collection("User");
        //mDBReference= MainActivity.mainActivity.firebaseDatabase.getReference("User");//FirebaseDatabase.getInstance().getReference("User");
        //getID = mDBReference.orderByChild("ID");//.equalTo("ID");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup=(ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);
        init(viewGroup);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData=idEdit.getText().toString();
                getID.whereEqualTo("id",inputData)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    if(task.getResult().isEmpty())//when id not Exist
                                        Toast.makeText(getContext(),"null",Toast.LENGTH_SHORT).show();
                                    else{//when id Exist
                                        for (QueryDocumentSnapshot document : task.getResult()){
                                            Log.d("getdb2",document.getId()+"==>"+document.getData());//log data(vol)
                                            if(document.getData().get("password").toString().equals(pwEdit.getText().toString()))//loginSuccess
                                            {
                                                Toast.makeText(getContext(),"Login Success",Toast.LENGTH_SHORT).show();
                                                mainActivity.assign.setId(inputData);
                                                Intent intent = new Intent(getActivity(), ServiceActivity.class);
                                                startActivity(intent);
                                            }
                                            else//loginFailure(Wrong PW)
                                            {
                                                Toast.makeText(getContext(),"Login Fail",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    Log.w("getdb2",task.getException());
                                }
                            }
                        });
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mainActivity.setScreen(MainActivity.Screen.SignUp);
            }
        });
        return viewGroup;
    }
}
