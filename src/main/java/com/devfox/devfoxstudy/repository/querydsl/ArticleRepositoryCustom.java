package com.devfox.devfoxstudy.repository.querydsl;

import com.devfox.devfoxstudy.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface ArticleRepositoryCustom {
    /**
     * @deprecated HashtagDomainを新しく作ったからこのコードはもういらない
     * @see HashtagRepositoryCustom#findAllHashtagNames()
     */
    @Deprecated
    List<String> findAllDistinctHashtags();
    Page<Article> findByHashtagNames(Collection<String> hashtagNames, Pageable pageable);
}