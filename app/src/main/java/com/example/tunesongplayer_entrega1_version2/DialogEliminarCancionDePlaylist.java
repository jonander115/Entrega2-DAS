package com.example.tunesongplayer_entrega1_version2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogEliminarCancionDePlaylist extends DialogFragment {

    private String usuario;

    private String cancion;

    private String autor;
    private String playlist;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Recogemos los elementos que ha recibido el diálogo
        if (getArguments() != null){
            usuario = getArguments().getString("usuario");
            cancion = getArguments().getString("cancion");
            autor = getArguments().getString("autor");
            playlist = getArguments().getString("playlist");
        }

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Eliminar canción de playlist");
        builder.setMessage("¿Quieres eliminar la canción " + cancion + " de " + autor + " de la playlist " + playlist + "?");

        //Opción de Aceptar, que elimina la canción de la playlist y cierra el diálogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Eliminamos la canción de la playlist (el método está en la actividad CancionesPlaylist)
                //(No he podido hacer la llamada al servicio web desde el diálogo porque había problemas con el contexto de la aplicación)
                ((CancionesPlaylist) getActivity()).eliminarCancionDePlaylist(cancion, autor);

                dismiss();
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
