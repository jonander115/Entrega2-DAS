package com.example.tunesongplayer_entrega1_version2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CancionesBuscadas extends AppCompatActivity {

    private String cancionBuscada;
    private String usuario;
    private boolean sonandoMusica = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canciones_buscadas);

        //Recogemos la canción buscada, que nos viene dada desde la actividad de la página principal
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cancionBuscada = extras.getString("cancionBuscada");
            usuario = extras.getString("usuario");
            TextView tv_Busqueda = (TextView) findViewById(R.id.tv_Busqueda);
            tv_Busqueda.setText(cancionBuscada);
        }


        ListView listaCancionesBuscadas = (ListView) findViewById(R.id.lista_canciones_buscadas);


        //Llamamos a un servicio web para mostrar las canciones buscadas
        //Para no complicar mucho el código, solo se muestran las canciones que se llaman exactamente igual a lo que se ha buscado


        //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
        Data datos = new Data.Builder()
                .putString("nombreCancion", cancionBuscada)
                .build();

        //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebGestionarCancionesBuscadas.class)
                .setInputData(datos)
                .build();

        //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            //Recogemos el resultado
                            String[] arrayCanciones = workInfo.getOutputData().getStringArray("cancionesBuscadas");
                            String[] arrayAutores = workInfo.getOutputData().getStringArray("autoresCanciones");

                            //Le pasamos a la vista los datos a mostrar mediante el adaptador
                            CancionBuscadorAdapter adapter = new CancionBuscadorAdapter(getApplicationContext(), arrayCanciones, arrayAutores);
                            listaCancionesBuscadas.setAdapter(adapter);

                        }
                    }
                });

        //Encolamos la tarea
        WorkManager.getInstance(this).enqueue(otwr);


        //Click largo para añadir una canción a una playlist
        listaCancionesBuscadas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Recogemos información para enviar
                TextView tv_NombreCancionEnBuscador = (TextView) view.findViewById(R.id.tv_NombreCancionEnBuscador);
                String nombreCancionDelBotonPulsado = tv_NombreCancionEnBuscador.getText().toString();
                TextView tv_AutorCancionEnBuscador = (TextView) view.findViewById(R.id.tv_AutorCancionEnBuscador);
                String autorCancionDelBotonPulsado = tv_AutorCancionEnBuscador.getText().toString();


                //Preparamos y lanzamos el diálogo
                DialogAñadirCancionAPlaylist dialogAñadirCancionAPlaylist = new DialogAñadirCancionAPlaylist();
                Bundle args = new Bundle();
                args.putString("usuario", usuario);
                args.putString("cancion", nombreCancionDelBotonPulsado);
                args.putString("autor", autorCancionDelBotonPulsado);

                dialogAñadirCancionAPlaylist.setArguments(args);
                dialogAñadirCancionAPlaylist.show(getSupportFragmentManager(), "añadirAPlaylist");

                return true;
            }
        });

        //Click corto para reproducir una canción
        listaCancionesBuscadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Recogemos nombre y autor de la canción
                TextView tv_NombreCancionEnBuscador = (TextView) view.findViewById(R.id.tv_NombreCancionEnBuscador);
                String nombreCancionDelBotonPulsado = tv_NombreCancionEnBuscador.getText().toString();
                TextView tv_AutorCancionEnBuscador = (TextView) view.findViewById(R.id.tv_AutorCancionEnBuscador);
                String autorCancionDelBotonPulsado = tv_AutorCancionEnBuscador.getText().toString();

                //Ponemos / Paramos la música
                if (sonandoMusica==false){

                    //La versión de la aplicación siempre va a ser mayor o igual a la Oreo
                    //Por lo tanto, lanzamos el servicio con startForegroundService
                    Intent intentServicioMusica = new Intent(getApplicationContext(),MusicService.class);
                    intentServicioMusica.putExtra("nombreCancion",cancionBuscada);
                    intentServicioMusica.putExtra("autorCancion",autorCancionDelBotonPulsado);

                    //Llamamos al servicio
                    startForegroundService(intentServicioMusica);
                    sonandoMusica = true;
                }
                else{
                    //Paramos el servicio
                    Intent intentServicioMusica = new Intent(getApplicationContext(),MusicService.class);
                    stopService(intentServicioMusica);
                    sonandoMusica = false;
                }

            }
        });


    }



    //Método para añadir la canción a una playlist del usuario
    public void añadirAPlaylist(String nombrePlaylistAAñadir, String nombreCancion, String nombreAutor){

        //Establecemos los parámetros que requerirá la tarea que realizará el servicio web
        Data datos = new Data.Builder()
                .putString("nombreUsuario", usuario)
                .putString("nombrePlaylist", nombrePlaylistAAñadir)
                .putString("nombreCancion", nombreCancion)
                .putString("autorCancion", nombreAutor)
                .build();

        //Creamos una tarea que se ejecuta una vez y le asignamos los parámetros de entrada
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ServicioWebAñadirCancionAPlaylist.class)
                .setInputData(datos)
                .build();

        //Mediante WorkManager gestionamos la tarea, añadiendo un observer para recoger el resultado
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe((LifecycleOwner) this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            //Recuperamos el resultado de la tarea
                            String resultadoTarea = workInfo.getOutputData().getString("devolver");

                            if (resultadoTarea.equals("cancionAñadida")){
                                Toast.makeText(getApplicationContext(), "Se ha añadido la canción a la playlist", Toast.LENGTH_LONG).show();
                            }
                            else if (resultadoTarea.equals("noPosible")){
                                Toast.makeText(getApplicationContext(), "No se puede añadir la canción", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });

        //Encolamos la tarea
        WorkManager.getInstance(this).enqueue(otwr);


    }


}

