package raisetech.task.management.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.task.management.controller.dto.CardCreateRequest;
import raisetech.task.management.controller.dto.CardUpdateRequest;
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
        return cardRepository.findAllByOrderByPositionAsc();
    }

    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }

    public Card createCard(CardCreateRequest request) {
        Card card = new Card();
        card.setTitle(request.title());
        card.setStatus(Status.TODO);
        card.setPriority(request.priority() != null ? request.priority() : Priority.MID);
        card.setDueDate(request.dueDate());
        card.setPosition(cardRepository.findMaxPosition() + 1);
        return cardRepository.save(card);
    }

    public Optional<Card> updateCard(Long id, CardUpdateRequest request) {
        return cardRepository.findById(id)
                .map(card -> {
                    card.setTitle(request.title());
                    card.setPriority(request.priority());
                    card.setDueDate(request.dueDate());
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public Optional<Card> updateStatus(Long id, Status status) {
        return cardRepository.findById(id)
                .map(card -> {
                    if (card.getStatus() != status) {
                        card.setPosition(cardRepository.findMaxPositionByStatus(status) + 1);
                    }
                    card.setStatus(status);
                    return cardRepository.save(card);
                });
    }

    @Transactional
    public Optional<List<Card>> reorderCards(Status status, List<Long> cardIds) {
        List<Card> cards = cardRepository.findAllById(cardIds);
        boolean invalid = cards.size() != cardIds.size()
                || cards.stream().anyMatch(card -> card.getStatus() != status);
        if (invalid) {
            return Optional.empty();
        }

        Map<Long, Card> cardsById = cards.stream().collect(Collectors.toMap(Card::getId, card -> card));
        List<Card> reordered = new ArrayList<>();
        long position = 0;
        for (Long id : cardIds) {
            Card card = cardsById.get(id);
            card.setPosition(position++);
            reordered.add(card);
        }
        return Optional.of(cardRepository.saveAll(reordered));
    }

    public boolean deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            return false;
        }
        cardRepository.deleteById(id);
        return true;
    }
}
