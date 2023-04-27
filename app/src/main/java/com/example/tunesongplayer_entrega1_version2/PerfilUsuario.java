package com.example.tunesongplayer_entrega1_version2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PerfilUsuario extends AppCompatActivity {

    private String usuario;
    private EditText et_nombre;
    private EditText et_apellidos;
    private EditText et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        //Recogemos el usuario, que nos viene dado desde la actividad de la página principal
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }

        //Recogemos los elementos de la vista
        et_nombre = (EditText) findViewById(R.id.et_NombrePerfil);
        et_apellidos = (EditText) findViewById(R.id.et_ApellidosPerfil);
        et_email = (EditText) findViewById(R.id.et_EmailPerfil);

        //Tenemos que mostrar al usuario sus datos, para ello hay que cogerlos de la base de datos con un servicio web

        //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
        //Este servicio web se encarga tanto de pedir los datos del usuario como de modificarlos, por lo que mediante un parámetro llamado "accion" distinguiré qué acción realizar
        Data datos = new Data.Builder()
                .putString("accion", "select")
                .putString("usuario", usuario)
                .build();

        //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebPerfilUsuario.class)
                .setInputData(datos)
                .build();

        //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            //Recuperamos el resultado de la tarea
                            //Mostramos al usuario sus datos
                            et_nombre.setText(workInfo.getOutputData().getString("nombre"));
                            et_apellidos.setText(workInfo.getOutputData().getString("apellidos"));
                            et_email.setText(workInfo.getOutputData().getString("email"));

                        }
                    }
                });

        //Encolamos la tarea
        WorkManager.getInstance(this).enqueue(otwr);

    }


    //Método para guardar los cambios en la base de datos cuando el usuario pulse el botón
    public void onClick_GuardarCambios(View v){

        //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
        //Este servicio web se encarga tanto de pedir los datos del usuario como de modificarlos, por lo que mediante un parámetro llamado "accion" distinguiré qué acción realizar
        Data datos = new Data.Builder()
                .putString("accion", "update")
                .putString("usuario", usuario)
                .putString("nombre", et_nombre.getText().toString())
                .putString("apellidos", et_apellidos.getText().toString())
                .putString("email", et_email.getText().toString())
                .build();

        //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebPerfilUsuario.class)
                .setInputData(datos)
                .build();

        //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String devolver = workInfo.getOutputData().getString("devolver");
                            if (devolver.equals("usuarioModificado")){
                                Toast.makeText(getApplicationContext(), "Cambios guardados", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

        //Encolamos la tarea
        WorkManager.getInstance(this).enqueue(otwr);

    }
}