package org.naho.book.port.out;

import java.io.InputStream;

public interface ImportBookPort {
    void importBookDataFromExcel(InputStream inputStream);
}
