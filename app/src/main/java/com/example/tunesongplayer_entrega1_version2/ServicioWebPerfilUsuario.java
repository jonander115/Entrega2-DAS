package com.example.tunesongplayer_entrega1_version2;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ServicioWebPerfilUsuario extends Worker {
    public ServicioWebPerfilUsuario(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private Data resultadoADevolver;
    private String parametros;

    @NonNull
    @Override
    public Result doWork() {
        //Dirección del fichero php con el servicio web, ubicado en el servidor de la asignatura
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/jlopezdeahumad001/WEB/perfilUsuario.php";

        //Realizamos la conexión al servidor
        HttpURLConnection urlConnection = null;
        String result = null;
        try {
            //Configuramos elementos de la conexión
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);


            //La tarea ha recibido los parámetros que requiere el servicio web mediante un objeto de tipo Data
            String usuario = getInputData().getString("usuario");
            String accion = getInputData().getString("accion"); //Este parámetro indica la acción a realizar

            if (accion.equals("update")) {
                //Si hay que modificar el usuario, el servicio web ha recibido los datos a actualizar
                String nombre = getInputData().getString("nombre");
                String apellidos = getInputData().getString("apellidos");
                String email = getInputData().getString("email");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("usuario", usuario)
                        .appendQueryParameter("accion", accion)
                        .appendQueryParameter("nombre", nombre)
                        .appendQueryParameter("apellidos", apellidos)
                        .appendQueryParameter("email", email);
                parametros = builder.build().getEncodedQuery();

            } else if (accion.equals("select")) {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("usuario", usuario)
                        .appendQueryParameter("accion", accion);
                parametros = builder.build().getEncodedQuery();
            }

            //Configuramos la variable de la conexión para indicarle que vamos a enviarle parámetros, especificando el método HTTP y el formato a utilizar
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Utilizamos el objeto PrintWriter para incluir los parámetros en la llamada
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            //Realizamos la llamada al servicio web y recogemos el resultado:
            int statusCode = urlConnection.getResponseCode();

            //Comprobamos si la llamada ha ido bien y procesamos el resultado:
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    //En la variable result vamos generando el resultado final
                    result += line;
                }
                inputStream.close();

                //Recogemos el resultado
                if (accion.equals("update")) {

                    resultadoADevolver = new Data.Builder()
                            .putString("devolver", result)
                            .build();

                } else if (accion.equals("select")) {

                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(result);

                    resultadoADevolver = new Data.Builder()
                            .putString("nombre", (String) json.get("nombre"))
                            .putString("apellidos", (String) json.get("apellidos"))
                            .putString("email", (String) json.get("email"))
                            .build();

                }


            } else {
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error en el servicio web del perfil de usuario", Toast.LENGTH_LONG).show();
                Log.d("servicioWeb", "Status Perfil Usuario no es 200");
            }




        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        return Result.success(resultadoADevolver);
    }
}
