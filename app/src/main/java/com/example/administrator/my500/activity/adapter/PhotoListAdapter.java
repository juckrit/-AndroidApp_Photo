package com.example.administrator.my500.activity.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.my500.R;
import com.example.administrator.my500.activity.dao.PhotoItemCollectionDao;
import com.example.administrator.my500.activity.dao.PhotoItemDao;
import com.example.administrator.my500.activity.dataType.MutableInteger;
import com.example.administrator.my500.activity.manager.PhotoListManager;
import com.example.administrator.my500.activity.view.PhotoListItem;

public class PhotoListAdapter extends BaseAdapter {

    PhotoItemCollectionDao dao;
   MutableInteger lastPositionInteger;

    public PhotoListAdapter(MutableInteger lastPositionInteger) {
        this.lastPositionInteger = lastPositionInteger;
    }

    public PhotoItemCollectionDao getDao() {
        return dao;
    }

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public int getCount() {
        //เปลี่ยนเปง return 1 เพราะ จะใส่ progress bar เข้าไป
//        if (dao == null) {
//            return 0;
//        }
//        if (dao.getData() == null) {
//            return 0;
//        }
//
//        return dao.getData().size();

        if (dao == null) {
            return 1;
        }
        if (dao.getData() == null) {
            return 1;
        }

        return dao.getData().size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return dao.getData().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    // สมมุติเพื่อให้ใช้ ListView เพียง 1 ตัว กับ View หลายประเภท
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (getItemViewType(position) == 0) {
//            PhotoListItem item;
//            if (convertView != null) {
//                item = (PhotoListItem) convertView;
//            } else {
//                item = new PhotoListItem(parent.getContext());
//            }
//            return item;
//        }else{
//            TextView item;
//            if (convertView != null) {
//                item = (TextView) convertView;
//            } else {
//                item = new TextView(parent.getContext());
//            }
//            item.setText("Position " + position);
//            return item;
//        }
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        //return ค่า max ที่เปงไปได้ของจำนวน View ใน ListView ในที่นี่สมมุติว่ามี 2 คือ TextView PhotoListItem
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        //return ค่าตั้งแต่ 0 ถึง getViewTypeCount()-1 ในที่นี่ก็เรยได้ 0-(2-1)= 0-1
//        return position % 2 == 0 ? 0:1;
//    }


    @Override
    public int getViewTypeCount() {
        //return ค่า max ที่เปงไปได้ของจำนวน View ใน ListView ในที่นีี 2 คือ ProgressBar PhotoListItem
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        //return ค่าตั้งแต่ 0 ถึง getViewTypeCount()-1 ในที่นี่ก็เรยได้ 0 ถึง (2-1) = 0ถ ึง 1
        return position == getCount()-1 ? 0 : 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1) {
            ProgressBar item;
            if (convertView != null) {
                item = (ProgressBar) convertView;
            } else {
                item = new ProgressBar(parent.getContext());
            }
            return item;
        }
        PhotoListItem item;
        if (convertView != null) {
            item = (PhotoListItem) convertView;
        } else {
            item = new PhotoListItem(parent.getContext());
        }
        PhotoItemDao dao = (PhotoItemDao) getItem(position);
        item.setTvName(dao.getCaption());
        item.setTvDescription(dao.getUsername() + "\n" + dao.getCamera());
        item.setIvImg(dao.getImageUrl());
        if (lastPositionInteger.getValue() < position) {
            Animation animation = AnimationUtils.loadAnimation(parent.getContext(), R.anim.up_from_bottom);
            item.startAnimation(animation);
            lastPositionInteger.setValue(position);
        }
        return item;

    }

    public void increaseLastPosition(int amount) {
        lastPositionInteger.setValue(lastPositionInteger.getValue()+amount);

    }
}

