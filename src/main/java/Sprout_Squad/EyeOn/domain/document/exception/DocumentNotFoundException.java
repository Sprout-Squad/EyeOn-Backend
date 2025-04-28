package Sprout_Squad.EyeOn.domain.document.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class DocumentNotFoundException extends BaseException {
    public DocumentNotFoundException() { super(DocumentErrorCode.DOCUMENT_NOT_FOUND_404); }
}
