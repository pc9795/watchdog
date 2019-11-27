package service.client.utils;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import service.client.entities.UserRole;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;

public final class Utils {
    private Utils() {
    }

    public static String createErrorJSON(int errorCode, String errorMessage) {
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
     * @param errorCode
     * @param errorMessage
     * @param response
     * @throws IOException
     */
    public static void createJSONErrorResponse(int errorCode, String errorMessage, HttpServletResponse response)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(errorCode);
        response.getWriter().write(Utils.createErrorJSON(errorCode, errorMessage));
    }

    /**
     * Join a collection with the given delimeter.
     *
     * @param collection
     * @param delimeter
     * @return joined string representation of collection.
     */
    public static String joinCollection(Collection collection, String delimeter) {
        if (collection.size() == 0) {
            return "";
        }
        int i = 0;
        int size = collection.size();
        StringBuilder sb = new StringBuilder();
        Iterator iterator = collection.iterator();
        while (i++ < size - 1) {
            sb.append(iterator.next()).append(delimeter).append(" ");
        }
        sb.append(iterator.next());
        return sb.toString();
    }

    /**
     * Check the principal object is having a Admin role or not.
     *
     * @param principal
     * @return
     */
    public static boolean isPrincipalAdmin(Principal principal) {
        UserDetails userDetails = (UserDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_" + UserRole.UserRoleType.ADMIN)) {
                return true;
            }
        }
        return false;
    }
}
