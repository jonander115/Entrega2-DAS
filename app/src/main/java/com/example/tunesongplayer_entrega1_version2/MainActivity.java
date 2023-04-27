package com.example.tunesongplayer_entrega1_version2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int CODIGO_DE_PERMISO_NOTIFICACIONES = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Pedimos permisos necesarios

        //Pedimos el permiso de notificaciones porque se recibirán notificaciones de Firebase tras registrar un usuario,
        // y también se recibirán al escuchar música
        //El permiso de notificaciones es necesario desde Android 13 (API 33 - TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Comprobamos si el permiso de notificaciones está concedido
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                //El permiso no está concedido, lo pedimos
                String[] permisos = new String[]{android.Manifest.permission.POST_NOTIFICATIONS};
                ActivityCompat.requestPermissions(this, permisos, CODIGO_DE_PERMISO_NOTIFICACIONES);
            }
        }


    }

    //Método que sobreescribimos para gestionar la decisión del usuario tras responder al diálogo de los permisos
    //Los permisos de notificaciones solo se piden si la versión es mayor o igual a Android 13
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {

            case CODIGO_DE_PERMISO_NOTIFICACIONES: {
                // Si la petición se cancela, granResults estará vacío
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permiso concedido, se pueden recibir notificaciones
                } else {
                    // PERMISO DENEGADO
                    Toast.makeText(getApplicationContext(), "No se pueden recibir notificaciones", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    //Método para cuando se haga click en el botón para Iniciar sesión
    public void bt_IniciarSesion_onClick(View v) {
        //Al hacer click en el botón de Iniciar sesión, se abre la actividad correspondiente
        Intent i = new Intent (this, IniciarSesion.class);
        startActivity(i);
    }

    //Método para cuando se haga click en el botón para Registrarse
    public void bt_Registrarse_onClick(View v) {
        //Al hacer click en el botón de Registrarse, se abre la actividad correspondiente
        Intent i = new Intent (this, Registrarse.class);
        startActivity(i);
    }

}