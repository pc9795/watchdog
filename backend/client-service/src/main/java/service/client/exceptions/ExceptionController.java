package service.client.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import service.client.utils.Constants;
import service.client.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Purpose: For custom implementation of common errors.
 */
@ControllerAdvice
public class ExceptionController {
    private static Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    /**
     * Handle resource not found exception
     *
     * @param response response object
     * @throws IOException if not able to update the response object
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleResourceNotFound(Exception e, HttpServletResponse response) throws IOException {
        Utils.createJSONErrorResponse(HttpServletResponse.SC_NOT_FOUND, String.format(Constants.ErrorMsg.NOT_FOUND,
                e.getMessage()), response);
    }

    /**
     * Handles exception raised by validation api.
     *
     * @param exc      exception object
     * @param response response object
     * @throws IOException if not able to update the response object
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValidException(MethodArgumentNotValidException exc,
                                                      HttpServletResponse response) throws IOException {
        Map<String, String> errors = new HashMap<>();
        exc.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        Utils.createJSONErrorResponse(HttpServletResponse.SC_BAD_REQUEST, errors.toString(), response);
    }

    /**
     * Handles exception for forbidden access of the resources
     *
     * @param response response object
     * @throws IOException if not able to update the response object
     */
    @ExceptionHandler(ForbiddenResourceException.class)
    public void handleForbiddenResourceException(HttpServletResponse response) throws IOException {
        Utils.createJSONErrorResponse(HttpServletResponse.SC_FORBIDDEN, Constants.ErrorMsg.FORBIDDEN_RESOURCE, response);
    }

    /**
     * Exception when given client request is not in expected format.
     *
     * @param response response object
     * @throws IOException if not able to update the response object
     */
    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMediaTypeNotSupportedException.class})
    public void handleInvalidClientRequests(HttpServletResponse response) throws IOException {
        Utils.createJSONErrorResponse(HttpServletResponse.SC_BAD_REQUEST, Constants.ErrorMsg.BAD_REQUEST, response);
    }


    /**
     * Exception when given client request is not in expected format. In this method we send more detailed response
     * in comparison to another method which handles invalid client requests
     *
     * @param e        exception object
     * @param response response object
     * @throws IOException if not able to update the response object
     */
    @ExceptionHandler({BadCredentialsException.class, BadDataException.class,
            MissingServletRequestParameterException.class, HttpRequestMethodNotSupportedException.class,
            UserAlreadyExistException.class, ServletException.class})
    public void handleInvalidClientRequestsWithExcMessages(Exception e, HttpServletResponse response) throws IOException {
        Utils.createJSONErrorResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), response);
    }

    /**
     * Exception if monitoring service is not working correctly
     *
     * @param e        exception object
     * @param response response object
     * @throws IOException if not able to update the response object
     */
    @ExceptionHandler(MonitoringServiceException.class)
    public void monitoringServiceException(Exception e, HttpServletResponse response) throws IOException {
        LOGGER.error(String.format("Something bad happened:%s", e.getMessage()), e);
        Utils.createJSONErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage(), response);
    }

    /**
     * A catch all block.
     *
     * @param e        exception object
     * @param response response object
     * @throws IOException if not able to update the response object
     */
    @ExceptionHandler(Exception.class)
    public void handleAll(Exception e, HttpServletResponse response) throws IOException {
        LOGGER.error(String.format("Something bad happened:%s", e.getMessage()), e);
        Utils.createJSONErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Constants.ErrorMsg.INTERNAL_SERVER_ERROR, response);
    }
}
