<?php

$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xjlopezdeahumad0"; #el usuario para esa base de datos
$DB_PASS="N9tVbYiq"; #la clave para ese usuario
$DB_DATABASE="Xjlopezdeahumad0_BaseDeDatos"; #la base de datos a la que hay que conectarse

#Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

#Comprobamos la conexión:
if (mysqli_connect_errno()) {
	echo 'Error de conexion: ' . mysqli_connect_error();
	exit();
}

#------------Este fichero sirve para, cada vez que se registra un usuario, se le mande una notificación por Firebase
#------------Para probar el envío de notificación de Firebase de forma externa, hay que usar el fichero pruebaFirebaseExterna.php

#Parámetros a utilizar
$usuario = $_POST["usuario"];
$token = $_POST["token"];

#Modificamos el usuario en la base de datos para añadir el token
$resultado = mysqli_query($con, "UPDATE Usuarios SET Token='$token' WHERE Usuario='$usuario'");

#Comprobar si se ha ejecutado correctamente
if (!$resultado) {
	echo 'Ha ocurrido algún error: ' . mysqli_error($con);
}

#Hacemos uso de la mensajería FCM
#El servicio de mensajería Firebase se utiliza en mi aplicación para mandar una notificación de bienvenida a los usuarios nada más registrarse

#Cabecera de la petición
$cabecera= array(
	'Authorization: key=AAAAwvFjxvQ:APA91bFSidYIPblSTvWBRw2EI_3NBYhnBJ6dpypcOP-yZg271WwxN37rj-4dvqBhEFmZK_rj8rInYxihZdItN_nxKqQEUah08SdxIlfambb1Fkmccc82xocsUak1Sk0C606tFR5MSjKE',
	'Content-Type: application/json'
);
	
#A quién va dirigido el mensaje
$msg= array(
		'to'=>$token,
		'data' => array(
				"mensaje" => "Bienvenido a TuneSongPlayer")
		);
			
#Pasar mensaje a JSON
$msgJSON= json_encode ( $msg);
	
$ch = curl_init(); #inicializar el handler de curl

#indicar el destino de la petición, el servicio FCM de google
curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');

#indicar que la conexión es de tipo POST
curl_setopt( $ch, CURLOPT_POST, true );

#agregar las cabeceras
curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
	
#Indicar que se desea recibir la respuesta a la conexión en forma de string
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
	
#agregar los datos de la petición en formato JSON
curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON );

#ejecutar la llamada
$resultado= curl_exec( $ch );

#cerrar el handler de curl
curl_close( $ch );
	
if (curl_errno($ch)) {
	print curl_error($ch);
}

#Devolver el resultado
if ($resultado == True){
	$devolver = 'notificacionExitosa';
}
else{
	$devolver = 'notificacionFallada';
}

echo $devolver;

#Cerramos la conexión con la base de datos
mysqli_close($con);


?>