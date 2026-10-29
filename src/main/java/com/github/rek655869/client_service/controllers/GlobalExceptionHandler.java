package com.github.rek655869.client_service.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler
        extends ResponseEntityExceptionHandler {

    // Ошибки валидации DTO: @Valid @RequestBody
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, List<String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        error -> error.getField(),
                        HashMap::new,
                        Collectors.mapping(
                                error -> error.getDefaultMessage(),
                                Collectors.toList()
                        )
                ));

        ProblemDetail problem = createProblem(
                status,
                "Ошибка валидации",
                "Переданные данные не прошли проверку"
        );

        problem.setProperty("errors", errors);

        return handleExceptionInternal(
                ex, problem, headers, status, request
        );
    }

    // Некорректный JSON или ошибка десериализации
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ProblemDetail problem = createProblem(
                status,
                "Некорректный запрос",
                "Некорректное тело запроса или недопустимое значение поля"
        );

        return handleExceptionInternal(
                ex, problem, headers, status, request
        );
    }

    // Валидация параметров контроллера в Spring MVC 6.1+
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ProblemDetail problem = createProblem(
                status,
                "Ошибка валидации параметров",
                "Один или несколько параметров запроса не прошли проверку"
        );

        return handleExceptionInternal(
                ex, problem, headers, status, request
        );
    }

    // Валидация через @Validated и Bean Validation
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex) {

        Map<String, List<String>> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.groupingBy(
                        violation -> {
                            String path =
                                    violation.getPropertyPath().toString();
                            return path.substring(path.lastIndexOf('.') + 1);
                        },
                        HashMap::new,
                        Collectors.mapping(
                                violation -> violation.getMessage(),
                                Collectors.toList()
                        )
                ));

        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации параметров",
                "Один или несколько параметров не прошли проверку"
        );

        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }

    private ProblemDetail createProblem(
            HttpStatusCode status,
            String title,
            String detail) {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(status, detail);

        problem.setTitle(title);

        return problem;
    }
}