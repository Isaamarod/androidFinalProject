package com.isa_t.proyectofinalmasterd;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity{

    public static final int REQUEST_CODE_PERMISSION = 1;

    public abstract void onPermissionsGranted();

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == REQUEST_CODE_PERMISSION){
            boolean ok = true;
            for(int grantResult : grantResults){
                if(grantResult != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(this)
                            .setTitle("Error de permisos")
                            .setMessage("Se necesitan algunos permisos para el correcto funcionamiento de la aplicación.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Utils.requestPermissions(BaseActivity.this);
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                    ok = false;
                    break;
                }
            }
            if(ok)
                onPermissionsGranted();
        }
    }
}
