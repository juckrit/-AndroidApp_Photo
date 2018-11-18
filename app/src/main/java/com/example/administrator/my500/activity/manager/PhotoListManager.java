package com.example.administrator.my500.activity.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.administrator.my500.activity.dao.PhotoItemCollectionDao;
import com.example.administrator.my500.activity.dao.PhotoItemDao;
import com.google.gson.Gson;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import java.util.ArrayList;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class PhotoListManager {


    private Context mContext;

    public PhotoListManager() {
        mContext = Contextor.getInstance().getContext();
        loadCache();
    }

    private PhotoItemCollectionDao dao;

    public PhotoItemCollectionDao getDao() {
        return dao;
    }

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
        saveCache();
    }

    public void insertDaoAtTopPosition(PhotoItemCollectionDao newDao) {
        if (dao == null) {
            dao = new PhotoItemCollectionDao();
        }
        if (dao.getData() == null) {
            dao.setData(new ArrayList<PhotoItemDao>());
        }
        dao.getData().addAll(0, newDao.getData());
        saveCache();

    }

    public void appendDaoAtBottomPosition(PhotoItemCollectionDao newDao) {
        if (dao == null) {
            dao = new PhotoItemCollectionDao();
        }
        if (dao.getData() == null) {
            dao.setData(new ArrayList<PhotoItemDao>());
        }
        dao.getData().addAll(dao.getData().size(), newDao.getData());
        saveCache();

    }

    public int getMaximunId() {
        if (dao == null) {
            return 0;
        }
        if (dao.getData() == null) {
            return 0;
        }
        if (dao.getData().size() == 0) {
            return 0;
        }
        int maxId = dao.getData().get(0).getId();
        for (int i = 0; i < dao.getData().size(); i++) {

            maxId = Math.max(maxId, dao.getData().get(i).getId());

        }
        return maxId;

    }

    public int getMinmunId() {
        if (dao == null) {
            return 0;
        }
        if (dao.getData() == null) {
            return 0;
        }
        if (dao.getData().size() == 0) {
            return 0;
        }
        int minId = dao.getData().get(0).getId();
        for (int i = 0; i < dao.getData().size(); i++) {

            minId = Math.min(minId, dao.getData().get(i).getId());

        }
        return minId;

    }

    public int getCount() {
        if (dao == null) {
            return 0;
        }
        if (dao.getData() == null) {
            return 0;
        }
        return dao.getData().size();
    }

    public Bundle onSavedInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("dao", dao);
        return bundle;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        dao = savedInstanceState.getParcelable("dao");

    }

    private void saveCache() {
        PhotoItemCollectionDao cacheDoa = new PhotoItemCollectionDao();
        if (dao != null && dao.getData() != null) {
            //เลือก มา 20 ตัว แต่มีโอกาสที่มีไม่ถึง 20 เรย เลือกค่าที่น้อยที่สุดระหว่าง 20 กับ dao.getData().size()
            cacheDoa.setData(dao.getData().subList(0, Math.min(20, dao.getData().size())));
        }
        String json = new Gson().toJson(cacheDoa);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("photos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("json", json);
        editor.apply();

    }

    private void loadCache() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("photos", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("json",null);
        if (json == null){
            return;
        }
        dao = new Gson().fromJson(json,PhotoItemCollectionDao.class);
    }
}
