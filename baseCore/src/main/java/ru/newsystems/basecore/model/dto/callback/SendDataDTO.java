package ru.newsystems.basecore.model.dto.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Getter
@Setter
public class SendDataDTO extends SerializableInlineObject {
  @JsonProperty("m")
  private String ticketId;

  public SendDataDTO() {
    super(SerializableInlineType.SEND_COMMENT);
  }

  public SendDataDTO(String ticketId) {
    this();
    this.ticketId = ticketId;
  }
}
