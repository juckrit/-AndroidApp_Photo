package com.example.administrator.my500.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.my500.R;
import com.example.administrator.my500.activity.configulation.GlideApp;
import com.example.administrator.my500.activity.dao.PhotoItemDao;

import org.w3c.dom.Text;


/**
 * Created by nuuneoi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class PhotoSummaryFragment extends Fragment {

    PhotoItemDao dao;
    ImageView ivImg;
    TextView tvName;
    TextView tvDescription;

    public PhotoSummaryFragment() {
        super();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_more_info,menu);
        //หลักการคือ Activity มันจะสร้างเมนูตัวมันเอง (ถ้ามีการสร้าง) และมันจะมาถาม fragment ว่ามีการสร้างมั้ย ถ้ามีมันก็จะสร้างรวมกัน
        //พอสร้างเสร็จ menu ของ fragment จะไม่แสดง จนกว่าจะไปประกาศ setHasOptionsMenu(true); ใน init ของ Fragment
        MenuItem menuItem = (MenuItem)menu.findItem(R.id.action_action);
        ShareActionProvider shareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(menuItem);
        shareActionProvider.setShareIntent(getSharedIntent());
    }

    private Intent getSharedIntent(){
        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject");
//        intent.putExtra(Intent.EXTRA_TEXT,"Text");
        intent.setType("image/jpeg");
        return  intent;


    }

    @SuppressWarnings("unused")
    public static PhotoSummaryFragment newInstance(PhotoItemDao dao) {
        PhotoSummaryFragment fragment = new PhotoSummaryFragment();
        Bundle args = new Bundle();
        args.putParcelable("dao",dao);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        dao = getArguments().getParcelable("dao");
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_summary, container, false);

        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        setHasOptionsMenu(true);
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        tvDescription = (TextView)rootView.findViewById(R.id.tvDescription);
        tvName = (TextView)rootView.findViewById(R.id.tvName);
        ivImg = (ImageView)rootView.findViewById(R.id.ivImg);

        tvName.setText(dao.getCaption());
        tvDescription.setText(dao.getUsername() + "\n" + dao.getCamera());
        GlideApp.with(PhotoSummaryFragment.this)
                .load(dao.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivImg);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

}
