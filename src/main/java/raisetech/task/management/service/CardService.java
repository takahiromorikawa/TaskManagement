package raisetech.task.management.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import raisetech.task.management.entity.Card;
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
}
