package lab.contract.infrastructure.exception;

import lab.contract.infrastructure.exception.contract.IdNullException;
import lab.contract.infrastructure.exception.user.DoesNotExistUserException;
import lab.contract.infrastructure.exception.user.DuplicatedUserException;
import lab.contract.infrastructure.exception.user.PasswordMismatchException;
import lab.contract.infrastructure.exception.user.SessionNullException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedUserException.class)
    public final ResponseEntity handleDuplicatedUserException(
            DuplicatedUserException e) {
        log.info("이미 존재하는 회원입니다.");
        return new ResponseEntity(DefaultRes.res(StatusCode.DUPLICATED_USER, e.getResponseMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DoesNotExistUserException.class)
    public final ResponseEntity handleDoesNotExistUserException(
            DoesNotExistUserException e) {
        log.info("존재하지 않는 회원입니다.");
        return new ResponseEntity(DefaultRes.res(StatusCode.DOES_NOT_EXIST_USER, e.getResponseMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public final ResponseEntity handlePasswordMismatchException(
            PasswordMismatchException e) {
        log.info("비밀번호가 일치하지 않습니다.");
        return new ResponseEntity(DefaultRes.res(StatusCode.PASSWORD_MISMATCH, e.getResponseMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(SessionNullException.class)
    public final ResponseEntity handleSessionNullException (
            SessionNullException e) {
        log.info("로그인 사용자가 존재하지 않습니다.");
        return new ResponseEntity(DefaultRes.res(StatusCode.SESSION_NULL, e.getResponseMessage()),HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(IdNullException.class)
    public final ResponseEntity handleIdNullException (
            IdNullException e) {
        log.info("아이디 값이 없습니다.");
        return new ResponseEntity(DefaultRes.res(StatusCode.ID_NULL, e.getResponseMessage()),HttpStatus.BAD_REQUEST);
    }
}
