package ru.newsystems.webservice.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.basecore.integration.Subscriber;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.integration.parser.CommandParser;
import ru.newsystems.basecore.model.dto.CommandUpdateDTO;
import ru.newsystems.basecore.model.dto.ParseDTO;

import java.util.Optional;

@Service
public class UpdateReceiveService implements Subscriber {

    private final VirtaBot bot;
    private final CommandParser commandParser;
    private final CommandService commandService;
    private final MessageService messageService;
    private final RestTemplate restTemplate;

    public UpdateReceiveService(VirtaBot bot, CommandParser commandParser, CommandService commandService, MessageService messageService, RestTemplate restTemplate) {
        this.commandService = commandService;
        this.messageService = messageService;
        this.restTemplate = restTemplate;
        bot.subscribe(this);
        this.bot = bot;
        this.commandParser = commandParser;
    }

    @SneakyThrows
    @Override
    public void handleEvent(Update update) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("TicketID", "117");
//        map.add("Extended", "1");
//        map.add("AllArticles", "1");
//        map.add("Attachments", "1");
//
//        String url = "http://192.168.246.218/otrs/nph-genericinterface.pl/Webservice/Ticket/TicketGet?UserLogin=PRa&Password=pr";
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
////        ResponseEntity<Ticket> exchange = restTemplate.exchange(url, HttpMethod.POST, request, Ticket.class);
////
////        Ticket ticket = restTemplate.postForObject(url, request, Ticket.class);
////        ResponseEntity<Ticket> ticketResponseEntity = restTemplate.postForEntity(url, request, Ticket.class);
//
//        ResponseEntity<TicketDTO> ticket = restTemplate.exchange(url, HttpMethod.POST, request, TicketDTO.class);

        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            Optional<ParseDTO> parseTextToCommand = commandParser.parseCommand(text);
            if (parseTextToCommand.isPresent()) {
                commandService.received(new CommandUpdateDTO(update, parseTextToCommand.get()));
            } else {
                messageService.received(new CommandUpdateDTO(update, new ParseDTO(null, text)));
            }
        }
    }
}

