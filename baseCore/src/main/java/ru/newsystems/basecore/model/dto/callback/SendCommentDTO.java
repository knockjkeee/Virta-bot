package ru.newsystems.basecore.model.dto.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Getter
@Setter
public class SendCommentDTO extends SerializableInlineObject {
  @JsonProperty("m")
  private String messageId;

  public SendCommentDTO() {
    super(SerializableInlineType.SEND_COMMENT);
  }

  public SendCommentDTO(String messageId) {
    this();
    this.messageId = messageId;
  }
}
