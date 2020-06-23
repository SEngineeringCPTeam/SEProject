package com.example.choppingmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RefundAdapter extends BaseAdapter {
    Context c;
    ArrayList<Receipt> receipt;
    AlertDialog.Builder builder;
    Receipt currentReceipt;
    MainActivity mainActivity;
    ServiceActivity serviceActivity;
    int currentPosition = -1;

    public RefundAdapter(Context _c)
    {
        mainActivity = MainActivity.mainActivity;
        serviceActivity = ServiceActivity.serviceActivity;
        c = _c;
        receipt =new ArrayList<>();
        currentReceipt =null;
        setBuilder();
    }

    public RefundAdapter(Context _c, ArrayList<Receipt> _list)
    {
        mainActivity = MainActivity.mainActivity;
        serviceActivity = ServiceActivity.serviceActivity;
        c = _c;
        receipt =_list;
        currentReceipt =null;
        setBuilder();
    }
    public boolean setList(ArrayList<Receipt> _r)
    {
        receipt=_r;
        notifyDataSetChanged();
        return true;
    }

    public void setBuilder()
    {
        builder = new AlertDialog.Builder(c);
        builder.setTitle("Message")
                .setMessage("환불하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (currentPosition!=-1)
                        {
                            Log.e("delete","delete");

                            removeItem(currentPosition);
                            currentPosition=-1;
                            //데이터셋에서 삭제
                        }
                        else
                            Log.e("delete","null");
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
    }
    public void getList(ArrayList<Receipt> commentList)
    {
        receipt =commentList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return receipt.size();
    }

    @Override
    public Object getItem(int position) {
        return receipt.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.requirement_item, parent, false);
        }
        final Receipt com = receipt.get(position);

        LinearLayout layout = convertView.findViewById(R.id.reqLayout);
        TextView id = convertView.findViewById(R.id.ReqUserId);
        TextView content = convertView.findViewById(R.id.reqContent);

        if(com!=null)
        {
            //id.setText(Long.toString(com.cost));
            setTextview(id, receipt.get(position).items.get(0));
            content.setText(com.time.toDate().toString());
        }
        layout.setOnClickListener(new View.OnClickListener() {//listView 클릭했을 때 이벤트
            @Override
            public void onClick(View v) {
                currentPosition=position;
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return convertView;
    }
    public int removeItem(int position)
    {
        removeFromDB(position);
        receipt.remove(position);
        notifyDataSetChanged();
        return receipt.size();
    }
    public void setTextview(final TextView tv, String id)
    {
        serviceActivity.db.collection("Item")
                .document(id)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception",e.toString());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        tv.setText(documentSnapshot.get("title").toString());
                    }
                });
    }
    public void removeFromDB(int position)
    {
        FirebaseFirestore db = serviceActivity.db;
        db.collection("Order").document(receipt.get(position).id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("refund","success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("refund","failure");
                    }
                });
    }
}
