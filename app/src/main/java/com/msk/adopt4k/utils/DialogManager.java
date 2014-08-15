package com.msk.adopt4k.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogManager {
    private Context mContext;

    public DialogManager(Context context)
    {
        mContext = context;
    }

    public void ownedWarning() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("You have already adopted this Omega Zone.");
        alertDialogBuilder.setPositiveButton("OK, I'll choose another.", dismissListener);
        alertDialogBuilder.show();
    }

    public void takenWarning(DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("This Omega Zone is adopted to another base, Do you want to adopt this OZ anyway?.");
        alertDialogBuilder.setPositiveButton("Yes", positiveListener);
        alertDialogBuilder.setNegativeButton("No, I'll choose another.", dismissListener);
        alertDialogBuilder.show();
    }

    public void showNetworkWarning() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Network not connected");
        alertDialogBuilder.setMessage("Your Device is not connected to any Network, Please check 3G/4G or Wifi status.");
        alertDialogBuilder.setPositiveButton("OK", dismissListener);
        alertDialogBuilder.show();
    }

    public void showRequiredField() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Required");
        alertDialogBuilder.setMessage("Your need to provied which Omega zone world ID you choose");
        alertDialogBuilder.setPositiveButton("OK", dismissListener);
        alertDialogBuilder.show();
    }

    public void badOZIDWarning() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Bad World ID");
        alertDialogBuilder.setMessage("You have enterted wrong Omega Zone ID");
        alertDialogBuilder.setPositiveButton("OK", dismissListener);
        alertDialogBuilder.show();
    }

    public void deleteWarning(DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("Are you sure to delete?");
        alertDialogBuilder.setPositiveButton("Yes", positiveListener);
        alertDialogBuilder.setNegativeButton("No", dismissListener);
        alertDialogBuilder.show();
    }

    public void selectTargetYearWarning() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("Please enter target year");
        alertDialogBuilder.setPositiveButton("OK", dismissListener);
        alertDialogBuilder.show();
    }

    public void apiKeyWarning() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Warning");
        alertDialogBuilder.setMessage("User Email or Api Key is invalid.");
        alertDialogBuilder.setPositiveButton("OK", dismissListener);
        alertDialogBuilder.show();
    }

    public void networkError() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Network error");
        alertDialogBuilder.setMessage("Please check you are accessible to internet or have valid user email and api key.");
        alertDialogBuilder.setPositiveButton("OK", dismissListener);
        alertDialogBuilder.show();
    }

    private DialogInterface.OnClickListener dismissListener = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    };

}
