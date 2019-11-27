package service.client.utils;


import service.client.exceptions.InvalidDataException;

public class SearchCriteria {
    private String key;
    private SearchOption operation;
    private Object value;

    public SearchCriteria(String key, Object value, String operation) throws InvalidDataException {
        this.key = key;
        this.operation = SpecificationUtils.searchOptionFromStr(operation);
        this.value = value;
    }

    String getKey() {
        return key;
    }

    SearchOption getOperation() {
        return operation;
    }

    Object getValue() {
        return value;
    }

}
