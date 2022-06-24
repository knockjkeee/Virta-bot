package ru.newsystems.webservice.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.newsystems.basecore.model.domain.TickerJ;
import ru.newsystems.basecore.model.dto.CommandUpdateDTO;
import ru.newsystems.basecore.model.dto.TicketGetDTO;
import ru.newsystems.basecore.model.dto.TicketSearchDTO;
import ru.newsystems.basecore.repo.RepoService;
import ru.newsystems.webservice.service.RestService;

import java.util.List;
import java.util.Optional;

import static ru.newsystems.basecore.utils.NumberUtil.getIdByTicketNumber;

@Component
public class ReceivedText {
    private final RepoService repo;
    private final RestService restService;

    public ReceivedText(RepoService repo, RestService restService) {
        this.repo = repo;
        this.restService = restService;
    }

    public String getTextToMessage(Update update) {
        String text = update.getMessage().getText();
        long id;
        try {
            id = getIdByTicketNumber(text);
            //query from bd
            //TODO change to rest
//            Optional<Ticket> tickerByBD = id != 0 ? repo.findByTicketNumber(text) : Optional.empty();
            Optional<TicketSearchDTO> ticketSearch = id != 0 ? restService.getTicketOperationSearch(List.of(id)) : Optional.empty();
            List<Long> ticketsId;
            if (ticketSearch.isPresent()) {
                ticketsId = ticketSearch.get().getTicketIDs();
            } else {
                return "Search ticket by search operation: " + text + " not found, please try again with correct ticker number";
            }
            Optional<TicketGetDTO> ticket = restService.getTicketOperationGet(ticketsId);
            if (ticket.isPresent()) {
                if (ticket.get().getError() == null) {
                    return prepareTextFromMessage(ticket.get().getTickets().get(0));
                } else {
                    return "ErrorCode: "+ ticket.get().getError().getErrorCode() + ""
                            + "\nErrorMessage: " + ticket.get().getError().getErrorMessage()+ ""
                            + "\nby text: " + text;
                }
            } else {
                return "Search ticket by get operation: " + text + " not found, please try again with correct ticker number";
            }
        } catch (NumberFormatException e) {
            return "NumberFormatException, " + e.getLocalizedMessage() + ", by text: " + text;
        }
    }


    public String getTextToMessage(CommandUpdateDTO cUpdate) {
        String text = cUpdate.getCommand().getText();
        long id;
        try {
            id = getIdByTicketNumber(text);
            //query from bd
            //TODO change to rest
//            Optional<Ticket> tickerByBD = id != 0 ? repo.findByTicketNumber(text) : Optional.empty();
            Optional<TicketSearchDTO> ticketSearch = id != 0 ? restService.getTicketOperationSearch(List.of(id)) : Optional.empty();
            List<Long> ticketsId;
            if (ticketSearch.isPresent()) {
                ticketsId = ticketSearch.get().getTicketIDs();
            } else {
                return "Search ticket by search operation: " + text + " not found, please try again with correct ticker number";
            }
            Optional<TicketGetDTO> ticket = restService.getTicketOperationGet(ticketsId);
            if (ticket.isPresent()) {
                if (ticket.get().getError() == null) {
                    return prepareTextFromMessage(ticket.get().getTickets().get(0));
                } else {
                    return "ErrorCode: "+ ticket.get().getError().getErrorCode() + ""
                            + "\nErrorMessage: " + ticket.get().getError().getErrorMessage()+ ""
                            + "\nby text: " + text;
                }
            } else {
                return "Search ticket by get operation: " + text + " not found, please try again with correct ticker number";
            }
        } catch (NumberFormatException e) {
            return "NumberFormatException, " + e.getLocalizedMessage() + ", by text: " + text;
        }
    }

    public String getTextFromCommandStart(CommandUpdateDTO cUpdate) {
        return "start";
    }

    public String getTextFromCommandHelp(CommandUpdateDTO cUpdate) {
        return prepareTextFromCommandHelp();
    }

    public String getTextFromCommandCreateTicket(CommandUpdateDTO cUpdate) {
        return "create";
    }

    public String getTextFromCommandMyId(CommandUpdateDTO cUpdate) {
        return prepareTextFromCommandMyId(cUpdate.getUpdate().getMessage().getFrom());
    }

    public String getTextFromCommandMyTicket(CommandUpdateDTO cUpdate) {
        return prepareTextFromCommandMyTicket(cUpdate);
    }


    private String prepareTextFromMessage(TickerJ ticket) {
        return "TicketNumber: "
                + ticket.getTicketNumber()
                + "\nTitle: "
                + ticket.getTitle()
                + "\nQueue: "
                + ticket.getQueue()
                + "\nStateType: "
                + ticket.getStateType();
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

    private String prepareTextFromCommandMyTicket(CommandUpdateDTO cUpdate) {

        return """
                Registration\s
                \tRegistration\s
                \t\tRegistration\s
                \t\t\tRegistration\s
                \t\t\t\tRegistration\s""";
    }
}
