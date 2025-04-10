package com.example.marketing_agency_app.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.postgresql.util.PSQLException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработчик исключений, возникающих при сохранении данных.
     * В частности, ловит ситуацию, когда поле "demographics" имеет тип JSONB, а передаётся строка.
     */
    @ExceptionHandler({ InvalidDataAccessResourceUsageException.class, PSQLException.class })
    public String handleInvalidDataAccessException(Exception ex, HttpServletRequest request, Model model) {
        String uri = request.getRequestURI();
        String errorMessage = "Произошла ошибка при сохранении данных. " + ex.getMessage();

        // Если ошибка произошла при сохранении аудитории, скорее всего проблема с демографическими данными.
        if (uri.contains("/audience/save") || uri.contains("/audience/edit")) {
            errorMessage = "Ошибка сохранения сегмента аудитории: поле 'Демографические данные' должно быть в формате JSON. Проверьте корректность ввода.";
            model.addAttribute("errorMessage", errorMessage);
            // Дополнительно можно вернуть введённый объект, если он передавался в форме.
            return "audience_form"; // возвращаем шаблон формы для аудитории
        }
        // Если ошибка в сохранении кампании
        else if (uri.contains("/campaign/save") || uri.contains("/campaign/edit")) {
            errorMessage = "Ошибка сохранения кампании: проверьте введённые данные.";
            model.addAttribute("errorMessage", errorMessage);
            return "campaign_form";
        }
        // Если ошибка в сохранении канала
        else if (uri.contains("/channel/save") || uri.contains("/channel/edit")) {
            errorMessage = "Ошибка сохранения канала: проверьте введённые данные.";
            model.addAttribute("errorMessage", errorMessage);
            return "channel_form";
        }
        // Для всех остальных случаев – общий шаблон ошибки или возврат на главную страницу
        else {
            model.addAttribute("errorMessage", errorMessage);
            return "error"; // создайте шаблон error.html для отображения ошибок
        }
    }
}
