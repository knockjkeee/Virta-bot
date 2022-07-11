package ru.newsystems.basecore.model.dto.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Getter
@Setter
public class TicketsViewDTO extends SerializableInlineObject {
  @JsonProperty("m")
  private Long messageId;

  @JsonProperty("s")
  private int size;

  @JsonProperty("c")
  private int page;

  @JsonProperty("h")
  private boolean isHome;

  @JsonProperty("d")
  private String direction;


  public TicketsViewDTO() {
    super(SerializableInlineType.TICKETS_VIEW);
  }

  public TicketsViewDTO(Long messageId, int page, int size, boolean isHome, String direction ) {
    this();
    this.messageId = messageId;
    this.size = size;
    this.page = page;
    this.isHome = isHome;
    this.direction = direction;
  }
}
