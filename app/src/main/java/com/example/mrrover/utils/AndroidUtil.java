package com.example.mrrover.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mrrover.model.DriverHistoryModel;
import com.example.mrrover.model.UserModel;
import com.google.firebase.firestore.auth.User;

public class AndroidUtil {

    public static void showToast(Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();

    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model){

        intent.putExtra("firstname",model.getFirstname());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
    }

    public static void passUserModelAsIntent1(Intent intent, DriverHistoryModel model){

        intent.putExtra("firstname",model.getDriverName());
        //intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getdriverUID());
    }

    public static UserModel getUserModelFromIntent(Intent intent){

        UserModel userModel = new UserModel();
        userModel.setFirstname(intent.getStringExtra("firstname"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        return userModel;
    }
}
