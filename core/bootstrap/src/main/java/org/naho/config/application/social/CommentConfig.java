package org.naho.config.application.social;

import org.naho.league.port.in.CrudLeagueInputPort;
import org.naho.learning.port.out.LearningPathNodeRepositoryPort;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.social.comment.dto.mapper.CommentCommandMapper;
import org.naho.social.comment.mapper.CommentDomainMapper;
import org.naho.social.comment.mapper.CommentListResultMapper;
import org.naho.social.comment.mapper.CommentResultMapper;
import org.naho.social.comment.port.in.CommentCrudInputPort;
import org.naho.social.comment.port.out.CommentRepositoryPort;
import org.naho.social.comment.usecase.CommentCrudUseCase;
import org.naho.social.reaction.port.out.ReactionRepositoryPort;
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
            CrudLeagueInputPort crudLeagueInputPort
    ) {
        return new CommentResultMapper(crudLeagueInputPort);
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
    public CommentCrudInputPort commentCrudInputPort(
            CommentRepositoryPort commentRepositoryPort,
            CommentListResultMapper commentListResultMapper,
            CommentDomainMapper commentDomainMapper,
            CommentResultMapper commentResultMapper,
            EventPublisherPort eventPublisherPort,
            UserRepositoryPort userRepositoryPort,
            LearningPathNodeRepositoryPort learningPathNodeRepositoryPort
    ) {
        return new CommentCrudUseCase(
                commentRepositoryPort,
                commentListResultMapper,
                commentDomainMapper,
                commentResultMapper,
                eventPublisherPort,
                userRepositoryPort,
                learningPathNodeRepositoryPort
        );
    }
}
