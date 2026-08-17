package raisetech.task.management.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.task.management.entity.Card;
import raisetech.task.management.service.CardService;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public List<Card> getAllCards() {
        return cardService.getAllCards();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@PathVariable Long id) {
        return cardService.getCardById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
