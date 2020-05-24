package objects;

public class Component {

    private int keyboardId;
    private String componentType;
    private String componentName;
    private String componentBrand;
    private float keyPressure;
    private int keyTravel;
    private String addedDate;
    private String removedDate;
    private int keyStrokes;
    private boolean isActive;

    // TODO get the keyStrokes, update the keystrokes in the table
    public Component(int keyboardId, String componentType, String componentName,
                     String componentBrand, float keyPressure, int keyTravel,
                     String addedDate, String removedDate, int keyStrokes, boolean isActive) {
        this.keyboardId = keyboardId;
        this.componentType = componentType;
        this.componentName = componentName;
        this.componentBrand = componentBrand;
        this.keyPressure = keyPressure;
        this.keyTravel = keyTravel;
        this.addedDate = addedDate;
        this.removedDate = removedDate;
        this.keyStrokes = keyStrokes;
        this.isActive = isActive;
    }

    public int getKeyboardId() {
        return keyboardId;
    }

    public String getComponentType() {
        return componentType;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getComponentBrand() {
        return componentBrand;
    }

    public float getKeyPressure() {
        return keyPressure;
    }

    public int getKeyTravel() {
        return keyTravel;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public String getRemovedDate(){
        return removedDate;
    }

    public int getKeyStrokes(){
        return keyStrokes;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public void setRemovedDate(String removedDate) {
        this.removedDate = removedDate;
    }
}
