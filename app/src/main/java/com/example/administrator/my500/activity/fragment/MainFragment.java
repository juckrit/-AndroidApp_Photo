package com.example.administrator.my500.activity.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.my500.R;
import com.example.administrator.my500.activity.adapter.PhotoListAdapter;
import com.example.administrator.my500.activity.dao.PhotoItemCollectionDao;
import com.example.administrator.my500.activity.dao.PhotoItemDao;
import com.example.administrator.my500.activity.dataType.MutableInteger;
import com.example.administrator.my500.activity.manager.HttpManager;
import com.example.administrator.my500.activity.manager.PhotoListManager;
import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

import java.io.IOException;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by nuuneoi on 11/16/2014.
 */
public class MainFragment extends Fragment {

    // Variable
    ListView listView;
    PhotoListAdapter photoListAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    PhotoListManager photoListManager;
    Button btnNewPhoto;
    MutableInteger lastPositionInteger;
    private boolean isLoadingMore = false;

    public interface FragmentListener {
        void onPhotoItemClicked(PhotoItemDao dao);
    }

    /********************
     * Method
     *********************/
    public MainFragment() {
        super();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        init(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore
            onRestoreInstanceState(savedInstanceState);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        //Initialize Fragment level's Component
        //ใส่ตัวแปรระดับ fragment คือ พวกตัวแปรที่ไม่อยู่เกี่ยวกับ View
        photoListManager = new PhotoListManager();
        lastPositionInteger = new MutableInteger(-1);

//        File dir = getContext().getFilesDir();
//        File dir = getContext().getDir("DirectoryName",Context.MODE_PRIVATE);
//        File dir = getContext().getCacheDir();
//        Log.i("dir", String.valueOf(dir));
//        File file = new File(dir, "FileName");
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(file);
//            fos.write("hello".getBytes());
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initInstances(View rootView, Bundle savedInstanceState) {
        //เก็บตัวแปรระดับ view
        btnNewPhoto = (Button) rootView.findViewById(R.id.btnNewPhoto);

        btnNewPhoto.setOnClickListener(buttonClickListener);
        //ไม่ประกาศไว้ตรงนี้แล้วเพราะ เปนตัวแปรที่ไว้เก็บข้อมูล และอีกอย่างคือถ้า view ตายไปแล้ว เราควรได้ใช้ข้อมูลต่อเมื่อ view ถูกสร้างมาใหม่ เรยเอาไปใส่ไว้ใน onCreate
        //photoListManager = new PhotoListManager();
        // Init 'View' instance(s) with rootView.findViewById here
        listView = (ListView) rootView.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(pullToRefresh);

        photoListAdapter = new PhotoListAdapter(lastPositionInteger);
        photoListAdapter.setDao(photoListManager.getDao()); //restore  มาใช้ใหม่
        listView.setAdapter(photoListAdapter);

        listView.setOnScrollListener(listviewScrollListerner);

        listView.setOnItemClickListener(listViewItemClinkListenr);
        if (savedInstanceState == null) {

            refreshData();
        }
    }

    private void refreshData() {
        if (photoListManager.getCount() == 0) {
            reloadData();
        } else {
            reloadDataNewer();
        }
    }

    private void reloadDataNewer() {
        int maxId = photoListManager.getMaximunId();
        Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getApiService().loadPhotoListAfterId(maxId);
        //ห้ามใช้  call.execute(); เพราะมันเปงประเภท Syncronus คือ ต้องโหลดข้อมูลจาก server จนครบถึงจะทำงานบรรทัดถัดไป ยางทีนานเปง 10 นาที ทำให้แอปค้าง user จะงง
        call.enqueue(new PhotoListLoadCallBack(PhotoListLoadCallBack.MODE_RELOAD_NEWWER));
    }

    private void loadMoreData() {
        if (isLoadingMore) {
            return;
        }
        isLoadingMore = true;
        int minId = photoListManager.getMinmunId();
        retrofit2.Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getApiService().loadPhotoListBeforeId(minId);
        //ห้ามใช้  call.execute(); เพราะมันเปงประเภท Syncronus คือ ต้องโหลดข้อมูลจาก server จนครบถึงจะทำงานบรรทัดถัดไป ยางทีนานเปง 10 นาที ทำให้แอปค้าง user จะงง
        call.enqueue(new PhotoListLoadCallBack(PhotoListLoadCallBack.MODE_LOAD_MORE));
    }

    private void reloadData() {
        retrofit2.Call<PhotoItemCollectionDao> call = HttpManager.getInstance().getApiService().loadPhotoList();
        //ห้ามใช้  call.execute(); เพราะมันเปงประเภท Syncronus คือ ต้องโหลดข้อมูลจาก server จนครบถึงจะทำงานบรรทัดถัดไป ยางทีนานเปง 10 นาที ทำให้แอปค้าง user จะงง
        call.enqueue(new PhotoListLoadCallBack(PhotoListLoadCallBack.MODE_RELOAD));
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
        outState.putBundle("photoListManager", photoListManager.onSavedInstanceState());
        outState.putBundle("lastPositionInteger", lastPositionInteger.onSavedInstanceState());
    }

    private void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore Instance State here
            photoListManager.onRestoreInstanceState(savedInstanceState.getBundle("photoListManager"));
            lastPositionInteger.onRestoreInstanceState(savedInstanceState.getBundle("lastPositionInteger"));
        }
    }

    /*
     * Restore Instance State Here
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private void showButtonNewPhoto() {
        btnNewPhoto.setVisibility(View.VISIBLE);
        //ใช้ Application Context เพราะเราเรียก method นี่ที่  onResponse ซึ่งมันเปง Asynchronus ตอนนั้น Activity อาจถูกทำลายไปแล้ว ทำให้ไม่มี context เรยใช้ Application Context
        Animation anim = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(), R.anim.zoom_fade_in);
        btnNewPhoto.startAnimation(anim);
    }

    private void hideButtonNewPhoto() {
        btnNewPhoto.setVisibility(View.GONE);
        //ใช้ Application Context เพราะเราเรียก method นี่ที่  onResponse ซึ่งมันเปง Asynchronus ตอนนั้น Activity อาจถูกทำลายไปแล้ว ทำให้ไม่มี context เรยใช้ Application Context
        Animation anim = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(), R.anim.zoom_fade_out);
        btnNewPhoto.startAnimation(anim);

    }

    private void showToast(String text) {
        Toast.makeText(Contextor.getInstance().getContext(), text, Toast.LENGTH_SHORT).show();

    }


    /*************************
     * Listener Zone
     *************************/
    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnNewPhoto) {
                hideButtonNewPhoto();
                listView.smoothScrollToPosition(0);
            }
        }
    };

    SwipeRefreshLayout.OnRefreshListener pullToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            refreshData();

        }
    };

    AbsListView.OnScrollListener listviewScrollListerner = new AbsListView.OnScrollListener() {


        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (view == listView) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    if (photoListManager.getCount() > 0) {
                        //Load More
                        loadMoreData();
                    }
                }
            }

        }
    };

    AdapterView.OnItemClickListener listViewItemClinkListenr = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // มีโอกาสที่ progressBar ที่เราสร้างจะโดน คลิก ในกรณีที่ไม่มีข้อมูล เรยต้อง check
            if (position < photoListManager.getCount()) {
                showToast(String.valueOf(position));
                PhotoItemDao dao = photoListManager.getDao().getData().get(position);
                FragmentListener listener = (FragmentListener) getActivity();
                listener.onPhotoItemClicked(dao);
            }

        }
    };

    /**********************
     * Inner Class
     **********************/

    class PhotoListLoadCallBack implements Callback<PhotoItemCollectionDao> {
        public static final int MODE_RELOAD = 1;
        public static final int MODE_RELOAD_NEWWER = 2;
        public static final int MODE_LOAD_MORE = 3;
        int mode;

        public PhotoListLoadCallBack(int mode) {
            this.mode = mode;
        }

        @Override
        public void onResponse(retrofit2.Call<PhotoItemCollectionDao> call, Response<PhotoItemCollectionDao> response) {
            swipeRefreshLayout.setRefreshing(false);
            if (response.isSuccessful()) {
                PhotoItemCollectionDao dao = response.body();

                //ให้ update แล้วยังอยู่ที่เดิม
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                View c = listView.getChildAt(firstVisiblePosition);
                int top;
                if (c == null) {
                    top = 0;
                } else {
                    top = c.getTop();
                }

                if (mode == MODE_RELOAD_NEWWER) {
                    photoListManager.insertDaoAtTopPosition(dao);
                } else if (mode == MODE_LOAD_MORE) {
                    photoListManager.appendDaoAtBottomPosition(dao);
                    clearLoadingMoreFlagIfCapable(mode);


                } else {
                    photoListManager.setDao(dao);

                }
                //PhotoListManager.getInstance().setDao(dao);
                photoListAdapter.setDao(photoListManager.getDao());
                photoListAdapter.notifyDataSetChanged();

                if (mode == MODE_RELOAD_NEWWER) {
                    int additionalSize;
                    if (dao.getData() != null && dao != null) {
                        additionalSize = dao.getData().size();
                    } else {
                        additionalSize = 0;
                    }
                    photoListAdapter.increaseLastPosition(additionalSize);
                    listView.setSelectionFromTop(firstVisiblePosition + additionalSize, top);
                    if (dao.getData().size() > 0) {
                        showButtonNewPhoto();
                    }
                } else {

                }
                //ในชุดคำสั่งที่เปง Asynchonous ห้ามใช้ Activity context
                // ต้องเปลี่ยน เพราะ ถ้าเปิดแอปแล้วปิดทันที่ แอปพัง เพราะตอนกดคำสั่งติดต่อserver ยังรันอยู่ พอกดปิด activity ตาย มันเรย getActivity() มาไม่ได้
//                    Toast.makeText(getActivity(), dao.getData().get(0).getCaption(), Toast.LENGTH_SHORT).show();
                showToast("Load Completed");
            } else {
                clearLoadingMoreFlagIfCapable(mode);

                try {
                    showToast(response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(retrofit2.Call<PhotoItemCollectionDao> call, Throwable t) {
            clearLoadingMoreFlagIfCapable(mode);
            swipeRefreshLayout.setRefreshing(false);
            showToast(t.toString());
        }

        private void clearLoadingMoreFlagIfCapable(int mode) {
            if (mode == MODE_LOAD_MORE) {
                isLoadingMore = false;
            }
        }
    }

}
