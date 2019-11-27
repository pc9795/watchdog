package service.client.utils;

import service.client.exceptions.InvalidSearchAttributeException;

@FunctionalInterface
public interface SpecificationAttrConverter {

    Object processAttr(String attributeName, Object attributeValue) throws InvalidSearchAttributeException;
}
