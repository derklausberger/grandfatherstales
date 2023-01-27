package utilityClasses;

public class NotSupportedException extends Exception {
    public NotSupportedException(String enemy) {
                super("This EntityType is not supported: " + enemy);
            }
}