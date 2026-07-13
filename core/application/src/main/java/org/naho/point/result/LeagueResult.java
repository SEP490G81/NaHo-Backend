package org.naho.point.result;

import org.naho.file.result.FileResult;

public record LeagueResult(
        Long id,
        FileResult iconFile,
        String name,
        String description,
        Double minPoint,
        Double maxPoint
) {
}
