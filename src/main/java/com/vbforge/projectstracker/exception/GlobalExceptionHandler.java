package com.vbforge.projectstracker.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    //handle ResourceNotFoundException (404)
    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Resource not found: {}", ex.getMessage());

        ModelAndView modelAndView = new ModelAndView("error/404");

        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("path", request.getRequestURI());

        return modelAndView;
    }

    //handle DuplicateResourceException (409 Conflict)
    @ExceptionHandler(value = DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateResource(DuplicateResourceException ex, RedirectAttributes redirectAttributes) {
        log.error("Duplicate resource found: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("error", ex.getMessage());

        return "redirect:/projects";
    }

    //handle InvalidOperationException (400 Bad Request)
    @ExceptionHandler(value = InvalidOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidOperation(InvalidOperationException ex, RedirectAttributes redirectAttributes) {
        log.error("Invalid operation found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());

        return "redirect:/projects";
    }

    //handle validation errors from @Valid annotations
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationErrors(MethodArgumentNotValidException ex, RedirectAttributes redirectAttributes) {
        BindingResult result = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        for(FieldError error: result.getFieldErrors()){
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.error("Validation errors found: {}", errors);

        String errorMessage = "Validation failed: " + String.join(", ",  errors.values());

        redirectAttributes.addFlashAttribute("error", errorMessage);
        redirectAttributes.addFlashAttribute("validationErrors", errors);

        return "redirect:/projects/new";
    }

    //handle IllegalArgumentException (400 Bad Request)
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
        log.error("Illegal argument found: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("error", "Invalid input: " + ex.getMessage());

        return "redirect:/projects";
    }

    //handle generic RuntimeException (500)
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        log.error("RuntimeException found: {}", ex.getMessage());

        ModelAndView modelAndView = new ModelAndView("error/500");
        modelAndView.addObject("errorMessage", "An unexpected error occurred. Please try again later.");
        modelAndView.addObject("technicalDetails", ex.getMessage());
        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("path", request.getRequestURI());

        return modelAndView;
    }

    //handle all other exceptions
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("An unexpected error occurred. Exception Message: {} Please try again later.",  ex.getMessage());

        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorMessage", "An unexpected error occurred. Please contact support if this persists.");
        mav.addObject("technicalDetails", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("path", request.getRequestURI());

        return mav;
    }


}
















