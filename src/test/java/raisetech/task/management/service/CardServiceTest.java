package raisetech.task.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.task.management.controller.dto.CardCreateRequest;
import raisetech.task.management.controller.dto.CardUpdateRequest;
import raisetech.task.management.entity.Card;
import raisetech.task.management.entity.Priority;
import raisetech.task.management.entity.Status;
import raisetech.task.management.repository.CardRepository;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @Test
    void カード作成時に優先度未指定ならMIDになる() {
        when(cardRepository.findMaxPosition()).thenReturn(4L);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card created = cardService.createCard(new CardCreateRequest("タイトル", null, null));

        assertThat(created.getPriority()).isEqualTo(Priority.MID);
        assertThat(created.getStatus()).isEqualTo(Status.TODO);
        assertThat(created.getPosition()).isEqualTo(5L);
    }

    @Test
    void カード作成時に優先度を指定すればその値になる() {
        when(cardRepository.findMaxPosition()).thenReturn(-1L);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card created = cardService.createCard(new CardCreateRequest("タイトル", Priority.HIGH, LocalDate.of(2026, 9, 1)));

        assertThat(created.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(created.getDueDate()).isEqualTo(LocalDate.of(2026, 9, 1));
        assertThat(created.getPosition()).isEqualTo(0L);
    }

    @Test
    void カード更新は存在すればタイトル優先度期限を上書きしステータスは変更しない() {
        Card existing = new Card();
        existing.setId(1L);
        existing.setTitle("旧タイトル");
        existing.setStatus(Status.DOING);
        existing.setPriority(Priority.MID);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Card> updated = cardService.updateCard(1L, new CardUpdateRequest("新タイトル", Priority.LOW, null));

        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo("新タイトル");
        assertThat(updated.get().getPriority()).isEqualTo(Priority.LOW);
        assertThat(updated.get().getStatus()).isEqualTo(Status.DOING);
    }

    @Test
    void カード更新は存在しなければ空を返す() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Card> updated = cardService.updateCard(99L, new CardUpdateRequest("タイトル", Priority.MID, null));

        assertThat(updated).isEmpty();
        verify(cardRepository, never()).save(any());
    }

    @Test
    void ステータス変更で列が変わる場合は移動先の末尾にpositionを採番し直す() {
        Card existing = new Card();
        existing.setId(1L);
        existing.setStatus(Status.TODO);
        existing.setPosition(3L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cardRepository.findMaxPositionByStatus(Status.DOING)).thenReturn(1L);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Card> updated = cardService.updateStatus(1L, Status.DOING);

        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(Status.DOING);
        assertThat(updated.get().getPosition()).isEqualTo(2L);
    }

    @Test
    void ステータス変更で列が変わらない場合はpositionを変更しない() {
        Card existing = new Card();
        existing.setId(1L);
        existing.setStatus(Status.TODO);
        existing.setPosition(3L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Card> updated = cardService.updateStatus(1L, Status.TODO);

        assertThat(updated).isPresent();
        assertThat(updated.get().getPosition()).isEqualTo(3L);
        verify(cardRepository, never()).findMaxPositionByStatus(any());
    }

    @Test
    void 並び替えは指定順に0始まりのpositionを振り直す() {
        Card first = new Card();
        first.setId(2L);
        first.setStatus(Status.TODO);
        Card second = new Card();
        second.setId(1L);
        second.setStatus(Status.TODO);

        when(cardRepository.findAllById(List.of(2L, 1L))).thenReturn(List.of(first, second));
        when(cardRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<List<Card>> result = cardService.reorderCards(Status.TODO, List.of(2L, 1L));

        assertThat(result).isPresent();
        assertThat(first.getPosition()).isEqualTo(0L);
        assertThat(second.getPosition()).isEqualTo(1L);
    }

    @Test
    void 並び替えで存在しないIDが含まれる場合は空を返す() {
        Card first = new Card();
        first.setId(2L);
        first.setStatus(Status.TODO);

        when(cardRepository.findAllById(List.of(2L, 3L))).thenReturn(List.of(first));

        Optional<List<Card>> result = cardService.reorderCards(Status.TODO, List.of(2L, 3L));

        assertThat(result).isEmpty();
        verify(cardRepository, never()).saveAll(any());
    }

    @Test
    void 並び替えでステータスが一致しないカードが含まれる場合は空を返す() {
        Card wrongStatus = new Card();
        wrongStatus.setId(2L);
        wrongStatus.setStatus(Status.DOING);

        when(cardRepository.findAllById(eq(List.of(2L)))).thenReturn(List.of(wrongStatus));

        Optional<List<Card>> result = cardService.reorderCards(Status.TODO, List.of(2L));

        assertThat(result).isEmpty();
        verify(cardRepository, never()).saveAll(any());
    }

    @Test
    void カード削除は存在すれば削除してtrueを返す() {
        when(cardRepository.existsById(1L)).thenReturn(true);

        boolean deleted = cardService.deleteCard(1L);

        assertThat(deleted).isTrue();
        verify(cardRepository).deleteById(1L);
    }

    @Test
    void カード削除は存在しなければfalseを返し削除しない() {
        when(cardRepository.existsById(99L)).thenReturn(false);

        boolean deleted = cardService.deleteCard(99L);

        assertThat(deleted).isFalse();
        verify(cardRepository, never()).deleteById(any());
    }
}
