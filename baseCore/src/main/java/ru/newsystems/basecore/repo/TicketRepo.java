package ru.newsystems.basecore.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.newsystems.basecore.model.db.Ticket;

import java.util.Optional;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTn(String name);
}
