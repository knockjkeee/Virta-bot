package ru.newsystems.basecore.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.newsystems.basecore.model.Ticket;
import ru.newsystems.basecore.model.dto.CommandUpdateDTO;
import ru.newsystems.basecore.repo.RepoService;

import java.util.Optional;

import static ru.newsystems.basecore.utils.NumberUtil.getId;

@Component
public class ReceivedText {
    private final RepoService repo;

    public ReceivedText(RepoService repo) {
        this.repo = repo;
    }

    public String getTextFromMessage(CommandUpdateDTO cUpdate) {
        String text = cUpdate.getCommand().getText();
        long id = 0;
        try {
            id = getId(text);
            Optional<Ticket> byId = repo.findById(id);
            return byId.map(this::prepareTextFromMessage)
                    .orElseGet(() -> "Search item: "
                            + text
                            + " not found, please try again with correct id ticker number");
        } catch (NumberFormatException e) {
            return "id " + id + " " + e.getLocalizedMessage();
        }
    }

    public String getTextFromCommandStart(CommandUpdateDTO cUpdate) {
        return "";
    }

    public String getTextFromCommandHelp(CommandUpdateDTO cUpdate) {
        return "";
    }

    public String getTextFromCommandCreateTicket(CommandUpdateDTO cUpdate) {
        return "";
    }

    public String getTextFromCommandMyId(CommandUpdateDTO cUpdate) {
        return prepareTextFromCommandMyId(cUpdate.getUpdate().getMessage().getFrom());
    }

    public String getTextFromCommandRegistration(CommandUpdateDTO cUpdate) {
        return "";
    }


    private String prepareTextFromMessage(Ticket ticket) {
        return "TicketNumber: " + ticket.getTn() + "\nTitle: " + ticket.getTitle() + "\nQueue: " + ticket.getQueue()
                .getName() + "\nStateType: " + ticket.getTicketState().getName();
    }

    private String prepareTextFromCommandMyId(User user) {
        return "Id: "
                + user.getId()
                + "\nUser name: "
                + user.getUserName()
                + "\nFirst name: "
                + user.getFirstName()
                + "\nLast name: "
                + user.getLastName()
                + "\nLanguage: "
                + user.getLanguageCode();
    }
}
