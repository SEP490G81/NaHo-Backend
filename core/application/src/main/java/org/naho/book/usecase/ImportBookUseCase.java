package org.naho.book.usecase;

import org.naho.book.port.in.ImportBookInputPort;
import org.naho.book.port.out.ImportBookPort;

import java.io.InputStream;

public class ImportBookUseCase implements ImportBookInputPort {
    private final ImportBookPort importBookPort;

    public ImportBookUseCase(ImportBookPort importBookPort) {
        this.importBookPort = importBookPort;
    }

    @Override
    public void importBookDataFromExcel(InputStream inputStream) {
        importBookPort.importBookDataFromExcel(inputStream);
    }
}
