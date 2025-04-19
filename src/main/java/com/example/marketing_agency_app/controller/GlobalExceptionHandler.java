//package com.example.marketing_agency_app.controller;
//
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(Exception.class)
//    public String handleAll(Exception ex, Model model) {
//        // можно логировать ex
//        model.addAttribute("errorMessage", "Произошла ошибка: " + ex.getMessage());
//        return "error_general"; // простой шаблон с выводом model.errorMessage
//    }
//}
