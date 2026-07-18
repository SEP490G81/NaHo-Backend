package org.naho.league.usecase;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.league.mapper.LeagueResultMapper;
import org.naho.league.model.League;
import org.naho.league.port.in.CrudLeagueInputPort;
import org.naho.league.port.out.LeagueRepositoryPort;
import org.naho.league.result.LeagueResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.LeaderboardUserResult;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CrudLeagueUseCase implements CrudLeagueInputPort {

    private final LeagueRepositoryPort leagueRepositoryPort;
    private final LeagueResultMapper leagueResultMapper;
    private final CrudFileInputPort crudFileInputPort;
    private final UserRepositoryPort userRepositoryPort;

    public CrudLeagueUseCase(
            LeagueRepositoryPort leagueRepositoryPort,
            LeagueResultMapper leagueResultMapper,
            CrudFileInputPort crudFileInputPort,
            UserRepositoryPort userRepositoryPort
    ) {
        this.leagueRepositoryPort = leagueRepositoryPort;
        this.leagueResultMapper = leagueResultMapper;
        this.crudFileInputPort = crudFileInputPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public List<LeagueResult> findAll() {
        List<League> leagues = leagueRepositoryPort.findAll();

        List<FileResult> files = crudFileInputPort.findAllByLeagueIds(leagues
                .stream()
                .map(League::getId)
                .toList()
        );

        // create map
        // key: id (file result id) and value: file result
        Map<Long, FileResult> fileResultMap = files.stream()
                .collect(Collectors.toMap(
                        FileResult::id,
                        Function.identity()
                ));

        return leagues.stream()
                .map(league -> {
                    FileResult iconFile = fileResultMap.get(league.getIconFileId());
                    return leagueResultMapper.domainToResult(league, iconFile);
                })
                .toList();
    }

    @Override
    public List<LeaderboardUserResult> findTop10OrderByTotalPointInLeague(Long leagueId) {
        return userRepositoryPort.findTop10OrderByTotalPointInLeague(leagueId);
    }

    @Override
    public LeaderboardUserResult findTopOfUserByUserId(Long userId) {
        return userRepositoryPort.findTopOfUserByUserId(userId)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND
                ));
    }
}
