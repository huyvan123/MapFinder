package com.example.hp.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PermissionAccess extends AsyncTask<Context ,Void, Boolean>{
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


//    public static boolean getLocationPerission(Context context, Activity activity){
//        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            return true;
//        }else{
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            return false;
//        }
//    }

    public static boolean checkInternetConnection(Context context) throws UnknownHostException {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getActiveNetworkInfo() != null){
            InetAddress inetAddress = InetAddress.getByName("google.com");
            if(!inetAddress.equals("")){
                return true;
            }
        }
        return false;
    }

    @Override
    protected Boolean doInBackground(Context... contexts) {
        try {
            return checkInternetConnection(contexts[0]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }
}
