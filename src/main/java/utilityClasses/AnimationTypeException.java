package utilityClasses;

public class AnimationTypeException extends Exception {
    public AnimationTypeException(String animation) {
                super("This animationtype caused an error: " + animation);
            }
}