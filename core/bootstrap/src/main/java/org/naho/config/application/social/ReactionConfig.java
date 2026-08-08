package org.naho.config.application.social;


import org.naho.shared.port.out.EventPublisherPort;
import org.naho.social.comment.port.out.CommentRepositoryPort;
import org.naho.social.reaction.dto.mapper.ReactionRequestMapper;
import org.naho.social.reaction.mapper.ReactionActionCommandMapper;
import org.naho.social.reaction.mapper.ReactionResultResponseMapper;
import org.naho.social.reaction.port.in.CrudReactionTypeInputPort;
import org.naho.social.reaction.port.out.ReactionRepositoryPort;
import org.naho.social.reaction.usecase.CrudReactionUsecase;
import org.naho.user.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactionConfig {
    @Bean
    public CrudReactionTypeInputPort crudReactionTypeInputPort(
            ReactionRepositoryPort reactionRepositoryPort,
            ReactionActionCommandMapper reactionActionCommandMapper,
            ReactionResultResponseMapper reactionResultResponseMapper,
            UserRepositoryPort userRepositoryPort,
            CommentRepositoryPort commentRepositoryPort,
            EventPublisherPort eventPublisherPort
    ) {
        return new CrudReactionUsecase(reactionRepositoryPort, reactionActionCommandMapper, reactionResultResponseMapper, userRepositoryPort, commentRepositoryPort, eventPublisherPort);
    }

    @Bean
    public ReactionActionCommandMapper reactionActionCommandMapper() {
        return new ReactionActionCommandMapper();
    }

    @Bean
    public ReactionResultResponseMapper reactionResultResponseMapper() {
        return new ReactionResultResponseMapper();
    }

    @Bean
    public ReactionRequestMapper reactionRequestMapper() {
        return new ReactionRequestMapper();
    }
}
