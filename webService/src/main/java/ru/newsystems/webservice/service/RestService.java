package ru.newsystems.webservice.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.newsystems.basecore.model.domain.Article;
import ru.newsystems.basecore.model.dto.domain.RequestUpdateDTO;
import ru.newsystems.basecore.model.dto.domain.TicketGetDTO;
import ru.newsystems.basecore.model.dto.domain.TicketSearchDTO;
import ru.newsystems.basecore.model.dto.domain.TicketUpdateDTO;

import java.util.*;

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
        HttpEntity<Map<String, Object>> requestEntity = getRequestHeaderTickerUpdate(data);
        ResponseEntity<TicketUpdateDTO> response = restTemplate.exchange(urlUpdate, HttpMethod.POST, requestEntity, TicketUpdateDTO.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Optional.ofNullable(response.getBody());
        } else {
            return Optional.empty();
        }
    }

    private String getReq(Article article) {
        StringBuilder res = new StringBuilder();
        res.append("{");
        res.append("\"ContentType\":\"text/plain; charset=utf8\",");
        res.append("\"Subject\":\"").append(article.getSubject()).append("\",");
        res.append("\"Body\":\"").append(article.getBody()).append("\"}");
//        res.append("\"To\":").append(article.getTo()).append("}");
        return res.toString();
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


    public  HttpEntity<Map<String, Object>> getRequestHeaderTickerUpdate(RequestUpdateDTO data) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> arc = new HashMap<>();

        arc.put("ContentType", "text/plain; charset=utf8");
        arc.put("Subject", data.getArticle().getSubject());
        arc.put("Body", data.getArticle().getBody());

        map.put("TicketNumber", data.getTicketNumber());
        map.put("CommunicationChannelID", 4);
        map.put("Article", arc);
//
        if (data.getAttaches() != null && data.getAttaches().size() > 0) {
            List<Object> obj = new ArrayList<>();

            data.getAttaches().forEach(e -> {
                Map<String, Object> attach = new HashMap<>();
                attach.put("Content", e.getContent());
                attach.put("ContentType", e.getContentType());
                attach.put("Filename", e.getFilename());
                obj.add(attach);
//                map.put("Attachment", attach);
            });
            map.put("Attachment", obj);
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
