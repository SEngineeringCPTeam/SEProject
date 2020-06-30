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

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {
    Context c;
    ArrayList<Comment> comments;
    AlertDialog.Builder builder;
    Comment currentComment;
    MainActivity mainActivity;
    ServiceActivity serviceActivity;
    int currentPosition = -1;

    public CommentAdapter(Context _c)
    {
        mainActivity = MainActivity.mainActivity;
        serviceActivity = ServiceActivity.serviceActivity;
        c = _c;
        comments=new ArrayList<>();
        currentComment=null;
        setBuilder();
    }

    public CommentAdapter(Context _c, ArrayList<Comment> _commentList)
    {
        mainActivity = MainActivity.mainActivity;
        serviceActivity = ServiceActivity.serviceActivity;
        c = _c;
        comments=_commentList;
        currentComment=null;
        setBuilder();
    }

    public void setBuilder()
    {
        builder = new AlertDialog.Builder(c);
        builder.setTitle("Message")
                .setMessage("삭제하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (currentPosition!=-1)
                        {
                            Log.e("delete","delete");
                            serviceActivity.removeComment(comments.get(currentPosition));
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
    public void getList(ArrayList<Comment> commentList)
    {
        comments=commentList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
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
        final Comment com = comments.get(position);

        LinearLayout layout = convertView.findViewById(R.id.reqLayout);
        TextView id = convertView.findViewById(R.id.ReqUserId);
        TextView content = convertView.findViewById(R.id.reqContent);

        if(com!=null)
        {
            id.setText(com.writerId);
            content.setText(com.content);
        }
        layout.setOnClickListener(new View.OnClickListener() {//listView 클릭했을 때 이벤트
            @Override
            public void onClick(View v) {
                if(com.writerId.equals(mainActivity.assign.id)||mainActivity.assign.authority.equals("Admin"))
                {
                    currentPosition=position;
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        return convertView;
    }
    /*
    * removeItem: remove Item from list
    * @param: list position
    * @turn: list size
     */
    public int removeItem(int position)
    {
        comments.remove(position);
        notifyDataSetChanged();
        return comments.size();
    }
}
