package com.example.choppingmobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakePostFragment extends Fragment implements IGetData{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MakePostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MakePostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MakePostFragment newInstance(String param1, String param2) {
        MakePostFragment fragment = new MakePostFragment();
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
        init();
    }
    private EditText titleEdit;
    private EditText contentEdit;
    private Spinner categorySpinner;
    private Button galleryBtn;
    private Button submitBtn;
    private MainActivity mainActivity;
    private FirebaseFirestore db;
    private ViewPager viewPager;
    private ArrayList<Bitmap> bitmapList;
    private ArrayList<String> category;
    private ImageAdapter adapter;
    private ArrayAdapter categoryAdapter;
    private void init()
    {
        mainActivity=MainActivity.mainActivity;
        db=mainActivity.db;
        bitmapList=new ArrayList<>();
        adapter=new ImageAdapter(getContext(),bitmapList);
        category=new ArrayList<>();
        getCategoryListFromDB();
    }

    private void initWidget(ViewGroup vg)
    {
        titleEdit=vg.findViewById(R.id.makePostTitleEdit);
        categorySpinner=vg.findViewById(R.id.categorySpinner);
        contentEdit=vg.findViewById(R.id.postContent);
        submitBtn = vg.findViewById(R.id.postSubmitBtn);
        galleryBtn = vg.findViewById(R.id.addImage);
        viewPager=vg.findViewById(R.id.makePostViewPager);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.fragment_make_post, container, false);

        initWidget(vg);
        Log.e("categoryData",category.toString());

        viewPager.setAdapter(adapter);

        if(validation())
            Log.e("validation","valid");
        else
            Log.e("validation","not valid");

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, MainActivity.galleryCode);
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePost();
            }
        });
        return vg;
    }
    public boolean validation()
    {
        if(titleEdit.getText().length()==0)
            return false;
        if(contentEdit.getText().length()==0)
            return false;
        return true;
    }
    public void makePost()
    {
        Post post = new Post(titleEdit.getText().toString(),contentEdit.getText().toString(),"qwertq");
        db.collection("Post").add(post).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(getContext(),"Post가 등록되었습니다.",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error: Post가 등록되지 않았습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCategoryListFromDB()
    {
        Query categoryQuery = db.collection("Category").whereEqualTo("name","productCategory");
        Log.e("category","startQuery");
        categoryQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document:task.getResult())
                {
                    getData("category",new ArrayList<String>((List)document.get("productCategory")));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Error: 카테고리를 받아올 수 없습니다",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void getData(String id, Object data) {
        if(id.equals("category")){
            category=(ArrayList<String>) data;
            categoryAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,category);
            categorySpinner.setAdapter(categoryAdapter);
            Log.e("categoryLength",Integer.toString(category.size()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==MainActivity.galleryCode)
        {
            if(resultCode==-1) {
                try {
                    InputStream in = getContext().getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    adapter.appendBitmap(img);
                    Log.e("exception_h",Integer.toString(viewPager.getHeight()));
                    Log.e("exception_n",Integer.toString(adapter.getCount()));
                    Log.e("exception",Integer.toString(img.getHeight()));
                } catch (Exception ex)
                {
                    Log.e("exception",ex.toString());
                }

            }
        }
    }
}
