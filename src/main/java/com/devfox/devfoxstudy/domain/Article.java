package com.devfox.devfoxstudy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields { // indexes : 追加の書き込み作業とストレージ容量を活用してデータベーステーブルの検索速度を向上させるための資料構造

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // MySQLのAutoIncrementはIdentity方式で作られるからIDENTITY設定
    private Long id;

    @Setter
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false)
    private UserAccount userAccount; // User情報 (ID)

    @Setter
    @Column(nullable = false)
    private String title; // 題目
    @Setter
    @Column(nullable = false, length = 10000)
    private String content; // 内容
    @ToString.Exclude
    @JoinTable(
            name = "article_hashtag",
            joinColumns = @JoinColumn(name = "articleId"),
            inverseJoinColumns = @JoinColumn(name = "hashtagId")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Hashtag> hashtags = new LinkedHashSet<>();
    // Setterをfieldにつける理由は使用者が特定のfieldに接近して設定を変わる事を防ぐため(ex : id)

    @ToString.Exclude
    @OrderBy("id")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();
    // 意図 : このarticleに連動されるcommentは重複を許容せず、全部集めてCollectionで見る
    // lazy loaded fieldsがあるため、後でmemory性能が下がる可能性があるだから隠すためのExclude
    // それとcircular referencing issue防止
    // article tableから受けたという明示
    // 運営の立場では掲示文が削除される時commentが削除するのを防止するため、FKを付かないこともある

    protected Article() {

    }
    // 全てのJPA EntityはHibernate具現体を使う時、基本constructorを持つべき
    // codeの外からnew生成を防ぐためのprotected

    private Article(UserAccount userAccount, String title, String content) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }
    // Domainと関連がある情報だけOpenする

    public static Article of(UserAccount userAccount, String title, String content) {
        return new Article(userAccount, title, content);
    }
    // factory method : capsule化して結合を緩める、
    // DomainArticleを生成するよう時はどんな値を必要とするというGuide

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // 受け入れた Object oがArticleなのかをCheck
        Article article = (Article) o;
        // (Type Casting)
        return id != null && id.equals(article.id);
        // 作ったばかりのまだpersistなってないEntityは同一性検査を脱落
        // つまりIDが付与されない、まだPersist化されないと同一性検査自体が無意味
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addHashtag(Hashtag hashtag) {
        this.getHashtags().add(hashtag);
    }

    public void addHashtags(Collection<Hashtag> hashtags) {
        this.getHashtags().addAll(hashtags);
    }

    public void clearHashtags() {
        this.getHashtags().clear();
    }

    // 同一性検査ができるequals and hashcode作成
}
