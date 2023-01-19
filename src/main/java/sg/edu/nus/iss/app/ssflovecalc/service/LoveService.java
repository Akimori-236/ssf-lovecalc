package sg.edu.nus.iss.app.ssflovecalc.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import sg.edu.nus.iss.app.ssflovecalc.model.LoveResult;

@Service
public class LoveService {
    private static final String LOVE_CALC_URL = "https://love-calculator.p.rapidapi.com/getPercentage";
    // private static final String apiKey = System.getenv("OPEN_WEATHER_MAP_API_KEY");
    private static final String apiKey = "05ab966dd7mshc7ebbedf6f1c5e6p1e8949jsnc03936a8d5f0";
    public Optional<LoveResult> getCalculation(String fname, String sname) throws IOException {

        // build the URL
        String calculatorUrl = UriComponentsBuilder
                .fromUriString(LOVE_CALC_URL)
                .queryParam("fname",
                        fname.replaceAll(" ", "+"))
                .queryParam("sname",
                        sname.replaceAll(" ", "+"))
                .toUriString();
        System.out.println("URL >>> " + calculatorUrl);

        ResponseEntity<String> resp = testHeader(calculatorUrl);

        LoveResult lr = LoveResult.create(resp.getBody());
        System.out.println(lr);
        if(lr != null)
            return Optional.of(lr);                        
        return Optional.empty();
    }

    public ResponseEntity<String> testHeader(String UrlString){
        RestTemplate restTemplate = new RestTemplate();
        //Set the headers you need send
        final HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", "love-calculator.p.rapidapi.com");

        //Create a new HttpEntity
        final HttpEntity<String> entity = new HttpEntity<String>(headers);
        
        //Execute the method writing your HttpEntity to the request
        ResponseEntity<String> response = restTemplate.exchange(UrlString, HttpMethod.GET, entity, String.class);        
        // System.out.println(response.getBody());
        return response;
    }
}
