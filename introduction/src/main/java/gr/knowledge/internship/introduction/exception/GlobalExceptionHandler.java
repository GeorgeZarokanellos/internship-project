package gr.knowledge.internship.introduction.exception;

import jakarta.persistence.EntityNotFoundException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<Map<String,Object>> handleEntityNotFoundException(Exception e){
        Map<String, Object> responseBody  = new HashMap<>();
        responseBody.put("error", e.getClass().getSimpleName());
        responseBody.put("message", e.getMessage());
        responseBody.put("statusCode", HttpStatus.NOT_FOUND.value());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
    }
}
