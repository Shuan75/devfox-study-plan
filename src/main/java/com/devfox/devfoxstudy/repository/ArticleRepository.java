package com.devfox.devfoxstudy.repository;

import com.devfox.devfoxstudy.domain.Article;
import com.devfox.devfoxstudy.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource// detection-strategy戦略
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>, // entityの中全てのfieldに対する基本検索機能追加
        QuerydslBinderCustomizer<QArticle> { // Customizer

    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true); // listingしてないpropertyは検索から除外
        bindings.including(root.title, root.hashtag, root.createdAt, root.createdBy); // 検索Filterに追加
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // (path, value) -> path.eq(value)同じ
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);

        // exact match role変更 , argumentを一つもらってpath=value check

        // redicateExecutorによってarticleの全てのFieldに対する検索ができる
        // だが選択したfieldだけ検索しよう時使う(ex : modifiedAtは検索対象から除外)
    }


}
