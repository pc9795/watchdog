package service.client.exceptions;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import service.client.utils.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static service.client.utils.Utils.createJSONErrorResponse;


@ControllerAdvice
public class ExceptionController {


    /**
     * Resource url is not mapped.
     *
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public void handle404(HttpServletResponse response) throws IOException {
        createJSONErrorResponse(HttpServletResponse.SC_NOT_FOUND, Constants.ErrorMsg.RESOURCE_NOT_FOUND, response);
    }


    /**
     * User already exists in the database.
     *
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    public void handleUserAlreadyExistException(HttpServletResponse response) throws IOException {
        createJSONErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Constants.ErrorMsg.USER_ALREADY_EXISTS,
                response);
    }

    /**
     * If data is not in the correct format. Custom validations and manual validations done by bean validation api.
     *
     * @param exc
     * @param response
     * @throws IOException
     */
    @ExceptionHandler({InvalidDataException.class, ValidationException.class,
            ServletException.class, InvalidSearchAttributeException.class})
    public void handleInvalidDataException(Exception exc, HttpServletResponse response) throws IOException {
        createJSONErrorResponse(HttpServletResponse.SC_BAD_REQUEST, exc.getMessage(), response);
    }

    /**
     * Errors caused by bean validation api while automatic converting json to beans.
     *
     * @param exc
     * @param response
     * @throws IOException
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
        createJSONErrorResponse(HttpServletResponse.SC_BAD_REQUEST, errors.toString(), response);
    }

    /**
     * If the requested resource is not present.
     *
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(ResourceNotExistException.class)
    public void handleResourceNotExistException(HttpServletResponse response) throws IOException {
        createJSONErrorResponse(HttpServletResponse.SC_BAD_REQUEST, Constants.ErrorMsg.RESOURCE_NOT_FOUND, response);
    }

    /**
     * Custom forbidden resource implementation.
     *
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(ForbiddenResourceException.class)
    public void handleForbiddenResourceException(HttpServletResponse response) throws IOException {
        createJSONErrorResponse(HttpServletResponse.SC_FORBIDDEN, Constants.ErrorMsg.FORBIDDEN_RESOURCE, response);
    }

    /**
     * Bad credentials exception
     *
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(BadCredentialsException.class)
    public void handleBadCredentialsException(HttpServletResponse response) throws IOException {
        createJSONErrorResponse(HttpServletResponse.SC_BAD_REQUEST, Constants.ErrorMsg.BAD_CREDENTIALS, response);
    }

    @ExceptionHandler(Exception.class)
    public void handleAll(Exception e, HttpServletResponse response) throws IOException {
        e.printStackTrace();
        createJSONErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Constants.ErrorMsg.INTERNAL_SERVER_ERROR,
                response);
    }

    @ExceptionHandler(UsernamePasswordIncorrect.class)
    public void handleUserDoesntExistException(Exception e, HttpServletResponse response) throws IOException {
        e.printStackTrace();
        createJSONErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, Constants.ErrorMsg.INCORRECT_PASSWORD,
                response);
    }


}
