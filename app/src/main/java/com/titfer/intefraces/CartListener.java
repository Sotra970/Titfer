package com.titfer.intefraces;

import android.view.View;

import com.titfer.Models.CartModel;

/**
 * Created by sotra on 1/1/2017.
 */
public interface CartListener {
    void onHolderClicked(View view, CartModel child);
    void onConfirm(View view, CartModel child);
    void onCancel(View view, CartModel child);
}
