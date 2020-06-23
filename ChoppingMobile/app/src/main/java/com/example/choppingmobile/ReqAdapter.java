package com.example.choppingmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReqAdapter extends BaseAdapter {
    ArrayList<Requirement> reqList;
    AlertDialog.Builder builder;
    Context context;
    Requirement currentRequirement;
    int currentPosition =0;

    public ReqAdapter(Context c)
    {
        context=c;
        reqList=new ArrayList<>();
        setBuilder();
    }

    public ReqAdapter(Context c, ArrayList<Requirement> _r)
    {
        context=c;
        reqList=_r;
        setBuilder();
    }
    public void setBuilder()
    {
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Message")
                .setMessage("권한을 허용하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (currentRequirement != null)
                        {
                            ServiceActivity.serviceActivity.changeAuthority(currentRequirement);
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
        return reqList.size();
    }

    @Override
    public Object getItem(int position) {
        return reqList.get(position);
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
        final Requirement requirement = reqList.get(pos);
        TextView id = convertView.findViewById(R.id.ReqUserId);
        TextView content = convertView.findViewById(R.id.reqContent);
        Log.e("req",Integer.toString(reqList.size()));
        if(reqList!=null)
        {
            if(id!=null)
                id.setText(requirement.writer);
            if(content!=null)
                content.setText(requirement.content);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //클릭 시
                    currentRequirement = requirement;
                    currentPosition=position;
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
        return convertView;
    }
    public boolean removeItem(int position)
    {
        reqList.remove(position);
        notifyDataSetChanged();
        return true;
    }
}
