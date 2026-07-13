package org.naho.point.dto.response;

import org.naho.file.dto.response.FileResponse;

public record LeagueResponse(
        Long id,
        FileResponse iconFile,
        String name,
        String description,
        Double minPoint,
        Double maxPoint
) {
}
