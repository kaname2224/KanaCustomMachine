package fr.kaname.KanaCustomMachine.objects;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CraftingMachine {

    private String name;
    private Structure structure;
    private String recipe_file;
    private Map<Integer, Integer> inputSlot;
    private Map<Integer, Integer> outputSlot;
    private ItemStack activationItem;
    private int size;
    private Material filling_material;
    private List<CustomCraftingRecipe> customCraftingRecipes;
    private CustomInventory customInventory;

    public CraftingMachine(String name,
                           String recipe_file,
                           int size,
                           Material filling_material,
                           CustomInventory inventory,
                           Structure structure
    ) {
        this.size = size;
        this.filling_material = filling_material;
        this.inputSlot = new HashMap<>();
        this.outputSlot = new HashMap<>();
        this.customCraftingRecipes = new ArrayList<>();
        this.customInventory = inventory;
        this.name = name;
        this.recipe_file = recipe_file;
        this.structure = structure;
    }

    public ItemStack getActivationItem() {
        return activationItem;
    }

    public int getSize() {
        return size;
    }

    public Material getFilling_material() {
        return filling_material;
    }

    public void addInput(int slot, int number) {
        this.inputSlot.put(slot, number);
    }

    public void addOutput(int slot, int number) {
        this.outputSlot.put(number, slot);
    }

    public void regenerateGUI() {

        ItemStack[] itemStacks = new ItemStack[this.customInventory.getInventory().getSize()];

        for (int i = 0; i < this.customInventory.getInventory().getSize(); i++) {
            itemStacks[i] = new ItemStack(this.filling_material);
        }

        for (int slot : this.inputSlot.keySet()) {
            itemStacks[slot] = new ItemStack(Material.AIR);
        }

        for (int slot : this.outputSlot.values()) {
            itemStacks[slot] = new ItemStack(Material.AIR);
        }

        this.customInventory.getInventory().setContents(itemStacks);
    }

    public String getName() {
        return name;
    }

    public Structure getStructure() {
        return structure;
    }

    public String getRecipe_file() {
        return recipe_file;
    }

    public void addCustomRecipe(CustomCraftingRecipe customCraftingRecipe) {
        if (!this.customCraftingRecipes.contains(customCraftingRecipe)) {
            this.customCraftingRecipes.add(customCraftingRecipe);
        }
    }
    public List<CustomCraftingRecipe> getCustomCraftingRecipes() {
        return customCraftingRecipes;
    }

    public Map<Integer, Integer> getInputSlot() {
        return inputSlot;
    }

    public Map<Integer, Integer> getOutputSlot() {
        return outputSlot;
    }

    public boolean hasActivationItem() {
        return activationItem != null;
    }

    public void setActivationItem(ItemStack activationItem) {
        this.activationItem = activationItem;
    }

    public Inventory getInventory() {
        return customInventory.getInventory();
    }

    public boolean checkActivationItem(ItemStack activationItem) {
        if (this.activationItem == null) {
            return true;
        }

        if (!this.activationItem.hasItemMeta()) {
            return this.activationItem.getType() == activationItem.getType();
        } else {
            return activationItem.hasItemMeta() && this.activationItem.getItemMeta().equals(activationItem.getItemMeta());
        }

    }
}
