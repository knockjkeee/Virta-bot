package ru.newsystems.basecore.model.dto.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Getter
@Setter
public class ArticlesViewDTO extends SerializableInlineObject {
  @JsonProperty("t")
  private String ticketId;

  public ArticlesViewDTO() {
    super(SerializableInlineType.ARTICLES_VIEW);
  }

  public ArticlesViewDTO(String ticketId) {
    this();
    this.ticketId = ticketId;
  }
}
