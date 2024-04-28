package fr.kaname.KanaCustomMachine.objects;

import fr.kaname.KanaCustomMachine.enums.Rotation;
import org.bukkit.Location;

public class PlayerMachine {


    private CraftingMachine machine;

    private Rotation rotation;

    private Location reference;

    public PlayerMachine(CraftingMachine machine, Rotation rotation, Location reference) {
        this.machine = machine;
        this.rotation = rotation;
        this.reference = reference;
    }

    public CraftingMachine getMachine() {
        return machine;
    }

    public void setMachine(CraftingMachine machine) {
        this.machine = machine;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public Location getReference() {
        return reference;
    }

    public void setReference(Location reference) {
        this.reference = reference;
    }
}
