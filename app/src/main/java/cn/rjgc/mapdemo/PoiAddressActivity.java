package cn.rjgc.mapdemo;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.rjgc.mapdemo.bean.POI;
import cn.rjgc.mapdemo.databinding.ActivityPoiAddressBinding;
import cn.rjgc.mapdemo.utils.RecyclerViewAdapter;

public class PoiAddressActivity extends AppCompatActivity implements
        PoiSearch.OnPoiSearchListener, SwipeRefreshLayout.OnRefreshListener {

    private PoiSearch poiSearch;
    private PoiSearch.Query mQuery = null;
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private List<PoiItem> poiItemList;// poi数据

    RecyclerViewAdapter mAdapter;
    private List<POI> mAdapterDatas = new ArrayList<>();


    private LatLonPoint lp = null;
    private ActivityPoiAddressBinding poiBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        poiBinding = DataBindingUtil.setContentView(this, R.layout.activity_poi_address);

        Double latitude = getIntent().getDoubleExtra("latitude", 0);
        Double longitude = getIntent().getDoubleExtra("longitude", 0);
        if (latitude != 0 && latitude != null) {
            lp = new LatLonPoint(latitude, longitude);
            poiBinding.errorTv.setVisibility(View.GONE);
            doSearchQuery();//开始搜索
        } else {
            poiBinding.errorTv.setText("定位失败，无法获取地址");
            poiBinding.errorTv.setVisibility(View.VISIBLE);
        }


    }

    //开始进行poi搜索
    private void doSearchQuery() {
        currentPage = 0;
        mQuery = new PoiSearch.Query("", "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        mQuery.setPageSize(20);// 设置每页最多返回多少条poiitem
        mQuery.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, mQuery);
            poiSearch.setOnPoiSearchListener(this);
            //设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(mQuery)) {// 是否是同一条
                    poiResult = result;
                    poiItemList = poiResult.getPois();
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItemList != null && poiItemList.size() > 0) {
                        for (PoiItem item : poiItemList) {
                            POI poi = new POI();
                            poi.address.set(item.getTitle());
                            poi.addrDetails.set(item.getSnippet());
                            poi.isSelected.set(false);

                            mAdapterDatas.add(poi);
                        }

                        setAdapter();
                    }
                }

            }
        }
    }

    private void setAdapter() {
        mAdapter = new RecyclerViewAdapter(this, mAdapterDatas);
        poiBinding.recyclerview.setHasFixedSize(true);
        //设置RecyclerView的布局管理
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        poiBinding.recyclerview.setLayoutManager(linearLayoutManager);
        poiBinding.recyclerview.setItemAnimator(new DefaultItemAnimator());//给RecyclerView添加默认的动画效果
        poiBinding.recyclerview.setAdapter(mAdapter);
        //设置RecyclerView的分隔线
        poiBinding.recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //设置下拉刷新样式
        setSwipeRefresh();

        mAdapter.setmOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapter.changeSelected(position);
                Toast.makeText(PoiAddressActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setmOnItemLongClickListener(new RecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(PoiAddressActivity.this, "已删除\n" + mAdapterDatas.get(position).address.get(), Toast.LENGTH_SHORT).show();
                mAdapter.deleteData(position);
            }
        });
    }

    private void setSwipeRefresh() {
        //设置下拉出现小圆圈是否是缩放出现，出现的位置，最大的下拉位置
        poiBinding.swipeRefresh.setProgressViewOffset(true, 50, 200);
        //设置下拉圆圈的大小，两个值 LARGE， DEFAULT
        poiBinding.swipeRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        //设定下拉圆圈的背景
        poiBinding.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorYellow);

        //设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        poiBinding.swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        poiBinding.swipeRefresh.setOnRefreshListener(this);
        // 通过 setEnabled(false) 禁用下拉刷新
//        poiBinding.swipeRefresh.setEnabled(false);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                POI poi = new POI();
                poi.address.set("test");
                poi.addrDetails.set("hello");
                poi.isSelected.set(false);

                mAdapter.addData(0,poi);
//                mAdapter.notifyDataSetChanged();
                //停止刷新动画
                poiBinding.swipeRefresh.setRefreshing(false);
            }
        }, 3000);
    }
}
