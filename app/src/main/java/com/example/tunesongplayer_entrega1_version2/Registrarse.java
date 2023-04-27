package com.example.tunesongplayer_entrega1_version2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class Registrarse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
    }

    //Método para cuando se haga click en el botón para Registrarse
    public void onClick_Registrarse(View v) {

        //Recogemos los EditText de la vista
        EditText et_NombreRegistro = (EditText) findViewById(R.id.et_NombreRegistro);
        EditText et_ApellidosRegistro = (EditText) findViewById(R.id.et_ApellidosRegistro);
        EditText et_EmailRegistro = (EditText) findViewById(R.id.et_EmailRegistro);
        EditText et_NombreUsuarioRegistro = (EditText) findViewById(R.id.et_NombreUsuarioRegistro);
        EditText et_ContraseñaRegistro = (EditText) findViewById(R.id.et_ContraseñaRegistro);
        EditText et_RepetirContraseñaRegistro = (EditText) findViewById(R.id.et_RepetirContraseñaRegistro);

        //Recogemos el contenido de los EditText
        String nombre = et_NombreRegistro.getText().toString();
        String apellidos = et_ApellidosRegistro.getText().toString();
        String email = et_EmailRegistro.getText().toString();
        String usuario = et_NombreUsuarioRegistro.getText().toString();
        String password = et_ContraseñaRegistro.getText().toString();
        String repetirPassword= et_RepetirContraseñaRegistro.getText().toString();

        //Comprobamos que se hayan introducido todos los datos:
        if (nombre.length() != 0 && apellidos.length() != 0 && email.length() != 0 && usuario.length() != 0 && password.length() != 0 && repetirPassword.length() != 0) {

            //Comprobamos que "Contraseña" y "Repetir contraseña" tienen el mismo contenido
            if (password.equals(repetirPassword)) {

                //Si la contraseña es la misma en ambos campos, ahora pasamos a comprobar si el nombre de usuario existe
                //Esto lo haremos mediante un servicio web que, en caso de que el usuario no estuviera registrado, también lo registra

                //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
                Data datos = new Data.Builder()
                        .putString("usuario", usuario)
                        .putString("password", password)
                        .putString("nombre", nombre)
                        .putString("apellidos", apellidos)
                        .putString("email", email)
                        .build();

                //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebRegistro.class)
                        .setInputData(datos)
                        .build();

                //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
                WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if(workInfo != null && workInfo.getState().isFinished()){

                                    //Recuperamos el resultado de la tarea
                                    String resultadoTarea = workInfo.getOutputData().getString("resultado");

                                    //Gestionamos qué se hará en base al resultado
                                    if (resultadoTarea.equals("usuarioYaExiste")) {
                                        Toast.makeText(getApplicationContext(), "Ya existe un usuario con ese nombre, elige otro", Toast.LENGTH_LONG).show();
                                    }
                                    else if (resultadoTarea.equals("usuarioNuevoRegistrado")) {
                                        Toast.makeText(getApplicationContext(), "Se ha registrado el usuario", Toast.LENGTH_LONG).show();

                                        //Ahora se lanza una notificación de bienvenida mediante Firebase

                                        //Obtenemos el token asignado al dispositivo para el uso de FCM
                                        FirebaseMessaging.getInstance().getToken()
                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<String> task) {
                                                        if (!task.isSuccessful()){
                                                            task.getException();
                                                            return;
                                                        }
                                                        else{
                                                            String token = task.getResult();

                                                            Log.d("token",token);

                                                            //Se lanza una notificación de bienvenida mediante Firebase
                                                            lanzarMensajeBienvenida(token,usuario);

                                                        }
                                                    }

                                                });


                                        //Cerramos la actividad para que el usuario vuelva a la página de bienvenida de la aplicación
                                        finish();
                                    }


                                }
                            }
                        });

                //Encolamos la tarea
                WorkManager.getInstance(this).enqueue(otwr);

            }
            else{
                Toast.makeText(getApplicationContext(), "Debes introducir la misma contraseña en los dos campos para ello", Toast.LENGTH_LONG).show();
            }

        }
        else{
            Toast.makeText(getApplicationContext(), "Por favor, rellena todos los campos", Toast.LENGTH_LONG).show();
        }

    }


    //Método para subir el token del usuario a la base de datos y lanzar una notificación al dispositivo
    //----Si se quisiera probar el envío de la notificación mediante FCM de forma externa, habría que usar el fichero pruebaFirebaseExterna.php
    public void lanzarMensajeBienvenida(String token, String usuario){

        //He utilizado la librería Volley, porque WorkManager iba muy lento
        //https://developer.android.com/training/volley?hl=es-419

        //Crear la cola de solicitudes
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        //Url del servicio web en el servidor
        String url = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/firebase.php";

        //Solicitud
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Procesar la respuesta del servidor

                //La respuesta es un String
                if (response.equals("notificacionExitosa")) {
                    Log.d("firebase","notificación recibida");
                    //Toast.makeText(getApplicationContext(), "Notificación recibida por Firebase", Toast.LENGTH_LONG).show();
                }
                else if (response.equals("notificacionFallada")){
                    Log.d("firebase","error de servicio firebase");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Manejar error de la solicitud
                Log.d("firebase","error de servicio firebase");
                Toast.makeText(getApplicationContext(), "Error de firebase", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Agregar los parámetros necesarios para la solicitud
                params.put("token",token);
                params.put("usuario",usuario);

                return params;
            }
        };

        //Encolar la solicitud
        queue.add(stringRequest);

    }


}