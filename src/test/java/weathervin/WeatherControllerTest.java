package weathervin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.vin.app.controller.WeatherController;
import com.vin.app.model.Forecast;
import com.vin.app.model.Weather;

@RunWith(PowerMockRunner.class)
@PrepareForTest(WeatherController.class)
public class WeatherControllerTest {
	
	
@Test
public void testGetWeather()throws Exception{
	WeatherController wc=PowerMock.createPartialMockForAllMethodsExcept(WeatherController.class, "getWeather");
	String xmlContent = PowerMock.createMock(String.class);
	PowerMock.expectPrivate(wc,"getXmlContentFromWeatherOrg").andReturn(xmlContent);	
	List<Weather> weatherList=new ArrayList<Weather>();
	PowerMock.expectPrivate(wc, "getWeatherFromXml",xmlContent).andReturn(weatherList);	
	Map<String,Forecast> forecast= new HashMap<String,Forecast>();
	PowerMock.expectPrivate(wc,"groupAndGetSuggestion",weatherList).andReturn(forecast);
	PowerMock.replayAll();
	wc.getWeather("London");
	PowerMock.verifyAll();
}
}
