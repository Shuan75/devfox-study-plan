package com.devfox.devfoxstudy.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;

@Configuration
public class ThymeleafConfig {
    // decoupled teamplate logic
    // thymeleaf構文を別々に分離して作成し、indexを純粋なmark up状態に保つ方法

    @Bean
    public SpringResourceTemplateResolver thymeleafTemplateResolver(
            SpringResourceTemplateResolver defaultTemplateResolver,
            Thymeleaf3Properties thymeleaf3Properties
    ) {
        defaultTemplateResolver.setUseDecoupledLogic(thymeleaf3Properties.isDecoupledLogic());

        return defaultTemplateResolver;
    }
    //　ThymeleafをSpringBootProjectに入れる時Auto-configが自動で作動その時、入れるのが基本thymeleafTemplateResolver
    // Thymeleaf-Templateをどうやってresolvingするかに対する具現部分

    @RequiredArgsConstructor
    @Getter
//    @ConstructorBinding
    @ConfigurationProperties("spring.thymeleaf3")
    public static class Thymeleaf3Properties {
        /**
         * Use Thymeleaf 3 Decoupled Logic
         */
        // つまりproperty fieldを作成して、それを通じて設定を受けてsettingする意味
        private final boolean decoupledLogic;

    }

}
