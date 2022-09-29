package com.customizedemo.mylibrary.todayinhistory;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.customizedemo.mylibrary.api.RequestController;
import com.customizedemo.mylibrary.api.ResultCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RecyclerInfoView extends LinearLayout {
    private final Context context;
    private List<HistoryData> datas;
    MyAdapter adapter;

    public RecyclerInfoView(Context context) {
        super(context);
        this.context = context;
        datas = new ArrayList<>();
        getInfo();
        initView();
    }

    private void getInfo() {
        RequestController.getInstance().todayInHistory(new ResultCallback() {
            @Override
            public void callback(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray data = jsonObject.optJSONArray("data");
                    if (data != null && data.length() > 0) {
                        datas.addAll(new Gson().fromJson(data.toString(), new TypeToken<List<HistoryData>>() {
                        }.getType()));
                        adapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        setBackgroundColor(Color.WHITE);
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        RecyclerView recyclerView = new RecyclerView(context);
        addView(recyclerView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView link;
            TextView year;
            TextView title;

            public MyViewHolder(@NonNull InfoItemView itemView) {
                super(itemView);
                link = itemView.link;
                year = itemView.year;
                title = itemView.title;

            }
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            InfoItemView infoItemView = new InfoItemView(context);

            return new MyAdapter.MyViewHolder(infoItemView);

        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder myViewHolder, int i) {
            myViewHolder.link.setText(datas.get(i).link);
            myViewHolder.year.setText(String.valueOf(datas.get(i).year));
            myViewHolder.title.setText(datas.get(i).title);

        }

        @Override
        public int getItemCount() {
            return datas.size();
        }


    }

    class HistoryData {
        private int year;
        private String title;
        private String link;
        private String type;
    }
}
