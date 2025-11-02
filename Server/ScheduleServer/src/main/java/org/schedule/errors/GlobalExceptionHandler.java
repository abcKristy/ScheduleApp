package org.schedule.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponseDto> handleRestClientException(RestClientException e) {
        log.error("Ошибка при обращении к внешнему API", e);
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Ошибка при получении данных из внешнего источника",
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException e) {
        log.error("Внутренняя ошибка приложения", e);
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Внутренняя ошибка сервера",
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {
        log.error("Непредвиденная ошибка", e);
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Произошла непредвиденная ошибка",
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

/*
 * Список ошибок для обработки в проекте:
 *
 * 1. Валидация входных данных
 *    - IllegalArgumentException - пустые списки, null параметры
 *    - NullPointerException - непроверенные null значения
 *
 * 2. Внешние API вызовы
 *    - RestClientException - ошибки HTTP запросов к MIREA API
 *    - HttpClientErrorException - 4xx ошибки (404, 400 и т.д.)
 *    - HttpServerErrorException - 5xx ошибки сервера
 *    - ResourceAccessException - проблемы с сетью/таймауты
 *
 * 3. Работа с базой данных
 *    - DataAccessException - общие ошибки доступа к данным
 *    - EmptyResultDataAccessException - данные не найдены
 *    - DataIntegrityViolationException - нарушение целостности данных
 *
 * 4. Парсинг и преобразование данных
 *    - DateTimeParseException - ошибки парсинга дат из iCal
 *    - PatternSyntaxException - ошибки в регулярных выражениях
 *    - NumberFormatException - преобразование строк в числа
 *
 * 5. Бизнес-логика
 *    - RuntimeException - общие ошибки в сервисах
 *    - IllegalStateException - некорректное состояние приложения
 *
 * 6. Транзакции
 *    - TransactionException - ошибки управления транзакциями
 *    - TransactionSystemException - системные ошибки транзакций
 *
 * 7. Кэширование (когда реализуется)
 *    - CacheException - ошибки работы с кэшем
 *    - SerializationException - проблемы сериализации данных
 *
 * 8. Общие системные ошибки
 *    - IOException - ошибки ввода/вывода
 *    - OutOfMemoryError - нехватка памяти (критическая)
 *
 * 9. Специфичные для парсера iCal
 *    - Exception в parseEventBlock - ошибки парсинга отдельных событий
 *    - Ошибки валидации isValidLesson - некорректные данные занятий
 *
 * 10. Spring Framework ошибки
 *     - BeanCreationException - ошибки создания бинов
 *     - NoSuchBeanDefinitionException - бин не найден
 */
