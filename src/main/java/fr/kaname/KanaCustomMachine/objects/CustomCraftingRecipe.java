package fr.kaname.KanaCustomMachine.objects;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomCraftingRecipe {

    private String name;
    private Map<Integer, ItemStack> input;
    private Map<Integer, ItemStack> output;

    public CustomCraftingRecipe(String name) {

        this.name = name;
        this.input = new HashMap<>();
        this.output = new HashMap<>();
    }

    public void addInput(int slot, ItemStack item) {
        this.input.put(slot, item);
    }

    public void addOutput(int slot, ItemStack item) {
        this.output.put(slot, item);
    }

    public boolean isInputMaterialValid(int slot, ItemStack itemStack) {

        Material material = itemStack.getType();

        // Si le slot n'est pas utilisé alors on valide
        if (material == Material.AIR && !this.input.containsKey(slot)) {
            return true;
        }

        // Si le slot est utilisé
        if (this.input.containsKey(slot)) {

            // On check si l'item possède des attribus
            if (!this.input.get(slot).hasItemMeta()) { // Si non on vérifie le material
                return this.input.get(slot).getType().equals(material);
            }

            // Si oui on vérifie l'ItemMeta en plus du material
            if (itemStack.hasItemMeta()) {
                if (this.input.get(slot).getType().equals(material)) {
                    return this.input.get(slot).getItemMeta().equals(itemStack.getItemMeta());
                }
            }
        }

        return false;
    }

    public ItemStack getOutputItemStack(int slot) {
        if (this.output.containsKey(slot)) {
            return this.output.get(slot);
        }

        return new ItemStack(Material.AIR);
    }

    public Map<Integer, ItemStack> getOutput() {
        return output;
    }
}
