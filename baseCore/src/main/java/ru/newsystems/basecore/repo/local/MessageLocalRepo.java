package ru.newsystems.basecore.repo.local;

import org.springframework.stereotype.Component;
import ru.newsystems.basecore.model.dto.MessageUpdateDTO;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class MessageLocalRepo {

    ConcurrentMap<Long, MessageUpdateDTO> repo = new ConcurrentHashMap<>();

    public void add(Long id, MessageUpdateDTO data) {
        repo.put(id, data);
    }

    public void remove(Long id) {
        repo.remove(id);
    }

    public void update(Long id, MessageUpdateDTO data) {
        MessageUpdateDTO old = repo.get(id);
        if (old == null) {
            repo.put(id, data);
        } else {
            repo.replace(id, old, data);
        }
    }

    public MessageUpdateDTO get(Long id) {
        return repo.get(id);
    }
}
