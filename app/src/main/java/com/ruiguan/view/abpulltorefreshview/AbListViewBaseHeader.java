package com.ruiguan.view.abpulltorefreshview;


import android.content.Context;
import android.widget.LinearLayout;

public class AbListViewBaseHeader extends LinearLayout {

    /**
     * 显示 下拉刷新.
     */
    public final static int STATE_NORMAL = 0;

    /**
     * 显示 松开刷新.
     */
    public final static int STATE_READY = 1;

    /**
     * 显示 正在刷新....
     */
    public final static int STATE_REFRESHING = 2;

    /**
     * 上下文.
     */
    public Context context;

    /**
     * 当前状态.
     */
    public int currentState = -1;

    /**
     * 初始化Header.
     *
     * @param context the context
     */
    public AbListViewBaseHeader(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 设置状态.
     *
     * @param state the new state
     */
    public void setState(int state) {

        //状态未变化
        if (state == currentState) {
            return;
        }
        switch (state) {
            case STATE_NORMAL:
                break;
            case STATE_READY:
                break;
            case STATE_REFRESHING:
                break;
            default:
        }
        currentState = state;
    }
    public int getState() {
        return currentState;
    }

}
