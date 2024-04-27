package fr.kaname.KanaCustomMachine.objects;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class CustomCraftingRecipe {

    private String name;
    private Map<Integer, Material> input;
    private Map<Integer, Material> output;

    public CustomCraftingRecipe(String name) {

        this.name = name;
        this.input = new HashMap<>();
        this.output = new HashMap<>();
    }

    public void addInput(int slot, Material material) {
        this.input.put(slot, material);
    }

    public void addOutput(int slot, Material material) {
        this.output.put(slot, material);
    }

    public boolean isInputMaterialValid(int slot, Material material) {

        if (material == Material.AIR && !this.input.containsKey(slot)) {
            return true;
        }

        if (this.input.containsKey(slot)) {
            return this.input.get(slot).equals(material);
        }

        return false;
    }

    public Material getOutputMaterial(int slot) {
        if (this.output.containsKey(slot)) {
            return this.output.get(slot);
        }

        return Material.AIR;
    }

    public Map<Integer, Material> getOutput() {
        return output;
    }
}
