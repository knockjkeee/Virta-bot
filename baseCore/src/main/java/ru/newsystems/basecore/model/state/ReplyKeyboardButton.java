package ru.newsystems.basecore.model.state;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ReplyKeyboardButton {
    HOME("Домой \uD83C\uDFE0"),
    BACK("◀️ Назад"),
    TO("Дальше ▶️"),
    NONE("???");

    private String label;

    ReplyKeyboardButton(String label) {
        this.label = label;
    }

    public static ReplyKeyboardButton getState(String label) {
        return switch (label) {
            case "Домой" -> HOME;
            case "Назад" -> BACK;
            case "Дальше" -> TO;
            default -> NONE;
        };
    }

    public static Optional<ReplyKeyboardButton> parse(String name) {
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(name) || b.label.equalsIgnoreCase(name))
                .findFirst();
    }
}
