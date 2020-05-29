package objects;

public class Component {

    private final int id;
    private final int keyboardId;
    private final String componentType;
    private final String componentName;
    private final String componentBrand;
    private final float keyPressure;
    private final int keyTravel;
    private String addedDate;
    private String retiredDate;
    private final int keyStrokes;
    private final boolean isActive;

    public Component(int id, int keyboardId, String componentType, String componentName,
                     String componentBrand, float keyPressure, int keyTravel,
                     String addedDate, String retiredDate, int keyStrokes, boolean isActive) {
        this.id = id;
        this.keyboardId = keyboardId;
        this.componentType = componentType;
        this.componentName = componentName;
        this.componentBrand = componentBrand;
        this.keyPressure = keyPressure;
        this.keyTravel = keyTravel;
        this.addedDate = addedDate;
        this.retiredDate = retiredDate;
        this.keyStrokes = keyStrokes;
        this.isActive = isActive;
    }

    public int getId(){
        return id;
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

    public String getRetiredDate(){
        return retiredDate;
    }

    public int getKeyStrokes(){
        return keyStrokes;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public void setRetiredDate(String retiredDate) {
        this.retiredDate = retiredDate;
    }
}
