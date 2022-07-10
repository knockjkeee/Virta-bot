package ru.newsystems.basecore.model.dto.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Getter
@Setter
public class TicketViewDTO extends SerializableInlineObject {
  @JsonProperty("m")
  private String messageId;

  @JsonProperty("s")
  private int size;

  @JsonProperty("c")
  private int currentItem;

  public TicketViewDTO() {
    super(SerializableInlineType.TICKET_VIEW);
  }

  public TicketViewDTO(String messageId, int currentItem, int size ) {
    this();
    this.messageId = messageId;
    this.size = size;
    this.currentItem = currentItem;
  }
}
