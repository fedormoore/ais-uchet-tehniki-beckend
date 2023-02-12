package ru.moore.AISUchetTehniki.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        String errors = "";

        for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
            if (errors.equals("")) {
                errors = constraintViolation.getMessage();
            } else {
                errors = errors + " " + constraintViolation.getMessage();
            }
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, errors);
        final ResponseEntity responseEntity = new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        return responseEntity;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errors = "";

        for (FieldError constraintViolation : ex.getBindingResult().getFieldErrors()) {
            if (errors.equals("")) {
                errors = constraintViolation.getDefaultMessage();
            } else {
                errors = errors + " " + constraintViolation.getDefaultMessage();
            }
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, errors.toString());
        final ResponseEntity responseEntity = new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        return responseEntity;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleMethodJSON(HttpMessageNotReadableException ex) {
        String errors = "";
        if (ex.getMessage().contains("UUID has to be")) {
            errors = "UUID должен быть представлен стандартным 36-символьным представлением.";
        } else if (ex.getMessage().contains("Required request body is missing")) {
            errors = "Отсутствует требуемый текст запроса";
        } else if (ex.getMessage().contains("Cannot deserialize value of type")) {
            errors = "Невозможно десериализовать значение типа";
        }

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, errors.toString());
        final ResponseEntity responseEntity = new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        return responseEntity;
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleMethodJSON(SQLException ex) {
        String errors = ex.getMessage();
        if (ex.getSQLState().equals("23505")) {
            errors = "Запись уже существует.";
        } else if (ex.getMessage().contains("Required request body is missing")) {
            errors = "Отсутствует требуемый текст запроса";
        } else if (ex.getMessage().contains("Cannot deserialize value of type")) {
            errors = "Невозможно десериализовать значение типа";
        }

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST, errors.toString());
        final ResponseEntity responseEntity = new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        return responseEntity;
    }

    @ExceptionHandler
    public final ResponseEntity<?> handleUserNotFoundException(ErrorTemplate ex) {
        ErrorResponse error = new ErrorResponse(ex.getStatus(), ex.getMessage());
        return new ResponseEntity(error, ex.getStatus());
    }
}
