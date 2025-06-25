package com.example;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class parseJSON {
	public static String parseJson(String json){
    	/*
    	 Esta función procesa el json que devuelve la función getJsonFromUrl. Lo parsea para obtener las coordenadas.
    	 */
        String coordenadas="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonObject = objectMapper.readTree(json);
            System.out.println(jsonObject.get("status"));
            System.out.println(jsonObject.get("status").toString().equals("\"OK\""));
            if(jsonObject.get("status").toString().equals("\"OK\"")){
                String lat = jsonObject.get("results").get(0).get("geometry").get("location").get("lat").toString();
                String lng = jsonObject.get("results").get(0).get("geometry").get("location").get("lng").toString();

                // Imprime las coordenadas
                coordenadas=lat+","+lng;
                System.out.println(coordenadas);
            }else{
                System.out.println("No result");
                return null;
            }
        } catch (JSONException |JsonProcessingException e) {
            System.out.println("Error al gestionar el json"+e.getMessage());
        }
        return coordenadas;
    }
	
	public static String parseJsonText(String json) {
    	/*
    	 Esta api parsea el json que devuelve la api getJsonSearchText. En lugar de parsear la coordenada, parsea la dirección bien formateada que después vuelvo a introducir en la primera api.
    	 */
    	String nombre="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonObject = objectMapper.readTree(json);
            if(jsonObject.get("places")!=null) {
            	nombre = jsonObject.get("places").get(0).get("formattedAddress").toString();
                nombre=nombre.replaceAll("\"","");
                System.out.println(nombre);
            }else {
            	System.out.println("No hay resultados");
            	nombre=null;
            }

        } catch (JSONException |JsonProcessingException e) {
            System.out.println("Error al gestionar el json"+e.getMessage());
        }
        return nombre;
    }
	
	public static String parseJsonAutocomplete(String json) {
    	/*
    	 Esta api parsea el json que devuelve la función getJsonAutocomplete para obtener la dirección formateada cercana a una cooredenada con un nombre de una empresa determinado.
    	 */
    	String nombre="";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonObject = objectMapper.readTree(json);
            if(jsonObject.get("suggestions")!=null) {
            	nombre = jsonObject.get("suggestions").get(0).get("placePrediction").get("text").get("text").toString();
                nombre=nombre.replaceAll("\"","");
                System.out.println(nombre);
            }else {
            	System.out.println("No hay resultados");
            	nombre=null;
            }

        } catch (JSONException |JsonProcessingException e) {
            System.out.println("Error al gestionar el json"+e.getMessage());
        }
        return nombre;
    }
}
