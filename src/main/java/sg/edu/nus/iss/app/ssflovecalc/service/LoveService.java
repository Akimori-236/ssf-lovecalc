package sg.edu.nus.iss.app.ssflovecalc.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import sg.edu.nus.iss.app.ssflovecalc.model.LoveResult;
import sg.edu.nus.iss.app.ssflovecalc.repository.LoveResultsRepo;

@Service
public class LoveService {
    private static final String LOVE_CALC_URL = "https://love-calculator.p.rapidapi.com/getPercentage";


    @Autowired
    private LoveResultsRepo resultsRepo;

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

        // get GET response entity
        ResponseEntity<String> resp = getResponse(calculatorUrl);
        // get body from GET response
        LoveResult lr = LoveResult.create(resp.getBody());
        System.out.println("Created object >>> " + lr);
        if (lr != null) {
            resultsRepo.saveResult(lr);
            return Optional.of(lr);
        }
        return Optional.empty();
    }

    public ResponseEntity<String> getResponse(String UrlString) {
        RestTemplate restTemplate = new RestTemplate();
        // set headers for GET request
        final HttpHeaders headers = new HttpHeaders();
            String apiKey = System.getenv("${LOVER_API_KEY}");
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", "love-calculator.p.rapidapi.com");

        // create the GET request HTTP Entity with the desired headers
        final HttpEntity<String> entity = new HttpEntity<String>(headers);
        // Execute the method writing your HttpEntity to the request
        ResponseEntity<String> response = restTemplate.exchange(UrlString, HttpMethod.GET, entity, String.class);
        return response;
    }

    public List<LoveResult> getAllResults(int startIndex) {
        return resultsRepo.getAllResults(startIndex);
    }
}
