package br.vinicius.moraes.moraes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = "FlashLight";

    private Button mOnBtn;
    private Button mOffBtn;

    private Camera mCamera;
    private SurfaceTexture mPreviewTexture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Atribui o layout
        setContentView(R.layout.activity_main);
        //Recupera o Botão de ON
        mOnBtn = (Button) findViewById(R.id.on_btn);
        // Atribui a função de click
        mOnBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    processOnClick();
                }else{
                    toggleFlashLight(true);
                }
            }
        });
        //Recupera o Botão de OFF
        mOffBtn = (Button) findViewById(R.id.off_btn);
        // Atribui a função de click
        mOffBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    processOffClick();
                }else{
                    toggleFlashLight(false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            // Abre a comunicação com a Camera
            mCamera = Camera.open();
        } catch( Exception e ){
            Log.e(LOG_TAG, "Não foi possivel conectar a Câmera");
        }
    }

    @Override
    protected void onPause() {
        if( mCamera != null ){
            // Libera o uso da Camera
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }

    private void processOffClick(){

        // Desliga a Camera
        if( mCamera != null ){
            mPreviewTexture = new SurfaceTexture(0);
            Parameters params = mCamera.getParameters();
            params.setFlashMode( Parameters.FLASH_MODE_OFF );
            mCamera.setParameters( params );
            try {
                mCamera.setPreviewTexture(mPreviewTexture);
            } catch (IOException ex) {
                // Ignore
            }
            mCamera.startPreview();
        }
    }

    private void processOnClick(){
        // Liga a Camera
        if( mCamera != null ){
            mPreviewTexture = new SurfaceTexture(0);
            Parameters params = mCamera.getParameters();
            params.setFlashMode( Parameters.FLASH_MODE_TORCH );
            mCamera.setParameters( params );
            try {
                mCamera.setPreviewTexture(mPreviewTexture);
            } catch (IOException ex) {
                // Ignore
            }
            mCamera.startPreview();
        }
    }

    public void toggleFlashLight(boolean toggle){
        try {
            CameraManager cameraManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
            for (String id : cameraManager.getCameraIdList()) {

                // Turn on the flash if camera has one
                if (cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cameraManager.setTorchMode(id, toggle);
                    }
                }
            }
        } catch (Exception e2) {
            Toast.makeText(getApplicationContext(), "Torch Failed: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}
