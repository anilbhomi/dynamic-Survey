package com.rayoforms.survey.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by anil on 4/16/18.
 */

public class ToastManager {
    public static void ShowToast(Context context,String message,Boolean toastLength){
        if(toastLength){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();;
        }
    }
}
