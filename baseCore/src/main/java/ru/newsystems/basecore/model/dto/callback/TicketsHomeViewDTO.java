package ru.newsystems.basecore.model.dto.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Getter
@Setter
public class TicketsHomeViewDTO extends SerializableInlineObject {

  @JsonProperty("p")
  private int page;

  public TicketsHomeViewDTO() {
    super(SerializableInlineType.TICKET_HOME);
  }

  public TicketsHomeViewDTO(int page) {
    this();
    this.page = page;
  }
}
