package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    //static String filePath="/home/oscar/DAM/prácticas/copia2.xlsx";
    static String fileWrite="../output.xlsx";
    static final String apiKey="";			//Rellenar la clave api
    static int cont=1;
    public static void main(String[] args) {
    	Scanner sc=new Scanner(System.in);
    	//Prepara el fichero de entrada
    	System.out.println("Introduce el nombre del fichero(debe de encontarse en el directorio actual)");
    	String fich=sc.nextLine();
    	File resourceFile = new File("../"+fich);
        System.out.println(resourceFile);
        
        String coord=null;
        try {
            FileInputStream inputStream = new FileInputStream(resourceFile);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            String response="";
        	String ask;

        	String cell3;
        	String cell2;
        	boolean nombrePol=true;
            for (Row row : sheet) {
                if(row.getCell(0)!=null&&row.getRowNum()!=0){
                            if(row.getCell(3)!=null){
                            	System.out.println("Número "+cont);
                            	cell3 = row.getCell(3).getStringCellValue();
                                cell2 = row.getCell(2).getStringCellValue();

                                nombrePol=cell3.toLowerCase().contains("pi".toLowerCase())||cell3.toLowerCase().contains("pol");
                                //Booleano para saber si la fila se encuentra en un polígono industrial o no
                                if(nombrePol) {
                                	System.out.println("Es un polígono industrial");
                                	String cell1;
                                    cell1 = row.getCell(1).getStringCellValue();
                                    ask="https://maps.googleapis.com/maps/api/geocode/json?address="+cell1+" "+cell3+" "+cell2+"&key="+apiKey;
                                    System.out.println(ask);
                                    ask=ask.replaceAll("\s+","%20");
                                    response=getJsonFromUrl(ask);

                                    coord=parseJson(response);
                                    if(coord!=null) {
                                    	response=getJsonAutocomplete(cell1,coord);
                                    	//System.out.println(response); //imprime el contenido del json
                                    	response=parseJsonAutocomplete(response);
                                    	System.out.println("Test response "+response);
                                    	if(response!=null) {
                                    		System.out.println("Nombre "+response);
                                            ask="https://maps.googleapis.com/maps/api/geocode/json?address="+response+"&key="+apiKey;
                                            //http connection
                                            System.out.println(ask);
                                            //bien
                                            ask=ask.replaceAll("\s+","%20");
                                            response=getJsonFromUrl(ask);
                                            System.out.println(response);
                                            if(comprobResultJSON(response)) {
                                            	//System.out.println(response); //imprime el contenido del json

                                                coord=parseJson(response);
                                                System.out.println("here"+coord);

                                                if(coord!=null) {
                                                	System.out.println(coord);

                                                    if(comprobComunidad(coord,row)) {
                                                    	writeCell(workbook,row,coord);		//escribe coordenada en el excel
                                                    }else {
                                                    	resaltarCelda(workbook,row,coord);
                                                    }
                                                    System.out.println(coord);
                                                }else {
                                                	resaltarCelda(workbook,row,coord);
                                                }
                                            }else {
                                            	resaltarCelda(workbook,row,coord);
                                            }

                                    	}else {
                                    		resaltarCelda(workbook,row,coord);
                                    	}
                                    }
                                }else {
                                	System.out.println("No es un polígono industrial");
                                	//caso empresa que no se encuentra en polígono industrial
                                	ask=cell3+"%20"+cell2;

                                    System.out.println(ask);
                                    response=getJsonSearchText(ask);
                                    coord=parseJsonText(response);

                                    if(coord!=null) {
                                    	ask="https://maps.googleapis.com/maps/api/geocode/json?address="+coord+"&key="+apiKey;
                                        ask=ask.replaceAll("\s+","%20");
                                        //http connection
                                        System.out.println(ask);
                                        response=getJsonFromUrl(ask);
                                        //System.out.println(response); imprime el contenido del json
                                        coord=parseJson(response);
                                        System.out.println(coord);  //test delete
                                    }
                                }/*
                                if(comprobResultJSON(response)) {
                                	if(!parseJSONrooftop(response)) {
                                    	coord=null;
                                    	System.out.println("Coordenada eliminada rooftop");
                                    }
                                }
								*/
                                if(coord==null){
                                	boolean nombre=true;
                                	String aux;
                                	System.out.println("Probando de nuevo con nombre de empresa");
                                    String cell1;
                                    cell1 = row.getCell(1).getStringCellValue();
                                    cell3=row.getCell(3).getStringCellValue();
                                    cell2=row.getCell(2).getStringCellValue();
                                    ask=cell1+" "+cell2+" "+cell3;
                                    System.out.println(ask);
                                    response=getJsonSearchText(ask);

                                    coord=parseJsonText(response);
                                    if(coord!=null) {
                                    	System.out.println(response); //imprime el contenido del json
                                        System.out.println("Nombre "+coord);
                                        coord=coord.replaceAll("\s+","%20");
                                        ask="https://maps.googleapis.com/maps/api/geocode/json?address="+coord+"&key="+apiKey;
                                        //http connection
                                        System.out.println(ask);
                                        //bien
                                        response=getJsonFromUrl(ask);

                                        System.out.println(response); //imprime el contenido del json

                                        coord=parseJson(response);

                                        System.out.println(coord);
                                    }
                                    if(comprobComunidad(coord,row)) {
                                    	writeCell(workbook,row,coord);		//escribe coordenada en el excel
                                    }else {
                                    	resaltarCelda(workbook,row,coord);
                                    }

                                    System.out.println(coord);  //test delete
                                }else {
                                	writeCell(workbook,row,coord);		//escribe coordenada en el excel
                                }
                            }
                    }
                cont++;
                }
            inputStream.close();
        } catch (IOException | NullPointerException e) {
        	System.out.println("Error"+e.getMessage());
        }
    }

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
			con.addRequestProperty("X-Goog-Api-Key", apiKey);
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
            con.setRequestProperty("X-Goog-Api-Key", apiKey); // Añadir la cabecera de la clave API
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

    public static void writeCell(Workbook w,Row r,String c) {
    	/*
    	 Esta función escribe la coordenada en el excel
    	 */
    	// Cambiamos las comas por puntos y dividimos el string para poner los valores de la longitud y latitud en celdas distintas
    	if(c!=null) {
    		String[] coordenada = c.split(",");
        	coordenada[0]=coordenada[0].replaceAll(",",".");
        	coordenada[1]=coordenada[1].replaceAll(",",".");
        	//latitud
        	Cell cell = r.createCell(8);
            cell.setCellValue(coordenada[1]);
            
            //longitud
            cell = r.createCell(9);
            cell.setCellValue(coordenada[0]);
            
            //conjunto
            //cell = r.createCell(10);
            //cell.setCellValue(c);

            try (FileOutputStream fileOut = new FileOutputStream(fileWrite)) {
                w.write(fileOut);
            } catch (IOException e) {
                System.out.println("Error al escribir al excel"+e.getStackTrace());
            }
            System.out.println("-------------------------------------");
    	}
    }

    public static void resaltarCelda(Workbook w,Row r,String c) {
    	/*
    	 Esta función escribe el contenido de la coordenada al excel y escribe un mensaje para avisar un avisado manual.
    	 */
        // Cambiamos las comas por puntos y dividimos el string para poner los valores de la longitud y latitud en celdas distintas
    	String[] coordenada;
    	Cell cell;
    	if(c!=null) {
    		coordenada = c.split(",");
        	coordenada[0]=coordenada[0].replaceAll(",",".");
        	coordenada[1]=coordenada[1].replaceAll(",",".");
        	//latitud
        	//CellStyle style=w.createCellStyle();
        	cell = r.createCell(8);
            cell.setCellValue(coordenada[1]);
            
            //longitud
            cell = r.createCell(9);
            cell.setCellValue(coordenada[0]);
            
            //Crear y aplicar el estilo de resaltado
            //CellStyle headerStyle = w.createCellStyle();
            //headerStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
            //headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //cell.setCellStyle(headerStyle);

            cell = r.createCell(10);
            cell.setCellValue("ALERTA, POSIBLE ERROR");

            writeCell(w,r,c);

            System.out.println("Celda resaltada correctamente");
    	}
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
        	System.out.println("https://maps.googleapis.com/maps/api/geocode/json?address="+coord+"&key="+apiKey);
        	System.out.println(getJsonFromUrl("https://maps.googleapis.com/maps/api/geocode/json?address="+coord+"&key="+apiKey));
        	codPost=parseAdressSimple(getJsonFromUrl("https://maps.googleapis.com/maps/api/geocode/json?address="+coord+"&key="+apiKey));
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
