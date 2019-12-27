package service.client.utils;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Purpose: Utility methods for the project
 */
public final class Utils {
    private Utils() {
    }

    /**
     * Create a standard JSON message with given error code and message.
     *
     * @param errorCode    error code
     * @param errorMessage error message
     * @return json string formatted with given error code and message.
     */
    private static String createErrorJSON(int errorCode, String errorMessage) {
        ObjectNode errorNode = JsonNodeFactory.instance.objectNode();
        errorNode.put("code", errorCode);
        errorNode.put("message", errorMessage);
        ObjectNode root = JsonNodeFactory.instance.objectNode();
        root.set("error", errorNode);
        return root.toString();
    }

    /**
     * Utility method to create a JSON response for a particular error code and message.
     *
     * @param errorCode    error code
     * @param errorMessage error message
     * @param response     response object
     * @throws IOException if not able to update the response object
     */
    public static void createJSONErrorResponse(int errorCode, String errorMessage, HttpServletResponse response)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCode);
        response.getWriter().write(Utils.createErrorJSON(errorCode, errorMessage));
    }
}
