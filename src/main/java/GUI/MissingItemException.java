package GUI;

public class MissingItemException extends Exception {

    public MissingItemException(String item) {

        super("The character does not have this item: " + item);
    }
}
