package exceptions;

public class InvalidLocationException extends Exception {

    public InvalidLocationException() {super("This node is not available!");}
    public InvalidLocationException(String message) {super(message);}
}
