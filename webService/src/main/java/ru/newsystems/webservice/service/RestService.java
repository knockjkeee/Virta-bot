package ru.newsystems.webservice.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.newsystems.basecore.model.dto.domain.RequestUpdateDTO;
import ru.newsystems.basecore.model.dto.domain.TicketGetDTO;
import ru.newsystems.basecore.model.dto.domain.TicketSearchDTO;
import ru.newsystems.basecore.model.dto.domain.TicketUpdateDTO;

import java.util.List;
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

    public Optional<TicketGetDTO> getTicketOperationGet(List<Long> id) {
//    String url = "http://192.168.246.218/otrs/nph-genericinterface.pl/Webservice/Ticket/TicketGet?UserLogin=PRa&Password=pr";
        String urlGet = getUrl("TicketGet?UserLogin=");
        HttpEntity<MultiValueMap<String, Object>> requestEntity = getRequestHeaderTickerGet(id);
        ResponseEntity<TicketGetDTO> response = restTemplate.exchange(urlGet, HttpMethod.POST, requestEntity, TicketGetDTO.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Optional.ofNullable(response.getBody());
        } else {
            return Optional.empty();
        }
    }

    public Optional<TicketSearchDTO> getTicketOperationSearch(List<Long> listTicketNumbers) {
        String urlSearch = getUrl("TicketSearch?UserLogin=");
        HttpEntity<MultiValueMap<String, Object>> requestEntity = getRequestHeaderTickerSearch(listTicketNumbers);
        ResponseEntity<TicketSearchDTO> response = restTemplate.exchange(urlSearch, HttpMethod.POST, requestEntity, TicketSearchDTO.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Optional.ofNullable(response.getBody());
        } else {
            return Optional.empty();
        }
    }

    public Optional<TicketUpdateDTO> getTicketOperationUpdate(RequestUpdateDTO data) {
        String urlUpdate = getUrl("TicketUpdate?UserLogin=");
        HttpEntity<MultiValueMap<String, Object>> requestEntity = getRequestHeaderTickerUpdate(data);
        ResponseEntity<TicketUpdateDTO> response = restTemplate.exchange(urlUpdate, HttpMethod.POST, requestEntity, TicketUpdateDTO.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Optional.ofNullable(response.getBody());
        } else {
            return Optional.empty();
        }
    }


    public Optional<TicketSearchDTO> getTicketOperationSearch() {
        String url = getUrl("TicketSearch?UserLogin=");
        ResponseEntity<TicketSearchDTO> response = restTemplate.exchange(url, HttpMethod.POST, null, TicketSearchDTO.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Optional.ofNullable(response.getBody());
        } else {
            return Optional.empty();
        }
    }

    public HttpEntity<MultiValueMap<String, Object>> getRequestHeaderTickerGet(List<Long> listId) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("Extended", "1");
        map.add("AllArticles", "1");
        map.add("Attachments", "1");
        if (listId != null) listId.forEach(e -> map.add("TicketID", e));
        return new HttpEntity<>(map, getHttpHeaders(MediaType.APPLICATION_JSON));
    }

    public HttpEntity<MultiValueMap<String, Object>> getRequestHeaderTickerSearch() {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        return new HttpEntity<>(map, getHttpHeaders(MediaType.APPLICATION_JSON));
    }


    public HttpEntity<MultiValueMap<String, Object>> getRequestHeaderTickerUpdate(RequestUpdateDTO data) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        MultiValueMap<String, Object> article = new LinkedMultiValueMap<>();
        article.add("ContentType", "text/plain; charset=utf8");
        article.add("Subject", data.getArticle().getSubject());
        article.add("Body", data.getArticle().getBody());
        article.add("To", data.getArticle().getTo());

        map.add("TicketNumber", data.getTicketNumber());
        map.add("CommunicationChannelID", data.getCommunicationChannelID());
        map.add("Article", article);

        if (data.getAttaches().size() > 0) {
            data.getAttaches().forEach(e -> {
                MultiValueMap<String, Object> attach = new LinkedMultiValueMap<>();
                attach.add("Content", e.getContent());
                attach.add("ContentType", e.getContentType());
                attach.add("Filename", e.getFilename());
                map.add("Attachment", attach);
            });
        }
        return new HttpEntity<>(map, getHttpHeaders(MediaType.APPLICATION_JSON));
    }


    public HttpEntity<MultiValueMap<String, Object>> getRequestHeaderTickerSearch(List<Long> listTicketNumbers) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        listTicketNumbers.forEach(e -> map.add("TicketNumber", e));
        return new HttpEntity<>(map, getHttpHeaders(MediaType.APPLICATION_JSON));
    }

    @NotNull
    private HttpHeaders getHttpHeaders(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return headers;
    }

    @NotNull
    private String getUrl(String operation) {
        return baseUrl + path + operation + login + "&Password=" + password;
    }
}
