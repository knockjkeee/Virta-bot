package ru.newsystems.basecore.model.dto.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Getter
@Setter
public class TicketsViewDTO extends SerializableInlineObject {

  @JsonProperty("s")
  private int size;

  @JsonProperty("c")
  private int page;

  @JsonProperty("d")
  private String direction;


  public TicketsViewDTO() {
    super(SerializableInlineType.TICKETS_VIEW);
  }

  public TicketsViewDTO(int page, int size, String direction ) {
    this();
    this.size = size;
    this.page = page;
    this.direction = direction;
  }
}
