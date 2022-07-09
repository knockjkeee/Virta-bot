package ru.newsystems.basecore.model.dto.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.newsystems.basecore.model.domain.Article;
import ru.newsystems.basecore.model.domain.Attachment;

import java.util.List;

@Data
@RequiredArgsConstructor
public class RequestCreateDTO {
    private Long ticketNumber;
    private Article article;
    List<Attachment> attaches;
}
