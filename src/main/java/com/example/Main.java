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
    public static String apiKey="";			//Rellenar la clave api
    static String fileWrite="../output.xlsx";
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
                                    response=com.example.conexionAPI.getJsonFromUrl(ask);

                                    coord=parseJSON.parseJson(response);
                                    if(coord!=null) {
                                    	response=conexionAPI.getJsonAutocomplete(cell1,coord);
                                    	//System.out.println(response); //imprime el contenido del json
                                    	response=parseJSON.parseJsonAutocomplete(response);
                                    	System.out.println("Test response "+response);
                                    	if(response!=null) {
                                    		System.out.println("Nombre "+response);
                                            ask="https://maps.googleapis.com/maps/api/geocode/json?address="+response+"&key="+apiKey;
                                            //http connection
                                            System.out.println(ask);
                                            //bien
                                            ask=ask.replaceAll("\s+","%20");
                                            response=com.example.conexionAPI.getJsonFromUrl(ask);
                                            System.out.println(response);
                                            if(validacion.comprobResultJSON(response)) {
                                            	//System.out.println(response); //imprime el contenido del json

                                                coord=parseJSON.parseJson(response);
                                                System.out.println("here"+coord);

                                                if(coord!=null) {
                                                	System.out.println(coord);

                                                    if(validacion.comprobComunidad(coord,row)) {
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
                                    response=com.example.conexionAPI.getJsonSearchText(ask);
                                    coord=parseJSON.parseJsonText(response);

                                    if(coord!=null) {
                                    	ask="https://maps.googleapis.com/maps/api/geocode/json?address="+coord+"&key="+apiKey;
                                        ask=ask.replaceAll("\s+","%20");
                                        //http connection
                                        System.out.println(ask);
                                        response=com.example.conexionAPI.getJsonFromUrl(ask);
                                        //System.out.println(response); imprime el contenido del json
                                        coord=parseJSON.parseJson(response);
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
                                    response=com.example.conexionAPI.getJsonSearchText(ask);

                                    coord=parseJSON.parseJsonText(response);
                                    if(coord!=null) {
                                    	System.out.println(response); //imprime el contenido del json
                                        System.out.println("Nombre "+coord);
                                        coord=coord.replaceAll("\s+","%20");
                                        ask="https://maps.googleapis.com/maps/api/geocode/json?address="+coord+"&key="+apiKey;
                                        //http connection
                                        System.out.println(ask);
                                        //bien
                                        response=com.example.conexionAPI.getJsonFromUrl(ask);

                                        System.out.println(response); //imprime el contenido del json

                                        coord=parseJSON.parseJson(response);

                                        System.out.println(coord);
                                    }
                                    if(validacion.comprobComunidad(coord,row)) {
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
}
