package com.example.administrator.my500.activity.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.administrator.my500.R;
import com.example.administrator.my500.activity.dao.PhotoItemDao;
import com.example.administrator.my500.activity.fragment.MainFragment;
import com.example.administrator.my500.activity.fragment.MoreInfoFragment;

public class MoreInfoActivity extends AppCompatActivity {


    //comment เพราะจะไปสร้างไว้ใน Fragment
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        MenuInflater menuInflater = new MenuInflater(this);
////        menuInflater.inflate(R.menu.menu_more_info,menu);
//        getMenuInflater().inflate(R.menu.menu_more_info,menu);
//        return true;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);


        PhotoItemDao dao = getIntent().getParcelableExtra("dao"); // ได้ object มาแล้วต้องโยนต่อให้ MoreInfoFragment.newInstance(dao)

        initInstance();
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.contentContainer2,MoreInfoFragment.newInstance(dao))
                    .commit();
        }
    }

    private void initInstance() {
        //create home button
        getSupportActionBar().setHomeButtonEnabled(true);
        //Change home button to Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //get up button then handle event in onOptionsItemSelected with specific ID android.R.id.home

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){ //Unique id not created
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
