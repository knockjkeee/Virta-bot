package ru.newsystems.basecore.repo;

import org.springframework.stereotype.Service;
import ru.newsystems.basecore.model.domain.Ticket;

import java.util.Optional;

@Service
public class RepoService {
    private final TicketRepo ticketRepo;

    public RepoService(TicketRepo ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    public Optional<Ticket> findById(Long id) {
        return ticketRepo.findById(id);
    }

    public Optional<Ticket> findByTicketNumber(String tn) {
        return ticketRepo.findByTn(tn);
    }
}
