package com.devfox.devfoxstudy.service;


import com.devfox.devfoxstudy.dto.ArticleCommentDto;
import com.devfox.devfoxstudy.repository.ArticleCommentRepository;
import com.devfox.devfoxstudy.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ArticleCommentService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComment(long articleId) {
        return List.of();
    }
}
