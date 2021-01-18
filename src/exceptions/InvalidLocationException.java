package exceptions;

public class InvalidLocationException extends Exception {

    public InvalidLocationException() {super("Username already used!");}
    public InvalidLocationException(String message) {super(message);}
}
