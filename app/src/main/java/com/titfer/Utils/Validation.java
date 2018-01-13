package com.titfer.Utils;

import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by Moza on 26-Jul-17.
 */

public class Validation {


    static  public boolean isEditTextEmpty(EditText editText){
        if (editText.getText().toString().isEmpty())
            return true;
        else
            return false;
    }

    static public boolean validateName(String name){
        if (!name.matches("^[a-zA-Z]+$"))
            return false;
        else
            return true;
    }

    static public boolean validateEmail(String email){
        if (!email.matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"))
            return false;
        else
            return true;
    }

    static public boolean validatePhone(String phone){
        if (!phone.matches("^[+\\d{3}]?\\d{11,20}$"))
            return false;
        else
            return true;
    }

    static public boolean validateCheckBox(CheckBox checkBox){
        if (checkBox.isChecked())
            return true;
        else
            return false;
    }

}
