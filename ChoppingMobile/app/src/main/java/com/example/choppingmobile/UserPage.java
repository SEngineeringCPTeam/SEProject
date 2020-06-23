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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
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
    private Button buyBtn;
    private Button refundBtn;
    private ListView postList;
    private ListView itemList;
    private int screenPostNum =10;
    ListAdapter imgAdapter;
    ArrayList<CartItem> items;
    CartAdapter cartAdapter;
    public boolean init()
    {
        u=new User();
        mainActivity = MainActivity.mainActivity;
        serviceActivity =ServiceActivity.serviceActivity;
        db = mainActivity.db;
        items = new ArrayList<>();
        imgAdapter = new ListAdapter(getContext(),false);
        return true;
    }

    private void initWidget(ViewGroup vg)
    {
        nameText = vg.findViewById(R.id.userPage_nameText);
        idText = vg.findViewById(R.id.userPage_idText);
        authorityText = vg.findViewById(R.id.userPage_authority);
        buyBtn =vg.findViewById(R.id.cart_buyBtn);
        editBtn = vg.findViewById(R.id.userPage_Edit);
        manageBtn = vg.findViewById(R.id.userPage_Manage);
        if(!mainActivity.assign.authority.equals("Admin"))
            manageBtn.setVisibility(View.GONE);
        else
            manageBtn.setVisibility(View.VISIBLE);
        refundBtn = vg.findViewById(R.id.refundBtn);
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
        postList.setAdapter(imgAdapter);
        getUserData();
        getPostList();
        getCart();
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
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cartAdapter.getCount()>0) {

                    BuyFragment buyFragment = new BuyFragment();
                    for(int i=0;i<items.size();i++)
                    {
                        buyFragment.appendItems(items.get(i).itemID);
                        buyFragment.appendCost(items.get(i).cost);
                    }
                    cartAdapter.clear();
                    serviceActivity.setVolatileScreen(buyFragment);
                }
            }
        });
        refundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefundFragment refundFragment = new RefundFragment();
                serviceActivity.setVolatileScreen(refundFragment);
            }
        });
        return vg;
    }
    public boolean getUserData()
    {
        if(mainActivity.assign==null)
            return false;
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
        return true;
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

    public boolean getPostList()
    {
        Query postQuery = db.collection("Post").whereEqualTo("writer",mainActivity.assign.id);
        if(postQuery==null)
            return false;
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
        return true;
    }
    public boolean getCart()
    {
        if(db==null)
            return false;
        CollectionReference cartReference = db.collection("Cart");
        cartReference.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot task:queryDocumentSnapshots)
                        {
                            CartItem item = new CartItem();
                            item.fromMap(task.getData());
                            items.add(item);
                        }
                        cartAdapter=new CartAdapter(getContext(), items);
                        itemList.setAdapter(cartAdapter);
                    }
                });
        return true;
    }
    public int add(int a, int b)
    {
        int result = a+b;
        return result;
    }

    public boolean composeScreen(QuerySnapshot queryDocumentSnapshots, boolean direction)
    {
        int num=0;
        boolean isvalid =true;
        imgAdapter.resetItem();
        if(queryDocumentSnapshots == null)
            return false;
        for(QueryDocumentSnapshot task : queryDocumentSnapshots)
        {
            Log.e("test","testing_smap");
            Map<String, Object> data = task.getData();
            Log.e("testing",data.get("title").toString());
            PostItem temp = new PostItem();
            ArrayList<String> previewURL = (ArrayList<String>) data.get("urlList");
            Uri imageUri =null;
            temp.setId(task.getId());
            temp.writer = data.get("writer").toString();
            temp.title = data.get("title").toString();

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
            if(direction)
                imgAdapter.addItem(temp);
            else
                imgAdapter.pushItem(temp);
        }
        return  true;
    }
}
