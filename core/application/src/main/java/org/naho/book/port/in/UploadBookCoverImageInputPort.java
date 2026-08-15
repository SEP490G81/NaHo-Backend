package org.naho.book.port.in;

import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;

public interface UploadBookCoverImageInputPort {
    FileResult uploadCoverImage(StoredFile storedFile);
}
