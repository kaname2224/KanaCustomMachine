package fr.kaname.KanaCustomMachine.objects;

import fr.kaname.KanaCustomMachine.KanaCustomMachine;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CustomInventory implements InventoryHolder {

    private final Inventory inventory;

    public CustomInventory(KanaCustomMachine plugin, int size) {
        // Create an Inventory with 9 slots, `this` here is our InventoryHolder.
        this.inventory = plugin.getServer().createInventory(this, size);
        this.inventory.setMaxStackSize(1);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
