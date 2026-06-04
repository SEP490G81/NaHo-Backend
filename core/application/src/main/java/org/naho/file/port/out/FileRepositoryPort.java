package org.naho.file.port.out;

import org.naho.file.model.File;

public interface FileRepositoryPort {
    String findObjectKeyById(Long id);

    File save(File file);

    void deleteById(Long id);
}
