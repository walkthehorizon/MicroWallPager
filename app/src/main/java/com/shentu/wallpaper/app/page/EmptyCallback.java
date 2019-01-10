package com.shentu.wallpaper.app.page;

import com.kingja.loadsir.callback.Callback;
import com.shentu.wallpaper.R;


public class EmptyCallback extends Callback {

    private int emptyResId;


    public EmptyCallback(int emptyResId) {
        this.emptyResId = emptyResId;
    }

    public EmptyCallback() {
        this.emptyResId = R.layout.layout_default_empty;
    }

    @Override
    protected int onCreateView() {
        return emptyResId;
    }

}
