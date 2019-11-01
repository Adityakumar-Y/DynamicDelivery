package com.example.dynamicdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SplitInstallStateUpdatedListener {

    private static final String TAG = "MainActivity";
    private SplitInstallManager splitInstallManager;
    private SplitInstallRequest request;
    private int sessionId;

    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnDownload)
    Button btnDownload;
    @BindView(R.id.btnUninstall)
    Button btnUninstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnStart)
    public void start(){
        if(splitInstallManager.getInstalledModules().contains(getString(R.string.module_dashboard))){
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), "com.example.dashboard.DashboardActivity");
            startActivity(intent);

        }else{
            Toast.makeText(this, "Module not present !!", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnUninstall)
    public void uninstall(){
        if(splitInstallManager.getInstalledModules().contains(getString(R.string.module_dashboard))){
           splitInstallManager.deferredUninstall(Arrays.asList(getString(R.string.module_dashboard)));
            Toast.makeText(this, "Dashboard Module Uninstalled !!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Module not present !!", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnDownload)
    public void download(){
        if(!splitInstallManager.getInstalledModules().contains(getString(R.string.module_dashboard))){
            splitInstallManager = SplitInstallManagerFactory.create(this);
            request = SplitInstallRequest
                    .newBuilder()
                    .addModule(getString(R.string.module_dashboard))
                    .build();

            splitInstallManager.registerListener(this);
            splitInstallManager.startInstall(request)
                    .addOnSuccessListener(result -> sessionId = result)
                    .addOnFailureListener(e -> Log.d(TAG, "download: Error "+ e.getMessage()));
        }else{
            Toast.makeText(this, "Already Installed !!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStateUpdate(SplitInstallSessionState state) {
        if(state.sessionId() == sessionId){
            switch (state.status()){
                case SplitInstallSessionStatus.INSTALLED:
                    Toast.makeText(this, "Dashboard Module Downloaded !!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
