package com.example.foodification;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;

import androidx.core.content.res.ResourcesCompat;

public class ProgressBarClass {
    private static final ProgressBarClass ourInstance = new ProgressBarClass();
    public Context context;
    private ProgressDialog progressDialog;

    public ProgressBarClass() {
    }

    public static ProgressBarClass getInstance() {
        return ourInstance;
    }

    public void showProgress(Context context) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                return;
            }
            progressDialog = new ProgressDialog(context);
//            BaseActivity.progressDialog = progressDialog;
//            progressDialog.setMessage(Config.typeface(context, context.getString(R.string.please_wait)));
            progressDialog.setMessage("Please Wait");
//            progressDialog.setTitle(Config.typeface(context, context.getString(R.string.app_name)));
            progressDialog.setProgress(context.getResources().getColor(R.color.teal_200,null));
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void setMessageForProgressBar(String message) {
//        progressDialog.setMessage(Config.typeface(context, message));
//    }

    public void dismissProgress() {
        try {
            if (null != progressDialog && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static SpannableString typeface(Context context, CharSequence chars) {
//        if (chars == null) {
//            return null;
//        }
//        SpannableString s = new SpannableString(chars);
//        s.setSpan(new CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.kollektif)),
//                0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return s;
//    }

}
