package com.example.choppingmobile;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserEditFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserEditFragment newInstance(String param1, String param2) {
        UserEditFragment fragment = new UserEditFragment();
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
    private ImageButton editImage;
    private EditText nameText;
    private EditText addrText;
    private EditText pwText;
    private EditText confirmText;
    private String userKey;
    private CheckBox requireAuthority;
    private User tempUser;
    private Button submitBtn;
    private EditText content;
    private ScrollView scroll;
    public void init()
    {
        mainActivity = MainActivity.mainActivity;
        serviceActivity = ServiceActivity.serviceActivity;
        tempUser = new User();
        db = mainActivity.db;
    }
    public void initWidget(ViewGroup vg)
    {
        editImage = vg.findViewById(R.id.userEdit_imageView);
        nameText = vg.findViewById(R.id.editName_userEdit);
        addrText = vg.findViewById(R.id.editAddress_userEdit);
        pwText = vg.findViewById(R.id.editPw_userEdit);
        requireAuthority = vg.findViewById(R.id.sellerAuthority);
        scroll= vg.findViewById(R.id.userEdit_contentScroll);
        confirmText = vg.findViewById(R.id.confirmPw_userEdit);
        content = vg.findViewById(R.id.userEdit_contentEdit);
        submitBtn = vg.findViewById(R.id.userEdit_SubmitBtn);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg=(ViewGroup) inflater.inflate(R.layout.fragment_user_edit, container, false);
        init();
        initWidget(vg);
        getDefaultUser();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComposeUser();
                Log.e("user",tempUser.id);
            }
        });
        requireAuthority.setOnClickListener(new CheckBox.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(requireAuthority.isChecked())
                {
                    Toast.makeText(getContext(),"입력해주세요",Toast.LENGTH_SHORT).show();
                    scroll.setVisibility(View.VISIBLE);
                }
                else if(!requireAuthority.isChecked())
                {
                    Toast.makeText(getContext(),"XXX",Toast.LENGTH_SHORT).show();
                    scroll.setVisibility(View.GONE);
                }
            }
        });
        return vg;
    }

    public boolean validation()
    {
        if(pwText.getText().length()<8&& pwText.getText().length()>0)
            return false;
        if(!pwText.getText().toString().equals(confirmText.getText().toString()))
            return false;
        return true;
    }

    private void getDefaultUser()
    {
        db.collection("User")
                .whereEqualTo("id",mainActivity.assign.id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot task : queryDocumentSnapshots)
                        {
                            tempUser.fromMap(task.getData());
                            userKey = task.getId();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception","Error: get user Error");
                    }
                });
    }
    private void ComposeUser()
    {
        boolean isModify = false;
        if(validation())
        {
            if(nameText.getText().length()>0) {
                tempUser.info.put("name", nameText.getText().toString());
                isModify=true;
            }
            if(addrText.getText().length()>0) {
                tempUser.info.put("address", addrText.getText().toString());
                isModify=true;
            }
            if(pwText.getText().length()>0) {
                tempUser.password = pwText.getText().toString();
                isModify=true;
            }
            updateUser(isModify);
        }
        else
        {
            Toast.makeText(getContext(),"양식이 맞지 않습니다.",Toast.LENGTH_SHORT).show();
        }
    }
    private void updateUser(boolean modify)
    {
        if(modify) {
            db.collection("User")
                    .document(userKey)
                    .set(tempUser)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("exception", "Error: setUserFailure");
                        }
                    });
        }
        if(requireAuthority.isChecked()&&content.getText().length()>0)
        {
            Requirement authorityRequirement = new Requirement(tempUser.id, "Seller",content.getText().toString());
            db.collection("Requirement")
                    .add(authorityRequirement)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.e("requirement","requirementSuccess");
                            Toast.makeText(getContext(),"권한 요청이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("exception","setAuthorityRequirementFailure");
                        }
                    });
        }
    }
}
