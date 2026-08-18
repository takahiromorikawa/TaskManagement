package raisetech.task.management.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.task.management.controller.dto.CardCreateRequest;
import raisetech.task.management.controller.dto.CardResponse;
import raisetech.task.management.controller.dto.CardUpdateRequest;
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
    public List<CardResponse> getAllCards() {
        return cardService.getAllCards().stream()
                .map(CardResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable Long id) {
        return cardService.getCardById(id)
                .map(CardResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest request) {
        Card card = cardService.createCard(request.title(), request.priority(), request.dueDate());
        CardResponse response = CardResponse.from(card);
        return ResponseEntity.created(URI.create("/cards/" + card.getId())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable Long id, @Valid @RequestBody CardUpdateRequest request) {
        return cardService.updateCard(id, request.title(), request.priority(), request.dueDate())
                .map(CardResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
