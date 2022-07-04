package ru.newsystems.webservice.task;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SendLocalRepo {

    ConcurrentMap<Long, SendUpdateDTO> repo = new ConcurrentHashMap<>();

    public void add(Long id, SendUpdateDTO data) {
        repo.put(id, data);
    }

    public void remove(Long id) {
        repo.remove(id);
    }

    public void update(Long id, SendUpdateDTO data) {
        SendUpdateDTO old = repo.get(id);
        if (old == null) {
            repo.put(id, data);
        } else {
            repo.replace(id, old, data);
        }
    }

    public SendUpdateDTO get(Long id) {
        return repo.get(id);
    }

}
