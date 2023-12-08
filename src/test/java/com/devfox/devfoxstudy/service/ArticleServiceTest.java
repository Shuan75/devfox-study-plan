package com.devfox.devfoxstudy.service;

import com.devfox.devfoxstudy.domain.Article;
import com.devfox.devfoxstudy.domain.type.SearchType;
import com.devfox.devfoxstudy.dto.ArticleDto;
import com.devfox.devfoxstudy.dto.ArticleUpdateDto;
import com.devfox.devfoxstudy.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("Business Logic - 掲示文")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest { // 軽く作るためつまりSpringBootApplicationContextLoading時間を減るためmokitoを使う

    @InjectMocks private ArticleService sut; // System Under Test(Test対象) ,　mockを入れる対象をInjectMocksと呼ぶ

    @Mock private ArticleRepository articleRepository; // mokingする時必要


    @DisplayName("掲示文を検索したら掲示文Listを返還")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticleList() throws Exception {
        // given

        // when
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword");
        // 題目、内容、ID、Nickname,Hashtag

        // then
        assertThat(articles).isNotNull();
    }

    @DisplayName("掲示文を照会したら掲示文を返還")
    @Test
    void givenArticleId_whenSearchingArticles_thenReturnsArticle() throws Exception {
        // given

        // when
        ArticleDto articles = sut.searchArticles(1L);
        // then
        assertThat(articles).isNotNull();
    }

    @DisplayName("掲示文の情報入力をしたら掲示文を生成")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() throws Exception {
        // given
        given(articleRepository.save(any(Article.class))).willReturn(null);
        // article.classを渡したらnullを返還

        // when
        sut.saveArticle(ArticleDto.of("title", "内容", "#Java", "Jong", LocalDateTime.now()));
        // then
        then(articleRepository).should().save(any(Article.class));
        // Saveを呼び出したかをCheck
    }

    @DisplayName("掲示文のIDと修正情報を入力したら掲示文を修正")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() throws Exception {
        // given
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // when
        sut.updateArticle(1L, ArticleUpdateDto.of("title", "内容", "#Java"));
        // then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("掲示文のIDを入力したら掲示文を削除")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() throws Exception {
        // given
        willDoNothing().given(articleRepository).delete(any(Article.class));

        // when
        sut.deleteArticle(1L);
        // then
        then(articleRepository).should().delete(any(Article.class));

    }
}