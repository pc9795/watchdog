package service.client.utils;

import org.springframework.data.jpa.domain.Specification;
import service.client.exceptions.InvalidDataException;
import service.client.exceptions.InvalidSearchAttributeException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpecificationUtils {
    /**
     * Convert the search option representation to a uniform representation.
     *
     * @param optionStr
     * @return
     * @throws InvalidDataException
     */
    static SearchOption searchOptionFromStr(String optionStr) throws InvalidDataException {
        switch (optionStr) {
            case "eq":
                return SearchOption.EQUALS;
            case "gt":
                return SearchOption.GREATER_THAN;
            case "ge":
                return SearchOption.GREATER_THAN_OR_EQUAL_TO;
            case "lt":
                return SearchOption.LESS_THAN;
            case "le":
                return SearchOption.LESS_THAN_OR_EQUAL_TO;
            case "and":
                return SearchOption.AND;
            case "or":
                return SearchOption.OR;
        }
        throw new InvalidDataException(String.format("Not a valid search option %s", optionStr));
    }

    /**
     * Convert a infix search representation to postfix.
     *
     * @param query
     * @return
     * @throws InvalidDataException
     */
    private static List<String> infixToPostFix(String query) throws InvalidDataException {
        SearchQueryTokenizer tokenizer = new SearchQueryTokenizer(query);
        List<String> result = new ArrayList<>();
        ArrayDeque<String> stack = new ArrayDeque<>();
        String token = tokenizer.nextToken();

        while (token != null) {
            if (isOperand(token)) {
                result.add(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    result.add(stack.pop());
                }
                if (stack.isEmpty()) {
                    throw new InvalidDataException("Unbalanced braces");
                }
                stack.pop();
            } else {
                while (!stack.isEmpty() && precedence(token) <= precedence(stack.peek())) {
                    result.add(stack.pop());
                }
                stack.push(token);
            }
            token = tokenizer.nextToken();
        }
        while (!stack.isEmpty()) {
            if (stack.peek().equals("(")) {
                throw new InvalidDataException("Unbalanced braces");
            }
            result.add(stack.pop());
        }
        return result;
    }

    /**
     * Build specification from the postfix notation of queries.
     *
     * @param postfix
     * @param <T>
     * @return
     * @throws InvalidDataException
     */
    private static <T> Specification<T> buildSpecFromPostfixList(List<String> postfix,
                                                                 SpecificationAttrConverter converter)
            throws InvalidDataException {
        ArrayDeque<Specification<T>> stack = new ArrayDeque<>();
        int size = postfix.size();

        for (int i = 0; i < size; i++) {
            if (isOperand(postfix.get(i))) {
                // The query will always has the basic entity as "a op b" which will be joined by "and" and "or"
                // and there priority configured by braces.
                // So during postfix conversion the "a op b" will be converted to "a b op". Here while converting
                // to specification we will treat it as a single entity and extract it together. These 3 things will
                // be taken out together and expected to be in the form of "a b op" else the query is wrong.
                if (size - i < 3) {
                    throw new InvalidDataException("Invalid search query");
                }
                stack.push(new ApiSpecification<>(
                        new SearchCriteria(postfix.get(i), postfix.get(i + 1), postfix.get(i + 2)), converter));
                i += 2;
            } else {
                // Because we extract a unit "a op b" together and braces are already removed in postfix processing
                // So only thing which is left for consideration is "and" and "or". If any thing else is found then
                // the query is corrupt.
                SearchOption option = searchOptionFromStr(postfix.get(i));
                if (option != SearchOption.AND && option != SearchOption.OR) {
                    throw new InvalidDataException("Found options other than AND and OR after Infix conversion");
                }
                //There should be 2 units of specification for processing.
                if (stack.size() < 2) {
                    throw new InvalidDataException("Invalid search query");
                }
                Specification<T> first = stack.pop();
                Specification<T> second = stack.pop();
                if (option == SearchOption.OR) {
                    stack.push(first.or(second));
                } else {
                    stack.push(first.and(second));
                }
            }
        }
        return stack.peek();

    }

    // List of all reserved words which should not be taken as operands.
    private static List<String> reserved = Arrays.asList("gt", "ge", "lt", "le", "eq", "and", "or", "(", ")");

    /**
     * Check whether a token is operand or not.
     *
     * @param op
     * @return
     */
    private static boolean isOperand(String op) {
        return !reserved.contains(op);
    }

    /**
     * Precdence of operators which will help in infix to postfix conversion.
     *
     * @param op
     * @return
     * @throws InvalidDataException
     */
    private static int precedence(String op) throws InvalidDataException {
        if (op.equals("(")) {
            return 0;
        }
        SearchOption searchOption = searchOptionFromStr(op);
        switch (searchOption) {
            case GREATER_THAN:
            case GREATER_THAN_OR_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL_TO:
                return 4;
            case EQUALS:
                return 3;
            case AND:
                return 2;
            case OR:
                return 1;
        }
        throw new InvalidDataException(String.format("Can't get precedence of: %s", op));
    }

    /**
     * Method facing outside world to combine the working of this class.
     *
     * @param query
     * @return
     * @throws InvalidDataException
     */
    public static Specification getSpecFromQuery(String query, SpecificationAttrConverter converter)
            throws InvalidDataException {
        List<String> postFixedQuery = infixToPostFix(query);
        return buildSpecFromPostfixList(postFixedQuery, converter);
    }

    /**
     * Class to tokenize a search query.
     */
    public static class SearchQueryTokenizer {
        private final String query;
        private int counter = 0;

        SearchQueryTokenizer(String query) {
            this.query = query.trim();
        }

        /**
         * Get the next token from the string.
         *
         * @return
         */
        String nextToken() {
            //Remove spaces;
            for (; counter < query.length(); counter++) {
                if (query.charAt(counter) != ' ') {
                    break;
                }
            }
            if (counter == query.length()) {
                return null;
            }

            //Send braces as tokens.
            char c = query.charAt(counter);
            if (c == '(' || c == ')') {
                counter++;
                return "" + c;
            }

            //Retrieve next token. We assume everything is separated by space.
            int first = counter;
            for (; counter < query.length(); counter++) {
                c = query.charAt(counter);
                if (c == ' ' || c == ')') {
                    break;
                }
            }
            return query.substring(first, counter);
        }
    }

    /**
     * Attribute converter for User entity. We know that the value will be string.
     *
     * @param key
     * @param value
     * @return
     */
    public static Object userAttributeConverter(String key, Object value) throws InvalidSearchAttributeException {
        try {
            switch (key) {
                case "username":
                    return value;
            }

        } catch (Exception e) {
            throw new InvalidSearchAttributeException(String.format("Not able to convert Value: %s of Attribute: %s",
                    value, key));
        }
        // When nothing is mapped in switch.
        throw new InvalidSearchAttributeException(String.format("Attribute'%s' is not mapped", key));
    }

    /**
     * Attribute converter for Meal entity. We know that the value will be string.
     *
     * @param key
     * @param value
     * @return
     */
    public static Object mealAttributeConverter(String key, Object value) throws InvalidSearchAttributeException {
        try {
            switch (key) {
                case "date":
                    return LocalDate.parse(value.toString());
                case "time":
                    return LocalTime.parse(value.toString());
                case "text":
                    return value;
                case "calories":
                    return Integer.parseInt(value.toString());
                case "lessThanExpected":
                    return Boolean.parseBoolean(value.toString());
            }

        } catch (Exception e) {
            throw new InvalidSearchAttributeException(String.format("Not able to convert Value: %s of Attribute: %s",
                    value, key));
        }
        // When nothing is mapped in switch.
        throw new InvalidSearchAttributeException(String.format("Attribute '%s' is not mapped", key));
    }

}