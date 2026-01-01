package vn.quahoa.flowershop.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s with id %s not found", resourceName, id));
    }
}
