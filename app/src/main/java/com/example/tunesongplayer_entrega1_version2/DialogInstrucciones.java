package com.example.tunesongplayer_entrega1_version2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogInstrucciones extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Definimos un builder para construir el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Algunas acciones");

        //Mediante un LayoutInflater hacemos que la vista del diálogo sea un layout que hemos diseñado en un xml
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View aspectoDialog = inflater.inflate(R.layout.dialog_instrucciones, null);
        builder.setView(aspectoDialog);

        //Damos valor a los elementos del diálogo
        TextView tv_Selecciona = aspectoDialog.findViewById(R.id.tv_Selecciona);
        tv_Selecciona.setText("Selecciona los siguientes botones para:");

        TextView tv_IconoLupa = aspectoDialog.findViewById(R.id.tv_IconoLupa);
        tv_IconoLupa.setText("Buscar la canción escrita en la barra de búsqueda");

        TextView tv_IconoUsuario = aspectoDialog.findViewById(R.id.tv_IconoUsuario);
        tv_IconoUsuario.setText("Acceder a tu perfil y modificar tu información");

        //Opción de Aceptar, que cierra el diálogo
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return builder.create();
    }
}
