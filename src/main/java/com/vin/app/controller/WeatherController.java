package com.vin.app.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.vin.app.model.Forecast;
import com.vin.app.model.Util;
import com.vin.app.model.Weather;
 
@Controller
public class WeatherController {
	
	@RequestMapping("/weather")
	public @ResponseBody Map<String,Forecast> getWeather(
			@RequestParam(value = "country", required = false, defaultValue = "London") String country) {
		Map<String,Forecast> forecast=null;
		List<Weather> weatherList=null;
		try{		
		String xmlContent=getXmlContentFromWeatherOrg();
		weatherList= getWeatherFromXml(xmlContent);
		forecast = groupAndGetSuggestion(weatherList);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return forecast;
	}
	
	@RequestMapping("/full")
	public @ResponseBody List<Weather> getFullWeather(){
		List<Weather> weatherList=null;
		try{		
		String xmlContent=getXmlContentFromWeatherOrg();
		weatherList= getWeatherFromXml(xmlContent);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return weatherList;
	}
	
	private Map<String,Forecast> groupAndGetSuggestion(List<Weather> weatherList)
	{
		List<Forecast> forecastList=null;
		Map<String,Forecast> forecastMap=null;
		try{
			if(weatherList!=null){				
				forecastMap=new HashMap<String,Forecast>();				
				for(Weather weather:weatherList)
				{				
					
					String strDate=Util.getDate(weather.getDate());					
					if(!forecastMap.containsKey(strDate)){
					Forecast forecast=new Forecast();
					forecast.setMinTemp(weather.getMinTemp());
					forecast.setMaxTemp(weather.getMaxTemp());
					forecast.setDate(strDate);
					forecastMap.put(strDate, forecast);
					}
					else
					{
						Forecast forecast = forecastMap.get(strDate);
						forecast.setDate(strDate);
						Double avgMinTemp = Util.avg(forecast.getMinTemp(), weather.getMinTemp());//(forecast.getMinTemp().doubleValue()+weather.getMinTemp().doubleValue())/2;
						Double avgMaxTemp = Util.avg(forecast.getMaxTemp(),weather.getMaxTemp());
						forecast.setMinTemp(avgMinTemp);
						forecast.setMaxTemp(avgMaxTemp);
						if(weather.getWeatherType().contains("rain"))
						{
							forecast.setSuggestion("Carry umbrella");
						}
						else {							
							Double maxTempCelsius = avgMaxTemp-273.0;
							if(maxTempCelsius>40)
							{
								forecast.setSuggestion("Use sunscreen lotion");
							}
						}
						forecastMap.put(strDate, forecast);
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return forecastMap;
	}
	
	private String getXmlContentFromWeatherOrg()
	{
		String xmlContent=null;
		try{		
			URL url = new URL("https://samples.openweathermap.org/data/2.5/forecast?q=London,us&mode=xml&appid=b6907d289e10d714a6e88b30761fae22");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");		
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer content = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
					    content.append(inputLine);
					}
					in.close();				
					xmlContent=content.toString();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		return xmlContent;
	}
	
	private List<Weather> getWeatherFromXml(String content)
	{
		List<Weather> weatherList=new ArrayList<Weather>();
		try{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();      
        Document doc = dBuilder.parse(new InputSource(new StringReader(content)));
        doc.getDocumentElement().normalize();       
        NodeList nList = doc.getElementsByTagName("forecast");
        for (int temp = 0; temp < nList.getLength(); temp++) {        	
        	Node foreCastNode = nList.item(temp);
            if (foreCastNode.getNodeType() == Node.ELEMENT_NODE) {
            	 Element foreCastElement = (Element) foreCastNode;
            	 NodeList timeList=foreCastElement.getElementsByTagName("time");
            	 for(int t=0;t<timeList.getLength();t++)
            	 {
            		 Node timeNode = timeList.item(t);
            		 if (timeNode.getNodeType() == Node.ELEMENT_NODE) {
            			 Element timeElement=(Element) timeNode;
            			 String fromDate=timeElement.getAttribute("from");
            			 Weather weather=new Weather();
            			 NodeList tempList=timeElement.getElementsByTagName("temperature");
            			 for(int te=0;te<tempList.getLength();te++){
            				Node tempNode=tempList.item(te);
            				if(tempNode.getNodeType() ==  Node.ELEMENT_NODE){
            				Element tempElement=(Element) tempNode;
            				weather.setMinTemp(Double.valueOf(tempElement.getAttribute("min")));
            				weather.setMaxTemp(Double.valueOf(tempElement.getAttribute("max")));
            				}
            			 }
            			 NodeList symbolList=timeElement.getElementsByTagName("symbol");
            			 for(int te=0;te<symbolList.getLength();te++){
            				Node symbolNode=symbolList.item(te);
            				if(symbolNode.getNodeType() ==  Node.ELEMENT_NODE){
            				Element symbolElement=(Element) symbolNode;
            				weather.setWeatherType(symbolElement.getAttribute("name"));
            				}
            			 }
            			 weather.setDate(fromDate);
                         weatherList.add(weather);
            		 }
                 }
            }
          }
		}
		catch(Exception ex){
		ex.printStackTrace();	
		}
		return weatherList;
	}
	
}
