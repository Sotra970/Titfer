package com.titfer.intefraces;

import android.view.View;

/**
 * Created by sotra on 1/1/2017.
 */
public interface ReportHolderListener {
    void onHolderClicked(View view, Object child);
    void onHolderProplemClicked(String title);
}
