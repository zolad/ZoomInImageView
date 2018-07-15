/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.zolad.zoominimageview.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zolad.zoominimageview.ZoomInImageView;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImgViewHolder> {

    private List<Integer> mImgList;
    Context context;

    ImageListAdapter(Context context) {

        this.context = context;
    }




    @Override
    public ImgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_image, parent, false);


        ImgViewHolder viewHolder = new ImgViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ImgViewHolder holder, int position) {

        holder.tv_position.setText("picture"+(position+1));

        /**
         * load image resource
         * */
        Glide.with(context).load(mImgList.get(position)).into(holder.iv_pic);


        holder.iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"click",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mImgList == null ? 0 : mImgList.size();
    }

    public void setImgList(List<Integer> imgList) {
        mImgList = imgList;
        notifyDataSetChanged();
    }

    public static class ImgViewHolder extends RecyclerView.ViewHolder {
        public final ZoomInImageView iv_pic;
        public final TextView tv_position;
        public ImgViewHolder(View itemView) {
            super(itemView);
            iv_pic = (ZoomInImageView) itemView.findViewById(R.id.iv_pic);
            tv_position = (TextView) itemView.findViewById(R.id.tv_position);

        }
    }
}
