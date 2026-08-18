package raisetech.task.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import raisetech.task.management.entity.Card;
import raisetech.task.management.entity.Priority;
import raisetech.task.management.entity.Status;
import raisetech.task.management.service.CardService;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @MockitoBean
    private CardService cardService;

    @Test
    void カード作成に成功すると201とカード情報を返す() {
        Card card = new Card();
        card.setId(1L);
        card.setTitle("設計書を書く");
        card.setStatus(Status.TODO);
        card.setPriority(Priority.HIGH);
        card.setDueDate(LocalDate.of(2026, 8, 20));

        when(cardService.createCard(eq("設計書を書く"), eq(Priority.HIGH), eq(LocalDate.of(2026, 8, 20))))
                .thenReturn(card);

        mockMvcTester.post().uri("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"設計書を書く","priority":"HIGH","dueDate":"2026-08-20"}
                        """)
                .assertThat()
                .hasStatus(201)
                .hasHeader("Location", "/cards/1")
                .bodyJson()
                .extractingPath("$.title").isEqualTo("設計書を書く");
    }

    @Test
    void 優先度と期限を省略した場合はnullでサービスに渡す() {
        Card card = new Card();
        card.setId(2L);
        card.setTitle("最小構成のカード");
        card.setStatus(Status.TODO);
        card.setPriority(Priority.MID);

        when(cardService.createCard(eq("最小構成のカード"), isNull(), isNull()))
                .thenReturn(card);

        mockMvcTester.post().uri("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"最小構成のカード"}
                        """)
                .assertThat()
                .hasStatus(201);
    }

    @Test
    void タイトル未入力の場合は400を返しサービスを呼び出さない() {
        mockMvcTester.post().uri("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"","priority":"HIGH"}
                        """)
                .assertThat()
                .hasStatus(400);

        verify(cardService, org.mockito.Mockito.never()).createCard(any(), any(), any());
    }

    @Test
    void カード更新に成功すると200と更新後のカード情報を返す() {
        Card card = new Card();
        card.setId(1L);
        card.setTitle("設計書を書き直す");
        card.setStatus(Status.DOING);
        card.setPriority(Priority.LOW);
        card.setDueDate(LocalDate.of(2026, 9, 1));

        when(cardService.updateCard(eq(1L), eq("設計書を書き直す"), eq(Priority.LOW), eq(LocalDate.of(2026, 9, 1))))
                .thenReturn(Optional.of(card));

        mockMvcTester.put().uri("/cards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"設計書を書き直す","priority":"LOW","dueDate":"2026-09-01"}
                        """)
                .assertThat()
                .hasStatus(200)
                .bodyJson()
                .extractingPath("$.title").isEqualTo("設計書を書き直す");
    }

    @Test
    void 存在しないIDを更新しようとすると404を返す() {
        when(cardService.updateCard(eq(99L), any(), any(), any())).thenReturn(Optional.empty());

        mockMvcTester.put().uri("/cards/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"存在しないカード","priority":"MID"}
                        """)
                .assertThat()
                .hasStatus(404);
    }

    @Test
    void 更新時にタイトル未入力の場合は400を返しサービスを呼び出さない() {
        mockMvcTester.put().uri("/cards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"","priority":"MID"}
                        """)
                .assertThat()
                .hasStatus(400);

        verify(cardService, org.mockito.Mockito.never()).updateCard(any(), any(), any(), any());
    }

    @Test
    void 更新時に優先度未指定の場合は400を返す() {
        mockMvcTester.put().uri("/cards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title":"タイトルのみ"}
                        """)
                .assertThat()
                .hasStatus(400);

        verify(cardService, org.mockito.Mockito.never()).updateCard(any(), any(), any(), any());
    }

    @Test
    void ステータス変更に成功すると200と更新後のカード情報を返す() {
        Card card = new Card();
        card.setId(1L);
        card.setTitle("設計書を書く");
        card.setStatus(Status.DOING);
        card.setPriority(Priority.HIGH);
        card.setDueDate(LocalDate.of(2026, 8, 20));

        when(cardService.updateStatus(eq(1L), eq(Status.DOING))).thenReturn(Optional.of(card));

        mockMvcTester.put().uri("/cards/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status":"DOING"}
                        """)
                .assertThat()
                .hasStatus(200)
                .bodyJson()
                .extractingPath("$.status").isEqualTo("DOING");
    }

    @Test
    void 存在しないIDのステータスを変更しようとすると404を返す() {
        when(cardService.updateStatus(eq(99L), any())).thenReturn(Optional.empty());

        mockMvcTester.put().uri("/cards/99/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status":"DONE"}
                        """)
                .assertThat()
                .hasStatus(404);
    }

    @Test
    void ステータス未指定の場合は400を返しサービスを呼び出さない() {
        mockMvcTester.put().uri("/cards/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .assertThat()
                .hasStatus(400);

        verify(cardService, org.mockito.Mockito.never()).updateStatus(any(), any());
    }

    @Test
    void 並び替えに成功すると200と並び替え後のカード一覧を返す() {
        Card first = new Card();
        first.setId(2L);
        first.setTitle("後のカード");
        first.setStatus(Status.TODO);
        first.setPriority(Priority.MID);

        Card second = new Card();
        second.setId(1L);
        second.setTitle("先のカード");
        second.setStatus(Status.TODO);
        second.setPriority(Priority.MID);

        when(cardService.reorderCards(eq(Status.TODO), eq(List.of(2L, 1L))))
                .thenReturn(Optional.of(List.of(first, second)));

        mockMvcTester.put().uri("/cards/reorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status":"TODO","cardIds":[2,1]}
                        """)
                .assertThat()
                .hasStatus(200)
                .bodyJson()
                .extractingPath("$[0].id").isEqualTo(2);
    }

    @Test
    void 並び替えでステータスが一致しないカードが含まれる場合は400を返す() {
        when(cardService.reorderCards(eq(Status.TODO), eq(List.of(1L, 3L)))).thenReturn(Optional.empty());

        mockMvcTester.put().uri("/cards/reorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status":"TODO","cardIds":[1,3]}
                        """)
                .assertThat()
                .hasStatus(400);
    }

    @Test
    void 並び替えでcardIdsが空の場合は400を返しサービスを呼び出さない() {
        mockMvcTester.put().uri("/cards/reorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"status":"TODO","cardIds":[]}
                        """)
                .assertThat()
                .hasStatus(400);

        verify(cardService, org.mockito.Mockito.never()).reorderCards(any(), any());
    }

    @Test
    void カード削除に成功すると204を返す() {
        when(cardService.deleteCard(1L)).thenReturn(true);

        mockMvcTester.delete().uri("/cards/1")
                .assertThat()
                .hasStatus(204);
    }

    @Test
    void 存在しないIDを削除しようとすると404を返す() {
        when(cardService.deleteCard(99L)).thenReturn(false);

        mockMvcTester.delete().uri("/cards/99")
                .assertThat()
                .hasStatus(404);
    }
}
