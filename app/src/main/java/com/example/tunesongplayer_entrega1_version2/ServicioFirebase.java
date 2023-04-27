package com.example.tunesongplayer_entrega1_version2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {
    public ServicioFirebase() {
    }

    //Método con lo que se hará al recibir un mensaje
    //Si la aplicación está en primer plano se ejecuta el método pero no se muestra notificación a no ser que la programemos nosotros
    //Si la aplicación está en background:
    // -Si el mensaje es de tipo notificación, se muestra una notificación pero no se ejecuta este método
    // -Si el mensaje es de tipo datos, se ejecuta el método
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0){
            //Si el mensaje viene con datos
            //Programar la notificación

            //Definimos el Manager y el Builder
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "canalFirebase");

            //El Builder nos permite definir las características de la notificación
            //Recogemos el mensaje que ha sido enviado desde el servicio web
            builder.setSmallIcon(android.R.drawable.star_big_on)
                    .setContentTitle("Mensaje de Firebase")
                    .setContentText(remoteMessage.getData().get("mensaje"))
                    .setAutoCancel(true);

            //las versiones mayores que la Oreo necesitan un canal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel canal = new NotificationChannel("canalFirebase", "CanalNotificacionFirebase", NotificationManager.IMPORTANCE_DEFAULT);

                canal.setDescription("Canal de mensajes de Firebase");
                canal.setLightColor(Color.MAGENTA);

                //Unimos el canal al manager
                manager.createNotificationChannel(canal);

                //El manager lanza la notificación
                manager.notify(123, builder.build());
            }

        }
        if (remoteMessage.getNotification() != null){
            //Si el mensaje es una notificación

            //(Solo he utilizado mensajes con datos)
            Log.d("firebase", "notificacion");

        }
    }



}