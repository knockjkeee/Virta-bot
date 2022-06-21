package ru.newsystems.webservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.newsystems.basecore.model.domain.TickerJ;
import ru.newsystems.basecore.model.domain.Ticket;
import ru.newsystems.basecore.model.dto.TicketDTOJ;

import java.util.Objects;
import java.util.Optional;

@Service
public class RestService {

    private final RestTemplate restTemplate;

    @Value("${nis.pro.url}")
    private String baseUrl;
    @Value("${nis.pro.path}")
    private String path;
    @Value("${nis.pro.login}")
    private String login;
    @Value("${nis.pro.password}")
    private String password;

    public RestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<TickerJ> getTicketGET(String id) {
//    String url = "http://192.168.246.218/otrs/nph-genericinterface.pl/Webservice/Ticket/TicketGet?UserLogin=PRa&Password=pr";
        String url = baseUrl + path + "TicketGet?UserLogin=" + login + "&Password=" + password;
        HttpEntity<MultiValueMap<String, String>> request = getRequestTickerGet(id);
        ResponseEntity<TicketDTOJ> exchange = restTemplate.exchange(url, HttpMethod.POST, request, TicketDTOJ.class);
        if (exchange.getStatusCode() == HttpStatus.OK) {
            return Optional.ofNullable(Objects.requireNonNull(exchange.getBody()).getTicket().get(0));
        } else {
            return Optional.empty();
        }
    }

    public HttpEntity<MultiValueMap<String, String>> getRequestTickerGet(String id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("TicketID", id);
        map.add("Extended", "1");
        map.add("AllArticles", "1");
        map.add("Attachments", "1");
        return new HttpEntity<>(map, headers);
    }

}
