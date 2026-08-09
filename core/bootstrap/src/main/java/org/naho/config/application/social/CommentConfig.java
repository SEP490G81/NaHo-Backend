package org.naho.config.application.social;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.social.comment.dto.mapper.CommentCommandMapper;
import org.naho.social.comment.dto.mapper.CommentResponseMapper;
import org.naho.social.comment.mapper.CommentDomainMapper;
import org.naho.social.comment.mapper.CommentListResultMapper;
import org.naho.social.comment.mapper.CommentResultMapper;
import org.naho.social.comment.port.in.CommentCrudInputPort;
import org.naho.social.comment.port.out.CommentRepositoryPort;
import org.naho.social.comment.usecase.CommentCrudUsecase;
import org.naho.social.reaction.port.out.ReactionRepositoryPort;
import org.naho.user.port.in.CrudAuthProviderInputPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommentConfig {

    @Bean
    public CommentDomainMapper commentDomainMapper() {
        return new CommentDomainMapper();
    }

    @Bean
    public CommentResultMapper commentResultMapper(
            UserRepositoryPort userRepositoryPort,
            CrudFileInputPort crudFileInputPort,
            CrudAuthProviderInputPort crudAuthProviderInputPort
    ) {
        return new CommentResultMapper(userRepositoryPort, crudFileInputPort, crudAuthProviderInputPort);
    }

    @Bean
    public CommentListResultMapper commentListResultMapper(
            CommentResultMapper commentResultMapper,
            ReactionRepositoryPort reactionRepositoryPort
    ) {
        return new CommentListResultMapper(commentResultMapper, reactionRepositoryPort);
    }

    @Bean
    public CommentCommandMapper commentCommandMapper() {
        return new CommentCommandMapper();
    }

    @Bean
    public CommentResponseMapper commentResponseMapper() {
        return new CommentResponseMapper();
    }

    @Bean
    public CommentCrudInputPort commentCrudInputPort(
            CommentRepositoryPort commentRepositoryPort,
            CommentListResultMapper commentListResultMapper,
            CommentDomainMapper commentDomainMapper,
            CommentResultMapper commentResultMapper
    ) {
        return new CommentCrudUsecase(
                commentRepositoryPort,
                commentListResultMapper,
                commentDomainMapper,
                commentResultMapper
        );
    }
}
