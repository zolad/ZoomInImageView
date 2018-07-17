package com.zolad.zoominimageview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.zolad.zoominimageview.ZoomInImageViewAttacher;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageListAdapter mAdapter;

    RecyclerView mRVlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRVlist = (RecyclerView) findViewById(R.id.rv_imagelist);
        mRVlist.setLayoutManager(new LinearLayoutManager(this));

        mRVlist.setAdapter(mAdapter = new ImageListAdapter(this));


        List<Integer> imglist = new ArrayList<>();
        imglist.add(R.drawable.img_1);
        imglist.add(R.drawable.img_3);
        imglist.add(R.drawable.img_2);
        imglist.add(R.drawable.img_4);

        mAdapter.setImgList(imglist);


        mAdapter.notifyDataSetChanged();


      

    }
}
