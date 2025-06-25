package com.example;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Row;
import org.json.JSONException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class validacion {
	public static boolean parseJSONrooftop(String json) {
    	/*
    	 Esta función verifica si la coordenada proporcionada por las otras apis está sobre un edificio o en la carretera o en el campo.
    	 */
    	boolean valido=false;
    	if(json.length()>3) {
    		try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonObject = objectMapper.readTree(json);
                System.out.println(jsonObject.get("status"));
                System.out.println(jsonObject.get("status").toString().equals("\"OK\""));
                if(jsonObject.get("results").get(0).get("geometry").get("location_type").toString().equals("\"ROOFTOP\"")){
                    valido=true;
                }else{
                    valido=false;
                }
            } catch (JSONException | JsonProcessingException e) {
                System.out.println("Error al gestionar el json"+e.getMessage());
            }
    	}
        return valido;
    }
	
	public static ArrayList<String> parseAdressSimple(String a) {
		   /*
		    Con la intención de verificar si la coordenada se encuentra del código postal adecuado, esta función devuelve el código postal para comprobar si es o no es igual
		    */
		   ArrayList<String> codPost=new ArrayList<String>();
	       try {
	           ObjectMapper objectMapper = new ObjectMapper();
	           JsonNode jsonObject = objectMapper.readTree(a);
	           String aux=jsonObject.get("status").asText();
	           System.out.println(aux);
	           if(aux.equals("OK")) {
	        	   for(int j=0;j<=10;j++) {
	        		   for(int i=0;i<=10;i++) {
	        				   if(jsonObject.get("results").has(j)) {
	        					   //entra
	        					   if(jsonObject.get("results").get(j).get("address_components").has(i)) {
	        						   //entra
	            					   if(jsonObject.get("results").get(j).get("address_components").get(i).get("types").toString().equals("[\"postal_code\"]")) {
	            						   codPost.add(jsonObject.get("results").get(j).get("address_components").get(i).get("long_name").asText());
	                    				   System.out.print(jsonObject.get("results").get(j).get("address_components").get(i).get("long_name").toString()+" ");
	            					   }
	        					   }
	        				   }
	        		   }
	        	   }
	           }else {
	        	   System.out.println("No hay resultados");
	        	   return codPost;
	           }
	       } catch (JSONException |JsonProcessingException e) {
	           System.out.println("Error al gestionar el json"+e.getMessage());
	       }
	       return codPost;
	   }
	
	public static boolean comprobComunidad(String coord,Row r) {
    	/*
    	 Esta función llama a la función parseAdressSimple y compara los resultados del código postal de la api con el código postal del excel. Devuelve true si es válido o igual o falso si es diferente o inválido
    	 */
    	boolean valido=false;
    	ArrayList<String> codPost=new ArrayList<String>();
    	String cellS;
    	Double cell;
        try {
        	System.out.println("https://maps.googleapis.com/maps/api/geocode/json?address="+coord+"&key="+Main.apiKey);
        	System.out.println(com.example.conexionAPI.getJsonFromUrl("https://maps.googleapis.com/maps/api/geocode/json?address="+coord+"&key="+Main.apiKey));
        	codPost=parseAdressSimple(com.example.conexionAPI.getJsonFromUrl("https://maps.googleapis.com/maps/api/geocode/json?address="+coord+"&key="+Main.apiKey));
        	cell=r.getCell(4).getNumericCellValue();
        	cellS=String.format("%.0f", cell);			//Elimina el decimal del double para poder compararlo con el string codPost
        	System.out.println();
        	System.out.println("celda excel "+cellS);
        	if(codPost.size()!=0) {				//todo es nulo
        		for(int i=0;i<codPost.size();i++) {
            		if(!valido) {
            			System.out.println("here");
            			System.out.println(codPost.get(i));
            			if(codPost.get(i).equals(cellS)) {
                			valido=true;
                    		System.out.println("La coordenada es válida");
                		}else {
                    		System.out.println("La coordenada es inválida por código postal");
                    	}
            		}
            	}
        	}else {
        		System.out.println("No hay resultados test");
        	}
        	System.out.println(valido);
		} catch (IOException e) {
			System.out.println("Error downloading json"+e.getMessage());
		}
    	return valido;
    }
	
	public static boolean comprobResultJSON(String json) {
    	/*
    	 Esta función verifica si el json tiene resultados con el parámetro status. Si el estatus es "OK" devuelve el booleano true y si no devuelve false
    	 */
    	boolean valido=false;
    	try {
    		ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonObject = objectMapper.readTree(json);
            if(jsonObject.get("status").asText().equals("OK")) {
            	valido=true;
            	System.out.println("Hay resultados en el json");
            }else {
            	System.out.println("No hay resultados en el json");
            }
            System.out.println(jsonObject.get("status").asText());
            System.out.println(jsonObject.get("status").asText().equals("\"OK\""));
		} catch (Exception e) {
			System.out.println("Error comprobación json sin resultados"+e.getMessage());
		}
    	return valido;
    }
}
