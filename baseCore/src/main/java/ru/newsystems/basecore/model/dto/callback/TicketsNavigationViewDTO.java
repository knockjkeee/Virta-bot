package ru.newsystems.basecore.model.dto.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Getter
@Setter
public class TicketsNavigationViewDTO extends SerializableInlineObject {

//  @JsonProperty("s")
//  private int size;

  @JsonProperty("p")
  private int page;

  @JsonProperty("d")
  private String direction;


  public TicketsNavigationViewDTO() {
    super(SerializableInlineType.TICKETS_NAVIGATION);
  }

  public TicketsNavigationViewDTO(int page, String direction ) {
    this();
//    this.size = size;
    this.page = page;
    this.direction = direction;
  }
}
