package com.devfox.devfoxstudy.controller;

import com.devfox.devfoxstudy.config.SecurityConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View Controller - 掲示文")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class) // test対象になるcontrollerだけbeanで読む
class ArticleControllerTest {

    private final MockMvc mvc;

    public ArticleControllerTest(@Autowired MockMvc mvc) { // Testでは@Autowired省略不可能
        this.mvc = mvc;
    }
    // @Disabled理由　: (Testを失敗したままuploadするとgradle buildが失敗して配布自動化に悪い影響があるから
    // 失敗したtestを除外処理方式で政策を組む

//    @Disabled("具現中")
    @DisplayName("[view][GET] 掲示文 list (掲示板)page - 正常呼び出し")
    @Test
    public void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        // given

        // when
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // type check
                .andExpect(view().name("articles/index")) // mappingされるhtmlFileViewのtest条件
                .andExpect(model().attributeExists("articles"));
                // modelAttributeというmapにkeyがあるかdataがあるかを検査
        // then
    }

    @DisplayName("[view][GET] 掲示文詳細page - 正常呼び出し")
    @Test
    public void givenNothing_whenRequestingArticleView_thenReturnsArticlesView() throws Exception {
        // given

        // when
        mvc.perform(get("/articles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"));
        // then
    }

    @Disabled("具現中")
    @DisplayName("[view][GET] 掲示文検索専用 - 正常呼び出し")
    @Test
    public void givenNothing_whenRequestingArticleSearchView_thenReturnsArticlesView() throws Exception {
        // given

        // when
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("articles/search"));;
        // then
    }

    @Disabled("具現中")
    @DisplayName("[view][GET] 掲示文hashtag検索page - 正常呼び出し")
    @Test
    public void givenNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticlesView() throws Exception {
        // given

        // when
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("article/search-hashtag"));;
        // then
    }
}