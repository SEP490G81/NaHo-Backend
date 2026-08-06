package org.naho.file.port.out;

import org.naho.file.model.File;
import org.naho.file.result.FileResult;

public interface FileResultMapperPort {
    FileResult domainToResult(File domain);
}
