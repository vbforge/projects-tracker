package com.vbforge.projectstracker.exception;

//when trying to create a resource that already exists
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName,  fieldValue));
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

}
