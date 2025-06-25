package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class conexionAPI {
	public static String getJsonFromUrl(String url) throws IOException {
    	/*
    	 Es la api principal que se utiliza para obtener las coordenadas de las ubicaciones. Como parámetro, está la url que se forma en el main.
    	 */
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Configura la solicitud GET
        con.setRequestMethod("GET");
        // Lee la respuesta del servidor
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
	
	public static String getJsonSearchText(String url) {
    	/*
    	 Esta api se utiliza para obtener la dirección formateada, en caso que la dirección esté abreviada, devuelve la dirección completa, comunidad autónoma, pais y código postal, lo que asegura que la api geocode entienda
    	 la coordenada.
    	 */
    	String json="";
    	String aux = String.format("{\"textQuery\": \"%s\"}", url);
    	String urla = "https://places.googleapis.com/v1/places:searchText";
    	StringBuffer response=null;

    	URL obj;
		try {
			obj = new URL(urla);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.addRequestProperty("Content-type", "application/json");
			con.addRequestProperty("X-Goog-Api-Key", Main.apiKey);
			con.addRequestProperty("X-Goog-FieldMask", "places.displayName,places.formattedAddress,places.priceLevel");

			try (java.io.OutputStream os = con.getOutputStream()) {
                byte[] input = aux.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			response = new StringBuffer();
			while ((json = in.readLine()) != null) {
	            response.append(json);
	        }
			in.close();
		} catch (IOException e) {
			System.out.println("Error con la url"+e.getMessage());
		}

		System.out.println(response.toString());

    	return response.toString();
    }
	
	public static String getJsonAutocomplete(String input, String coord) {
    	/*
    	 Esta api, recibe como parámetro el nombre de la empresa y la coordenada obtenida en la api geocode de la función getJsonFromUrl. Esto es debido a que en algunas ocasiones, la api anterior falla.
    	 Sin embargo, en esta api, se devuelve la coordenada cercana a la coordenada de la otra api. Por lo tanto, al realizar una doble validación se consigue una tasa de aciertos superior.
    	 Esta función en concreto devuelve el json de la respuesta.
    	 */
    	StringBuffer response=null;
        try {
        	String json="";
        	String[] values=new String[3];
            // URL base de la API de Places
            String api = "https://places.googleapis.com/v1/places:autocomplete";
            // Parámetros de entrada
            String[] value = coord.split(",");
            String radius = "500.0";

            values[0]=value[0];
            values[1]=value[1];
            values[2]=radius;

            System.out.println(values[0]);
            System.out.println(values[1]);
            System.out.println(values[2]);
            System.out.println(input);

            // Crear el JSON para la solicitud
            String jsonInputString = String.format("{\"input\": \"%s\", \"locationBias\": {\"circle\": {\"center\": {\"latitude\": %s, \"longitude\": %s}, \"radius\": %s}}}", input, values[0], values[1], values[2]);
            System.out.println(jsonInputString);            // Crear la URL con el parámetro API Key

            // Configurar la conexión HTTP
            URL obj = new URL(api);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("X-Goog-Api-Key", Main.apiKey); // Añadir la cabecera de la clave API
            con.setDoOutput(true);

            try (java.io.OutputStream os = con.getOutputStream()) {
                byte[] input1 = jsonInputString.getBytes("utf-8");
                os.write(input1, 0, input1.length);
            }

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			response = new StringBuffer();
			while ((json = in.readLine()) != null) {
	            response.append(json);
	        }
			in.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return response.toString();
    }
}
