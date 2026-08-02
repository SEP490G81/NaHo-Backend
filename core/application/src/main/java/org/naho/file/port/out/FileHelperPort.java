package org.naho.file.port.out;

import java.nio.file.Path;

public interface FileHelperPort {
    String getExtension(String originalFileName);

    String calculateChecksum(Path file);
}
