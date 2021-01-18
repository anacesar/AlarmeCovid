package exceptions;

public class SpecialPasswordInvalidException extends Exception{

    public SpecialPasswordInvalidException() {super("Special password wrong..");}
    public SpecialPasswordInvalidException(String message) {super(message);}
}
