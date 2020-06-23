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

import java.util.ArrayList;

public class CartAdapter extends BaseAdapter {

    ArrayList<CartItem> itemList;
    AlertDialog.Builder builder;
    Context context;
    CartItem currentItem;
    int currentPosition =0;

    public CartAdapter(Context c)
    {
        context=c;
        itemList =new ArrayList<>();
        setBuilder();
    }

    public CartAdapter(Context c, ArrayList<CartItem> _r)
    {
        context=c;
        itemList =_r;
        setBuilder();
    }
    public void setBuilder()
    {
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Message")
                .setMessage("삭제하시겠습니까까?")                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (currentItem != null)
                        {
                            //아이템 삭제
                            removeItem(currentPosition);
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
    }
    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.requirement_item, parent, false);
        }
        LinearLayout layout = convertView.findViewById(R.id.reqLayout);
        final CartItem item = itemList.get(pos);
        TextView id = convertView.findViewById(R.id.ReqUserId);
        TextView content = convertView.findViewById(R.id.reqContent);
        Log.e("req",Integer.toString(itemList.size()));
        if(itemList !=null)
        {
            if(id!=null)
                id.setText(item.cost);
            if(content!=null)
                content.setText(item.itemName);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //클릭 시
                    currentItem = item;
                    currentPosition=position;
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
        return convertView;
    }
    public void removeItem(int position)
    {
        ServiceActivity.serviceActivity.foundId(itemList.get(position),"remove");
        itemList.remove(position);
        notifyDataSetChanged();
    }
    public void clear()
    {
        for(CartItem item:itemList)
        {
            ServiceActivity.serviceActivity.foundId(item,"remove");
        }
        itemList.clear();
        notifyDataSetChanged();
    }
}
