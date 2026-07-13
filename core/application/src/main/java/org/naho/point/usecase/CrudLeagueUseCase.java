package org.naho.point.usecase;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.point.mapper.LeagueResultMapper;
import org.naho.point.model.League;
import org.naho.point.port.in.CrudLeagueInputPort;
import org.naho.point.port.out.LeagueRepositoryPort;
import org.naho.point.result.LeagueResult;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CrudLeagueUseCase implements CrudLeagueInputPort {

    private final LeagueRepositoryPort leagueRepositoryPort;
    private final LeagueResultMapper leagueResultMapper;
    private final CrudFileInputPort crudFileInputPort;

    public CrudLeagueUseCase(
            LeagueRepositoryPort leagueRepositoryPort,
            LeagueResultMapper leagueResultMapper,
            CrudFileInputPort crudFileInputPort
    ) {
        this.leagueRepositoryPort = leagueRepositoryPort;
        this.leagueResultMapper = leagueResultMapper;
        this.crudFileInputPort = crudFileInputPort;
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
}
