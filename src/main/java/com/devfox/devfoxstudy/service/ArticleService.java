package com.devfox.devfoxstudy.service;

import com.devfox.devfoxstudy.domain.Article;
import com.devfox.devfoxstudy.domain.Hashtag;
import com.devfox.devfoxstudy.domain.UserAccount;
import com.devfox.devfoxstudy.domain.constant.SearchType;
import com.devfox.devfoxstudy.dto.ArticleDto;
import com.devfox.devfoxstudy.dto.ArticleUpdateDto;
import com.devfox.devfoxstudy.dto.ArticleWithCommentsDto;
import com.devfox.devfoxstudy.repository.ArticleRepository;
import com.devfox.devfoxstudy.repository.HashtagRepository;
import com.devfox.devfoxstudy.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagService hashtagService;
    private final HashtagRepository hashtagRepository;

    // commentは掲示文で扱うからDependencyを入れない

    @Transactional(readOnly = true) // 照会で変更がないから readOnly
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {

       if (searchKeyword == null || searchKeyword.isBlank()) {
           return articleRepository.findAll(pageable).map(ArticleDto::from);
           // pageの中の内容たちをもう一度形変換
       }
       // 検索語がない時

        return switch (searchType) {
           // IntelliJ13からswitchがreturnが可能
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtagNames(
                            Arrays.stream(searchKeyword.split(" ")).toList(),
                            pageable
                    )
                    .map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from) // 作っておいたものを利用して置き換える
                .orElseThrow(() -> new EntityNotFoundException("掲示文がありません - articleId: " + articleId));
    }


    @Transactional(readOnly = true)
    public ArticleDto searchArticles(long l) {
        return null;
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("掲示文がありません - articleId: " + articleId));
    }

    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());

        Article article = dto.toEntity(userAccount);
        article.addHashtags(hashtags);
        articleRepository.save(article);
    }

    public void updateArticle(long articleId, ArticleDto dto) {
        //　getReferenceByIdの中EntityNotFoundExceptionを使う
        // こういう点を利用してtry-catch文を作る
        try {
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

            if (article.getUserAccount().equals(userAccount)) {
                if (dto.title() != null) {article.setTitle(dto.title());}
                if (dto.content() != null) {article.setContent(dto.content());}

                Set<Long> hashtagIds = article.getHashtags().stream()
                        .map(Hashtag::getId)
                        .collect(Collectors.toUnmodifiableSet());
                article.clearHashtags();
                articleRepository.flush();

                hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);

                Set<Hashtag> hashtags = renewHashtagsFromContent(dto.content());
                article.addHashtags(hashtags);
                // SelectQueryを発生せず、referenceだけを持ってくる GetOnと同じ
                // ClassLevelTransactionalによってMethod単位でTransactionalされてるから
                // articleが変えたことを感知、その後感知した部分をQuery実行
                // だからsaveを明示する必要がない
            }

        } catch (EntityNotFoundException e) {
            log.warn("掲示文Update失敗、掲示文を探せません -dto: {}", dto);
            // string interpolation
            // WarningLogあえて使わない時DtoLogicを実行したりMemoryの負担を減る
        }

    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }

    public long getArticleCount() {
        return articleRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtagName, Pageable pageable) {
        if (hashtagName == null || hashtagName.isBlank()) {
            return Page.empty(pageable);
        }

        return articleRepository.findByHashtagNames(List.of(hashtagName), pageable)
                .map(ArticleDto::from);
    }

    public List<String> getHashtags() {
        return hashtagRepository.findAllHashtagNames(); // TODO: HashtagServiceで移動を考慮
    }
    private Set<Hashtag> renewHashtagsFromContent(String content) {
        Set<String> hashtagNamesInContent = hashtagService.parseHashtagNames(content);
        Set<Hashtag> hashtags = hashtagService.findHashtagsByNames(hashtagNamesInContent);
        Set<String> existingHashtagNames = hashtags.stream()
                .map(Hashtag::getHashtagName)
                .collect(Collectors.toUnmodifiableSet());

        hashtagNamesInContent.forEach(newHashtagName -> {
            if (!existingHashtagNames.contains(newHashtagName)) {
                hashtags.add(Hashtag.of(newHashtagName));
            }
        });

        return hashtags;
    }
}
