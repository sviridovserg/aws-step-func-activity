package exceptions;

public class ActivityExecutionException extends Exception {
    private int itemId;

    public int getItemId() {
        return itemId;
    }

    public ActivityExecutionException(int itemId) {
        super(String.format("Activity execution failed. ({ id: %d })", itemId));
        this.itemId = itemId;
    }

    public ActivityExecutionException(int itemId, String message) {
        super(String.format("Activity execution failed. Reason: %s ({ id: %d })", message, itemId));
        this.itemId = itemId;
    }
}
