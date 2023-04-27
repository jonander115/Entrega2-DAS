package com.example.tunesongplayer_entrega1_version2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class IniciarSesion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
    }

    //Método para cuando se haga click en el botón para Iniciar sesión
    public void onClick_IniciarSesion(View v) {

        //Recogemos del layout los EditText en los que el usuario introduce su usuario y contraseña
        EditText et_Usuario = (EditText) findViewById(R.id.et_NombreUsuario);
        EditText et_Password = (EditText) findViewById(R.id.et_Contraseña);

        //Comprobamos que se ha escrito algo en los EditText del usuario y la contraseña
        if (et_Usuario.getText().toString().length() != 0 && et_Password.getText().toString().length() != 0) {

            //Recogemos los valores introducidos en los EditText
            String usuario = et_Usuario.getText().toString();
            String password = et_Password.getText().toString();

            //Mediante un servicio web que está alojado en el servidor comprobaremos si el usuario y la contraseña introducidos son correctos
            //En caso de que el usuario esté registrado, el hash de la contraseña introducida debe ser igual al almacenado en la base de datos
            //Si el inicio de sesión es correcto, el usuario pasará a la página principal de la aplicación

            //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
            Data datos = new Data.Builder()
                    .putString("usuario", usuario)
                    .putString("password", password)
                    .build();

            //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebInicioSesion.class)
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
                                if (resultadoTarea.equals("usuarioNoExiste")) {
                                    Toast.makeText(getApplicationContext(), "Usuario incorrecto, inténtalo de nuevo", Toast.LENGTH_LONG).show();
                                }
                                else if (resultadoTarea.equals("passwordIncorrecto")) {
                                    Toast.makeText(getApplicationContext(), "Contraseña incorrecta, inténtalo de nuevo", Toast.LENGTH_LONG).show();
                                }
                                else if (resultadoTarea.equals("iniciarSesion")) {
                                    //Si el usuario y la contraseña introducidos coinciden con unos previamente registrados, se inicia
                                    // la sesión del usuario mediante una nueva actividad que lo dirige a la página principal de la aplicación
                                    Intent i = new Intent (getApplicationContext(), PagPrincipal.class);
                                    //Enviamos el nombre de usuario a esta nueva actividad
                                    i.putExtra("usuario", usuario);
                                    startActivity(i);
                                    finish();

                                    Toast.makeText(getApplicationContext(), "¡Bienvenido, " + usuario + "!", Toast.LENGTH_LONG).show();
                                }


                            }
                        }
                    });

            //Encolamos la tarea
            WorkManager.getInstance(this).enqueue(otwr);

        }
        else {
            Toast.makeText(getApplicationContext(), "Introduce usuario y contraseña", Toast.LENGTH_LONG).show();
        }

    }

}