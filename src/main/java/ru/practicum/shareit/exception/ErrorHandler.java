package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<String> exc(ConstraintViolationException ex) {
        log.info("ConstraintViolationException. Произошла ошибка {}, статус ошибки {}", ex.getMessage(),
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleValidationException(final ValidationException e) {
        log.info("ValidationException. Произошла ошибка {}, статус ошибки {}", e.getMessage(),
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleEntityNotFoundException(final EntityNotFoundException e) {
        log.info("EntityNotFoundException. Произошла ошибка {}, статус ошибки {}", e.getMessage(),
                HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    protected ResponseEntity<String> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        log.info("MethodArgumentNotValidException. Произошла ошибка {}, статус ошибки {}", e.getMessage(),
                HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(final Throwable e) {
        log.info("Throwable. Произошла ошибка {}, статус ошибки {}", e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>("Произошла непредвиденная ошибка.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingStateException(final BookingStateException e) {
        log.info("BookingStateException. Произошла ошибка {}, статус ошибки {}", e.getMessage(),
                HttpStatus.BAD_REQUEST);
        return new ErrorResponse(e.getMessage());
    }
}