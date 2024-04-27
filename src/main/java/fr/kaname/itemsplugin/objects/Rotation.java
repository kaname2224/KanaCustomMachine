package fr.kaname.itemsplugin.objects;

public enum Rotation {

    DEFAULT, ROTATE90, ROTATE180, ROTATE_MINUS_90;

    @Override
    public String toString() {
        return this.name();
    }
}
