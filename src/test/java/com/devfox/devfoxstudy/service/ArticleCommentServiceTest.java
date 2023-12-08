package com.devfox.devfoxstudy.service;

import com.devfox.devfoxstudy.domain.Article;
import com.devfox.devfoxstudy.dto.ArticleCommentDto;
import com.devfox.devfoxstudy.repository.ArticleCommentRepository;
import com.devfox.devfoxstudy.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("Business Logic - Comment")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks
    private ArticleCommentService sut;

    @Mock
    private ArticleCommentRepository articleCommentRepository;
    @Mock
    private ArticleRepository articleRepository;

    @DisplayName("掲示文IDで照会したらCommentListを返還")
    @Test
    void givenArticleId_whenSearchingArticleComments_thenReturnsArticleComments() throws Exception {
        // given
        Long articleId = 1L;

//        given(articleRepository.findById(articleId))
//                    .willReturn(Optional.of(Article.of("title", "content", "#Java")));
        // findByIdはOptionalが必要

        // when
        List<ArticleCommentDto> articleComments = sut.searchArticleComment(articleId);
        // idで検索しながらid番号がわかるからid設定

        // then
        assertThat(articleComments).isNotNull();
        then(articleRepository).should().findById(articleId);
    }

}