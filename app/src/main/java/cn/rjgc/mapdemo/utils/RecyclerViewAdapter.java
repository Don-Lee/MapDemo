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
import cn.rjgc.mapdemo.databinding.RecyclerviewItemBinding;

/**
 * Created by Don on 2017/7/17.
 */
//TODO 点击后出现选中的图片 http://blog.csdn.net/zxt0601/article/details/52703280
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>
implements View.OnClickListener, View.OnLongClickListener{

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
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerviewItemBinding binding = RecyclerviewItemBinding.inflate(inflater, parent, false);
        binding.getRoot().setOnClickListener(this);//监听点击事件
        binding.getRoot().setOnLongClickListener(this);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.bindDatas(datas.get(position));

        holder.binding.getRoot().setTag(holder.getLayoutPosition());//方便在别的地方调用


    }

    @Override
    public int getItemCount() {
        int result = 0;
        if (datas != null) {
            result = datas.size();
        }
        return result;
    }

    public void deleteData(int position){
        datas.remove(position);
        notifyDataSetChanged();
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
            Log.e("TAG", "bindDatas: "+poi.isSelected.get()+"--"+poi.address.get());
            if (poi.isSelected.get()) {
                binding.selectedImg.setVisibility(View.VISIBLE);
            } else {
                binding.selectedImg.setVisibility(View.GONE);
            }
            binding.executePendingBindings();
        }
    }

}
