package com.devfox.devfoxstudy.service;

import com.devfox.devfoxstudy.domain.type.SearchType;
import com.devfox.devfoxstudy.dto.ArticleDto;
import com.devfox.devfoxstudy.dto.ArticleUpdateDto;
import com.devfox.devfoxstudy.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    // commentは掲示文で扱うからDependencyを入れない

    @Transactional(readOnly = true) // 照会で変更がないから readOnly
    public Page<ArticleDto> searchArticles(SearchType title, String search_keyword) {
        return Page.empty();
    }


    @Transactional(readOnly = true)
    public ArticleDto searchArticles(long l) {
        return null;
    }

    public void saveArticle(ArticleDto dto) {

    }

    public void updateArticle(long articleId, ArticleUpdateDto dto) {
    }

    public void deleteArticle(long articleId) {
    }
}
