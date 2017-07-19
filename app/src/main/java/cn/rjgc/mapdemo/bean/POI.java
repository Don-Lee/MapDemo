package cn.rjgc.mapdemo.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import cn.rjgc.mapdemo.BR;

/**
 * Created by Don on 2017/7/17.
 */

public class POI extends BaseObservable {

    //Data Binding 更新UI,Reference -> https://appkfz.com/2015/07/09/android-data-binding-3/
    public ObservableField<String> address = new ObservableField<>();
    public ObservableField<String> addrDetails = new ObservableField<>();
    public ObservableBoolean isSelected = new ObservableBoolean();

}
