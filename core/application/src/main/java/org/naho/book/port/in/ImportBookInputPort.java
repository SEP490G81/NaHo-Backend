package org.naho.book.port.in;

import java.io.InputStream;

public interface ImportBookInputPort {
    void importBookDataFromExcel(InputStream inputStream);
}
