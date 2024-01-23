package com.devfox.devfoxstudy.repository.querydsl;

import com.devfox.devfoxstudy.domain.Article;
import com.devfox.devfoxstudy.domain.QArticle;
import com.devfox.devfoxstudy.domain.QHashtag;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.Collection;
import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {
    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    // select distinct(hashtag) from article where hashtag is not null;
    @Override
    public List<String> findAllDistinctHashtags() { // ArticleTableから重複なしHashtagFieldをすべて持ってくるQueryを実行
        QArticle article = QArticle.article;

        return from(article)// (article)はQArticleのInstance
                .distinct() // 重複した値を除外
                .select(article.hashtags.any().hashtagName)  // ArticleFieldのHashtagFieldだけを選択
                .fetch();

    }

    @Override
    public Page<Article> findByHashtagNames(Collection<String> hashtagNames, Pageable pageable) {
        QHashtag hashtag = QHashtag.hashtag;
        QArticle article = QArticle.article;

        JPQLQuery<Article> query = from(article)
                .innerJoin(article.hashtags, hashtag)
                .where(hashtag.hashtagName.in(hashtagNames));
        List<Article> articles = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(articles, pageable, query.fetchCount());
    }

}