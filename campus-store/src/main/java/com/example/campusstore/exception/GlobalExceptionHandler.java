package com.example.campusstore.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(ForbiddenException.class)
    public String handleForbidden(ForbiddenException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "forbidden";
    }

    @ExceptionHandler({ValidationException.class, OutOfStockException.class})
    public String handleBusinessErrors(RuntimeException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneric(Exception ex, Model model) {
        model.addAttribute("message", "Something went wrong.");
        return "error";
    }
}