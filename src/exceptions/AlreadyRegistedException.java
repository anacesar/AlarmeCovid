package exceptions;

public class AlreadyRegistedException extends Exception {

    public AlreadyRegistedException() {super("Username already used!");}
    public AlreadyRegistedException(String message) {super(message);}
}
