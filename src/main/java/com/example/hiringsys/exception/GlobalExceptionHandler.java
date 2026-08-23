package com.example.hiringsys.exception;

import com.example.hiringsys.dto.error.ApiErrorResponse;
import com.example.hiringsys.dto.error.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> tratarRecursoNaoEncontrado(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                exception.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ApiErrorResponse> tratarTransicaoDeStatusInvalida(
            InvalidStatusTransitionException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.UNPROCESSABLE_CONTENT,
                "Transição de status inválida",
                exception.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiErrorResponse> tratarRegraDeNegocio(
            BusinessRuleException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.CONFLICT,
                "Regra de negócio violada",
                exception.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> tratarCamposInvalidos(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<FieldValidationError> fields = new ArrayList<>();

        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                fields.add(new FieldValidationError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
        );

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Dados inválidos",
                "Existem campos inválidos na requisição",
                request.getRequestURI(),
                fields
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> tratarCorpoInvalido(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido",
                "Verifique o formato do JSON e os valores enviados",
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> tratarParametroInvalido(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String mensagem = "O parâmetro " + exception.getName() + " possui um valor inválido";

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Parâmetro inválido",
                mensagem,
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> tratarViolacaoDeIntegridade(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.CONFLICT,
                "Conflito de dados",
                "A operação viola uma restrição do banco de dados",
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> tratarFalhaDeAutenticacao(
            AuthenticationException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.UNAUTHORIZED,
                "Falha de autenticação",
                "Usuário ou senha inválidos",
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarErroInesperado(
            Exception exception,
            HttpServletRequest request
    ) {
        LOGGER.error("Erro inesperado ao processar {}", request.getRequestURI(), exception);

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro inesperado no servidor",
                request.getRequestURI(),
                List.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> criarResposta(
            HttpStatus status,
            String error,
            String message,
            String path,
            List<FieldValidationError> fields
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message,
                path,
                fields
        );

        return ResponseEntity.status(status).body(response);
    }
}
