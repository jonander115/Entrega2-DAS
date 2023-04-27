package com.example.tunesongplayer_entrega1_version2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class DialogAñadirCancionAPlaylist extends DialogFragment {

    private String usuario;
    private String cancion;
    private String autor;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Recogemos los elementos que ha recibido el diálogo
        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            cancion = getArguments().getString("cancion");
            autor = getArguments().getString("autor");
        }


        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir canción a playlist");
        builder.setMessage("Introduce el nombre de la playlist a la que añadir la canción");

        //Mediante un LayoutInflater hacemos que la vista del diálogo sea un layout que hemos diseñado en un xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspectoDialog = inflater.inflate(R.layout.dialog_anadir_a_playlist, null);
        builder.setView(aspectoDialog);


        //Opción de Aceptar, que en caso de que sea posible añade la canción a la playlist cuyo nombre ha introducido el usuario
        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Recogemos el texto del EditText donde el usuario ha escrito el nombre de la playlist
                EditText et_NombrePlaylistAAñadir = (EditText) aspectoDialog.findViewById(R.id.et_PlaylistAAñadir);

                //Comprobamos que el nombre de la playlist no está en blanco
                if (et_NombrePlaylistAAñadir.getText().toString().length() != 0){

                    String nombrePlaylist = et_NombrePlaylistAAñadir.getText().toString();

                    //Llamamos al método de la actividad encargado de añadir la canción a la playlist
                    //(No he podido hacer la llamada al servicio web desde el diálogo porque había problemas con el contexto de la aplicación)
                    ((CancionesBuscadas) getActivity()).añadirAPlaylist(nombrePlaylist, cancion, autor);

                    dismiss();
                }
                else{
                    Toast.makeText(getContext(), "Por favor, introduce el nombre de una playlist", Toast.LENGTH_LONG).show();
                }

            }
        });

        //Opción de Cancelar, que cierra el diálogo
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return builder.create();

    }

}
