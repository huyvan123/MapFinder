package arcore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.hp.myapplication.R;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

public class MyArActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 2;
    private Button mArButton;
    private boolean mUserRequestedInstall = true;
    private Session mSession;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_fragment);
        mArButton = findViewById(R.id.btn_01);
        getCamerePermission();
    }


    void maybeEnableArButton() {
        // Likely called from Activity.onCreate() of an activity with AR buttons.
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // re-query at 5Hz while we check compatibility.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            mArButton.setVisibility(View.VISIBLE);
            mArButton.setEnabled(true);
            // indicator on the button.
        } else { // unsupported or unknown
            mArButton.setVisibility(View.INVISIBLE);
            mArButton.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mSession == null) {
                switch (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                    case INSTALLED:
                        mSession = new Session(this);
                        System.out.println("ar success");
                        break;
                    case INSTALL_REQUESTED:
                        System.out.println("need to required");
                        mUserRequestedInstall = false;
                        return;
                }
            }
        } catch (UnavailableApkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableDeviceNotCompatibleException e) {
            e.printStackTrace();
        } catch (UnavailableUserDeclinedInstallationException e) {
            e.printStackTrace();
        } catch (UnavailableArcoreNotInstalledException e) {
            e.printStackTrace();
        } catch (UnavailableSdkTooOldException e) {
            e.printStackTrace();
        }
    }

    private void getCamerePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            maybeEnableArButton();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_CAMERA:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getCamerePermission();
                }
                break;
            default:
                System.out.println("ar permisson deni");
        }
    }
}
