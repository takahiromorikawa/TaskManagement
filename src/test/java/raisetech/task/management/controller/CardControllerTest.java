package raisetech.task.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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
}
