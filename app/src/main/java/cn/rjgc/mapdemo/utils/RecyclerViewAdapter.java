package cn.rjgc.mapdemo.utils;

import android.content.Context;
import android.databinding.adapters.AdapterViewBindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.rjgc.mapdemo.bean.POI;
import cn.rjgc.mapdemo.databinding.LayoutFooterviewRvBinding;
import cn.rjgc.mapdemo.databinding.RecyclerviewItemBinding;

/**
 * Created by Don on 2017/7/17.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter
implements View.OnClickListener, View.OnLongClickListener{

    private int myFooterView = 1;
    private int myViewHoler = 0;
    int load_more_status ;
    public static final int PULLUP_LOAD_MORE = 0;
    public static final int LOADING_MORE = 1;
    public static final int NO_MORE = 2;

    private Context mContext;
    private List<POI> datas;
    private LayoutInflater inflater;

    private int mSelectedPos = -1;//实现单选  变量保存当前选中的position

    public RecyclerViewAdapter(Context context, List<POI> datas) {
        this.mContext = context;
        this.datas = datas;

        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).isSelected.get()) {
                mSelectedPos = i;
            }
        }
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == myViewHoler) {
            RecyclerviewItemBinding binding = RecyclerviewItemBinding.inflate(inflater, parent, false);
            binding.getRoot().setOnClickListener(this);//监听点击事件
            binding.getRoot().setOnLongClickListener(this);
            return new MyViewHolder(binding);
        } else if (viewType == myFooterView) {
            LayoutFooterviewRvBinding footerviewRvBinding = LayoutFooterviewRvBinding.inflate(inflater, parent, false);
            return new MyFooterView(footerviewRvBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).bindDatas(datas.get(position));

            ((MyViewHolder) holder).binding.getRoot().setTag(holder.getLayoutPosition());//方便在别的地方调用
        } else {
            MyFooterView footerView = (MyFooterView) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footerView.progressVisible(0);
                    footerView.changeText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerView.progressVisible(1);
                    footerView.changeText("正在加载更多数据...");
                    break;
                case NO_MORE:
                    footerView.progressVisible(0);
                    footerView.changeText("没有更多数据");
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        int result = 0;
        if (datas != null) {
            result = datas.size() + 1;//此处+1 是因为增加了上拉加载更多栏目
        }
        return result;
    }

    public void addData(int position, POI poi) {
        datas.add(position, poi);
        notifyDataSetChanged();
//        notifyItemInserted(position);  //此方法定向更新
    }
    public void deleteData(int position){
        datas.remove(position);
        notifyDataSetChanged();
        if (position < mSelectedPos) {
            mSelectedPos -= 1;
        }
//        notifyItemRemoved(position);  //此方法虽然也更新了recyclerview,但是item的索引没有更新
    }

    public void changeSelected(int position){
        if (position != mSelectedPos) {
            if (mSelectedPos != -1) {
                //先取消上个item的勾选状态
                datas.get(mSelectedPos).isSelected.set(false);
                notifyItemChanged(mSelectedPos);
            }
            //设置新Item的勾选状态
            mSelectedPos = position;
            datas.get(mSelectedPos).isSelected.set(true);
            notifyItemChanged(mSelectedPos);
        }
    }

    public void changeFooterViewState(int status)
    {
        load_more_status = status;
        notifyDataSetChanged();
    }
    public void addMoreItem(List<POI> data) {
        int preSize = datas.size();
        if (data != null && data.size() > 0) {
            datas.addAll(data);
            notifyItemRangeInserted(preSize,datas.size());
        }
        notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            int position = (int) view.getTag();
            mOnItemClickListener.onItemClick(view, position);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(view, ((int) view.getTag()));
        }
        return true;//返回true是为了终止事件得传递，否则的话还会触发onClick事件
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return myFooterView;
        } else {
            return myViewHoler;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);

    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setmOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private RecyclerviewItemBinding binding = null;
        public MyViewHolder(RecyclerviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindDatas(POI poi) {
            binding.setAddress(poi);
            if (poi.isSelected.get()) {
                binding.selectedImg.setVisibility(View.VISIBLE);
            } else {
                binding.selectedImg.setVisibility(View.GONE);
            }
            binding.executePendingBindings();
        }
    }

    static class MyFooterView extends RecyclerView.ViewHolder{

        private LayoutFooterviewRvBinding footerviewRvBinding;
        public MyFooterView(LayoutFooterviewRvBinding footerviewRvBinding) {
            super(footerviewRvBinding.getRoot());
            this.footerviewRvBinding = footerviewRvBinding;
        }

        public void changeText(String s) {
            footerviewRvBinding.loadmoreTv.setText(s);
        }

        public void progressVisible(int i){
            if (i == 1) {
                footerviewRvBinding.progressBar.setVisibility(View.VISIBLE);
            }else {
                footerviewRvBinding.progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

}
