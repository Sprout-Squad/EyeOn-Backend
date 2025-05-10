package Sprout_Squad.EyeOn.global.external.exception;

import Sprout_Squad.EyeOn.global.exception.BaseException;

public class FileNotCreatedException extends BaseException {
    public FileNotCreatedException() { super(ExternalErrorCode.FILE_CREATE_FAILED_500); }
}
