package fr.kaname.KanaCustomMachine.enums;

public enum MachineType {

    CRAFTING, LARGE_CRAFTING, PROCESSING, INFUSING;

    @Override
    public String toString() {
        return this.name();
    }
}
