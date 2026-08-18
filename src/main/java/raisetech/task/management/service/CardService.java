package raisetech.task.management.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import raisetech.task.management.entity.Card;
import raisetech.task.management.entity.Priority;
import raisetech.task.management.entity.Status;
import raisetech.task.management.repository.CardRepository;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }

    public Card createCard(String title, Priority priority, LocalDate dueDate) {
        Card card = new Card();
        card.setTitle(title);
        card.setStatus(Status.TODO);
        card.setPriority(priority != null ? priority : Priority.MID);
        card.setDueDate(dueDate);
        return cardRepository.save(card);
    }

    public Optional<Card> updateCard(Long id, String title, Priority priority, LocalDate dueDate) {
        return cardRepository.findById(id)
                .map(card -> {
                    card.setTitle(title);
                    card.setPriority(priority);
                    card.setDueDate(dueDate);
                    return cardRepository.save(card);
                });
    }
}
