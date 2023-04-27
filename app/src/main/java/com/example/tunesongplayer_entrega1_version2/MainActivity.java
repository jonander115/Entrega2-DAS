package com.example.tunesongplayer_entrega1_version2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private final int CODIGO_DE_PERMISO_NOTIFICACIONES = 10;
    private final int CODIGO_DE_PERMISO_CAMARA = 1;
    private final int CODIGO_DE_PERMISO_LECTURA = 2;
    private final int CODIGO_DE_PERMISO_ESCRITURA = 3;

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

        //También hay que pedir el permiso de cámara por si el usuario quiere cambiar la foto de la playlist, y
        // los permisos de lectura y escritura de galería para poder elegir una imagen y guardar las fotos hechas

        //Comprobamos si el permiso de cámara está concedido
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            //El permiso no está concedido, lo pedimos
            String[] permisos = new String[] {android.Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this,permisos, CODIGO_DE_PERMISO_CAMARA);
        }

        //Comprobamos si el permiso de lectura de galería está concedido
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            //El permiso no está concedido, lo pedimos
            String[] permisos = new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permisos, CODIGO_DE_PERMISO_LECTURA);
        }

        //Comprobamos si el permiso de escritura de galería está concedido
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            //El permiso no está concedido, lo pedimos
            String[] permisos = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this,permisos, CODIGO_DE_PERMISO_ESCRITURA);
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