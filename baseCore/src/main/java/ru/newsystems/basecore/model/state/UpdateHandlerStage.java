package ru.newsystems.basecore.model.state;

public enum UpdateHandlerStage {
    CALLBACK,
    TICKET,
    ID,
    ARTICLE,
    MESSAGE,
    COMMAND,
    REPLY_BUTTON;

    public int getOrder() {
        return ordinal();
    }
}
