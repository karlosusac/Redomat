package com.redomat.improved.pojo;

import android.app.Activity;
import android.app.ProgressDialog;

import com.redomat.improved.R;

//ProgressDialog class
public class ProgressBar {

    private static ProgressDialog progressDialog;

    //Make new Progress dialog for loading screen
    static public void showProgressDialog(Activity activty){
        progressDialog = new ProgressDialog(activty);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    //Getters
    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    //Setters
    public static void setProgressDialog(ProgressDialog progressDialog) {
        ProgressBar.progressDialog = progressDialog;
    }

    //Close progress dialog
    static public void closeProgressDialog(){
        progressDialog.dismiss();
    }
}
