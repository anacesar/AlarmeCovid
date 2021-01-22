package exceptions;

public class QuarantineException extends Exception{

    public QuarantineException() {super("You are still in quarantine time.. Please stay at home!");}
    public QuarantineException(String message) {super(message);}
}
