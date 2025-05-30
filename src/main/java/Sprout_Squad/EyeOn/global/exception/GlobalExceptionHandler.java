package Sprout_Squad.EyeOn.global.exception;


import Sprout_Squad.EyeOn.global.auth.exception.SignupRequiredException;
import Sprout_Squad.EyeOn.global.response.ErrorResponse;
import Sprout_Squad.EyeOn.global.response.code.GlobalErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
    javax.validation.Valid or @Validated 으로 binding error 발생시 발생
    주로 @RequestBody, @RequestPart 어노테이션에서 발생
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException Error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.INVALID_HTTP_MESSAGE_BODY,
                e.getFieldError().getDefaultMessage());
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* binding error 발생시 BindException 발생 */
    @ExceptionHandler(BindException.class)
    private ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.INVALID_HTTP_MESSAGE_BODY);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* enum type 일치하지 않아 binding 못할 경우 발생 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException Error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.INVALID_HTTP_MESSAGE_BODY);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* 지원하지 않은 HTTP method 호출 할 경우 발생 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException Error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.UNSUPPORTED_HTTP_METHOD);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* request 값을 읽을 수 없을 때 발생 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException error", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.BAD_REQUEST_ERROR);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* 비지니스 로직 에러 */
    @ExceptionHandler(BaseException.class)
    private ResponseEntity<ErrorResponse> handleBusinessException(BaseException e) {
        log.error("BusinessError ");
        log.error(e.getErrorCode().getMessage());
        ErrorResponse error = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* 카카오 로그인을 위한 UserNotFoundException 에러 처리 */
    @ExceptionHandler(SignupRequiredException.class)
    private ResponseEntity<ErrorResponse> handleUserSignupRequiredException(SignupRequiredException e) {
        log.error("UserNotFoundException error", e.getErrorCode().getMessage());
        ErrorResponse error = ErrorResponse.of(
                e.getErrorCode(),
                e.getErrorCode().getMessage(),
                e.getExtra()
        );
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

    /* RequestPart 누락 시 에러 처리 */
    @ExceptionHandler(MissingServletRequestPartException.class)
    private ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        log.error("MissingServletRequestPartException Error: {}", e.getRequestPartName());
        ErrorResponse error = ErrorResponse.of(
                GlobalErrorCode.BAD_REQUEST_ERROR,
                e.getRequestPartName() + " 파트가 요청에 없습니다."
        );
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }


    /* 나머지 예외 처리 */
    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception Error ", e);
        ErrorResponse error = ErrorResponse.of(GlobalErrorCode.SERVER_ERROR);
        return ResponseEntity.status(error.getHttpStatus()).body(error);
    }

}