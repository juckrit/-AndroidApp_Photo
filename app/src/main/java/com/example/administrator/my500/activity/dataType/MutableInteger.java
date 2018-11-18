package com.example.administrator.my500.activity.dataType;


import android.os.Bundle;

//สร้าง คลาสนี้มาเพื่อเก็บ lastPosition จาก Adapter เราไม่สามารถเก็บที่ Adapter ได้ เรยสร้างคลาสนี้มาเก็บโดยเฉพาะ
public class MutableInteger {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public MutableInteger(int value) {
        this.value = value;
    }

    public Bundle onSavedInstanceState(){
       Bundle bundle = new Bundle();
       bundle.putInt("value",value);
       return bundle;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState){
        value = savedInstanceState.getInt("value");
    }
}
