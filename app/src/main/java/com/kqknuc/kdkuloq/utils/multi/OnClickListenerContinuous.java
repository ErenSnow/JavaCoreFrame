package com.kqknuc.kdkuloq.utils.multi;

import android.view.View;


/**
 * 防止连点
 */
public abstract class OnClickListenerContinuous implements View.OnClickListener {

    @Override
    public final void onClick(final View v) {
        ClickContinuousHelper.onlyFirstSameView(v, new ClickContinuousHelper.Callback() {
            @Override
            public void onClick(View view) {
                onClickContinuous(view);
            }
        });
    }

    public abstract void onClickContinuous(View v);
}
