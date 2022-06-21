package ru.newsystems.webservice.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.newsystems.basecore.model.domain.TickerJ;
import ru.newsystems.basecore.model.domain.Ticket;
import ru.newsystems.basecore.model.dto.CommandUpdateDTO;
import ru.newsystems.basecore.repo.RepoService;
import ru.newsystems.basecore.utils.NumberUtil;
import ru.newsystems.webservice.service.RestService;

import java.util.Optional;

import static ru.newsystems.basecore.utils.NumberUtil.getId;
import static ru.newsystems.basecore.utils.NumberUtil.getIdByTicketNumber;

@Component
public class ReceivedText {
    private final RepoService repo;
    private final RestService restService;

    public ReceivedText(RepoService repo, RestService restService) {
        this.repo = repo;
        this.restService = restService;
    }

    public String getTextFromMessage(CommandUpdateDTO cUpdate) {
        String text = cUpdate.getCommand().getText();
        long id = 0;
        try {
            id = getId(text);
            if (id == 0) {
                id = getIdByTicketNumber(text);
            }
            Optional<Ticket> tickerByBD = text.length() <= NumberUtil.LENGTH_CHAR_BY_ID ? repo.findById(id) : id
                    != 0 ? repo.findByTicketNumber(String.valueOf(id)) : Optional.empty();
            String ticketID = "0";
            if (tickerByBD.isPresent()) {
                ticketID = String.valueOf(tickerByBD.get().getId());
            } else {
                return "Search ticket: " + text + " not found, please try again with correct id ticker number";
            }
            Optional<TickerJ> ticketByGET = restService.getTicketGET(ticketID);
            return ticketByGET.map(this::prepareTextFromMessage)
                    .orElseGet(() -> "Search ticket: "
                            + text
                            + " not found, please try again with correct id ticker number");
        } catch (NumberFormatException e) {
            return "NumberFormatException, " + e.getLocalizedMessage() + ", text: " + text;
        }
    }

    public String getTextFromCommandStart(CommandUpdateDTO cUpdate) {
        return "";
    }

    public String getTextFromCommandHelp(CommandUpdateDTO cUpdate) {
        return prepareTextFromCommandHelp();
    }

    public String getTextFromCommandCreateTicket(CommandUpdateDTO cUpdate) {
        return "";
    }

    public String getTextFromCommandMyId(CommandUpdateDTO cUpdate) {
        return prepareTextFromCommandMyId(cUpdate.getUpdate().getMessage().getFrom());
    }

    public String getTextFromCommandRegistration(CommandUpdateDTO cUpdate) {
        return prepareTextFromCommandRegistration();
    }


    private String prepareTextFromMessage(TickerJ ticket) {
        return "TicketNumber: "
                + ticket.getTicketNumberJ()
                + "\nTitle: "
                + ticket.getTitleJ()
                + "\nQueue: "
                + ticket.getQueueJ()
                + "\nStateType: "
                + ticket.getStateTypeJ();
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

    private String prepareTextFromCommandHelp() {
        return """
                HELP\s
                \tHELP\s
                \t\tHELP\s
                \t\t\tHELP\s
                \t\t\t\tHELP\s""";
    }

    private String prepareTextFromCommandRegistration() {
        return """
                Registration\s
                \tRegistration\s
                \t\tRegistration\s
                \t\t\tRegistration\s
                \t\t\t\tRegistration\s""";
    }
}
