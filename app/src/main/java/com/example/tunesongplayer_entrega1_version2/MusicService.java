package com.example.tunesongplayer_entrega1_version2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {
    public MusicService() {
    }

    private MediaPlayer musicaMP3;
    private String nombreCancion;
    private String autorCancion;
    private boolean sonandoMusica = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //Recuperamos la información enviada al servicio
        Bundle extras = intent.getExtras();
        if(extras != null){
            nombreCancion = extras.getString("nombreCancion");
            autorCancion = extras.getString("autorCancion");
            Log.d("cancion",nombreCancion);
        }


        //Definimos el Manager de notificaciones
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Las versiones mayores que la Oreo necesitan un canal
        NotificationChannel canalservicio = new NotificationChannel("canalMusica",
                "Canal musical",NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(canalservicio);
        canalservicio.setDescription("Canal de audio");
        canalservicio.setLightColor(Color.GREEN);

        //El Builder nos permite definir las características de la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "canalMusica")
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                .setContentTitle("Escuchando música")
                .setContentText("Escuchando " + nombreCancion + " de " + autorCancion)
                .setAutoCancel(false);
        Notification notification = builder.build();
        startForeground(1, notification);


        //Reproducimos el archivp mp3 del servidor correspondiente a la canción recibida
        if (nombreCancion.equalsIgnoreCase("Boundless space")){
            musicaMP3 = MediaPlayer.create(this, Uri.parse("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/Canciones/boundlessspace.mp3"));
        }
        if (nombreCancion.equalsIgnoreCase("Burning fire")){
            musicaMP3 = MediaPlayer.create(this, Uri.parse("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/Canciones/burningfire.mp3"));
        }
        else if (nombreCancion.equalsIgnoreCase("Closer to the sun")) {
            musicaMP3 = MediaPlayer.create(this, Uri.parse("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/Canciones/closertothesun.mp3"));
        }
        else if (nombreCancion.equalsIgnoreCase("Faster, stronger")) {
            musicaMP3 = MediaPlayer.create(this, Uri.parse("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/Canciones/fasterstronger.mp3"));
        }
        else if (nombreCancion.equalsIgnoreCase("Get what you want")) {
            musicaMP3 = MediaPlayer.create(this, Uri.parse("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/Canciones/getwhatyouwant.mp3"));
        }
        else if (nombreCancion.equalsIgnoreCase("Hit the ground")) {
            musicaMP3 = MediaPlayer.create(this, Uri.parse("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/Canciones/hittheground.mp3"));
        }
        else if (nombreCancion.equalsIgnoreCase("Plain")) {
            musicaMP3 = MediaPlayer.create(this, Uri.parse("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/Canciones/plain.mp3"));
        }
        else if (nombreCancion.equalsIgnoreCase("Rabbits Hop")) {
            musicaMP3 = MediaPlayer.create(this, Uri.parse("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/Canciones/rabbitshop.mp3"));
        }
        else if (nombreCancion.equalsIgnoreCase("Rock and dust")) {
            musicaMP3 = MediaPlayer.create(this, Uri.parse("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/Canciones/rockanddust.mp3"));
        }
        else if (nombreCancion.equalsIgnoreCase("Slider")) {
            musicaMP3 = MediaPlayer.create(this, Uri.parse("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/Canciones/slider.mp3"));
        }
        else{
            Toast.makeText(getApplicationContext(), "No se puede reproducir el audio", Toast.LENGTH_LONG).show();
        }


        musicaMP3.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicaMP3.stop();
        musicaMP3.release();
        musicaMP3=null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}