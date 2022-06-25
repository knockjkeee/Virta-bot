package ru.newsystems.basecore.model.state;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum MessageState {
    SHOW("Показать"),
    EXIT("Закрыть"),
    SENDCOMMENT("Отправить сообщение"),
    DOWLOADFILE("Выгрузить документы из последнего сообщения"),
    NONE("Empty");

    private String name;

    MessageState(String name) {
        this.name = name;
    }

    public static MessageState getState(String value) {
        return switch (value) {
            case "Показать" -> SHOW;
            case "Закрыть" -> EXIT;
            case "Отправить сообщение" -> SENDCOMMENT;
            case "Выгрузить документы из последнего сообщения" -> DOWLOADFILE;
            default -> NONE;
        };
    }
}
