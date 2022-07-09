package ru.newsystems.basecore.model.dto.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Getter
@Setter
public class DownloadFilesDTO extends SerializableInlineObject {
  @JsonProperty("m")
  private String messageId;

  public DownloadFilesDTO() {
    super(SerializableInlineType.DOWNLOAD);
  }

  public DownloadFilesDTO(String messageId) {
    this();
    this.messageId = messageId;
  }
}
