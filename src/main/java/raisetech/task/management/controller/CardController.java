package raisetech.task.management.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.task.management.controller.dto.CardCreateRequest;
import raisetech.task.management.controller.dto.CardReorderRequest;
import raisetech.task.management.controller.dto.CardResponse;
import raisetech.task.management.controller.dto.CardStatusUpdateRequest;
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
        Card card = cardService.createCard(request);
        CardResponse response = CardResponse.from(card);
        return ResponseEntity.created(URI.create("/cards/" + card.getId())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable Long id, @Valid @RequestBody CardUpdateRequest request) {
        return cardService.updateCard(id, request)
                .map(CardResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CardResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody CardStatusUpdateRequest request) {
        return cardService.updateStatus(id, request.status())
                .map(CardResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/reorder")
    public ResponseEntity<List<CardResponse>> reorderCards(@Valid @RequestBody CardReorderRequest request) {
        return cardService.reorderCards(request.status(), request.cardIds())
                .map(cards -> cards.stream().map(CardResponse::from).toList())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        boolean deleted = cardService.deleteCard(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
