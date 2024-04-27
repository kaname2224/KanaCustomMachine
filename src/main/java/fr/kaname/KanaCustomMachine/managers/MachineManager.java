package fr.kaname.KanaCustomMachine.managers;

import fr.kaname.KanaCustomMachine.KanaCustomMachine;
import fr.kaname.KanaCustomMachine.enums.Folders;
import fr.kaname.KanaCustomMachine.objects.CraftingMachine;
import fr.kaname.KanaCustomMachine.objects.CustomCraftingRecipe;
import fr.kaname.KanaCustomMachine.objects.CustomInventory;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MachineManager {

    KanaCustomMachine plugin;
    Map<String, CraftingMachine> MachinesInMemory = new HashMap<>();
    Map<Player, CraftingMachine> MachineOpen = new HashMap<>();

    public MachineManager(KanaCustomMachine plugin) {
        this.plugin = plugin;
    }

    public void loadMachinesInFiles() {

        File[] fileList = this.plugin.getFolder(Folders.MACHINES).listFiles();

        for (File file : fileList) {
            loadMachineFile(file);
        }


    }

    public void openCraftingMachine(Player player, CraftingMachine machine) {
        MachineOpen.put(player, machine);
        player.openInventory(machine.generateGUI());
    }

    private void addMachineToMemory(CraftingMachine machine) {
        if (!this.MachinesInMemory.containsKey(machine.getName())) {
            this.MachinesInMemory.put(machine.getName(), machine);
        }
    }

    public CraftingMachine getMachine(String name) {
        if (this.MachinesInMemory.containsKey(name)) {
            return this.MachinesInMemory.get(name);
        }

        return null;
    }

    public void loadMachineFile(File file) {

        YamlConfiguration machineConfig = YamlConfiguration.loadConfiguration(file);
        this.plugin.getLogger().log(Level.INFO, "Loading : " + machineConfig.getString("name"));

        CraftingMachine craftingMachine = this.loadCraftingMachine(machineConfig);
        this.addMachineToMemory(craftingMachine);


    }

    public CraftingMachine getOpenMachine(Player player) {
        if (this.MachineOpen.containsKey(player)) {
            return this.MachineOpen.get(player);
        }

        return null;
    }

    public void processCrafting(CraftingMachine machine, Inventory inventory) {

        for (CustomCraftingRecipe customCraftingRecipe : machine.getCustomCraftingRecipes()) {
            boolean crafting = false;

            for (int inputSlot : machine.getInputSlot().keySet()) {

                ItemStack inputItemStack = inventory.getContents()[inputSlot];

                if (inputItemStack == null) {
                    inputItemStack = new ItemStack(Material.AIR);
                }

                Material material = inputItemStack.getType();
                int inputCraftingSlot = machine.getInputSlot().get(inputSlot);


                if (customCraftingRecipe.isInputMaterialValid(inputCraftingSlot, material)) {
                    crafting = true;
                } else {
                    crafting = false;
                    break;
                }

            }

            if (crafting) {
                Material resultMaterial = customCraftingRecipe.getOutputMaterial(0);
                ItemStack itemStack = new ItemStack(resultMaterial);
                inventory.setItem(machine.getOutputSlot().get(0), itemStack);
                break;
            } else {
                ItemStack itemStack = new ItemStack(Material.AIR);
                inventory.setItem(machine.getOutputSlot().get(0), itemStack);
            }

        }

    }

    public CraftingMachine loadCraftingMachine(YamlConfiguration config) {

        String name = config.getString("name");
        String recipe_file = config.getString("recipe_file");
        int size = config.getInt("gui.size");
        Material material = Material.valueOf(config.getString("gui.fill"));

        CraftingMachine craftingMachine = new CraftingMachine(name, recipe_file, size, material, new CustomInventory(this.plugin, size));
        File file = new File(this.plugin.getFolder(Folders.RECIPE_CRAFTING), recipe_file);
        YamlConfiguration craftingConfiguration = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getConfigurationSection("gui.inventory").getKeys(false)) {


            ConfigurationSection slotConfig = config.getConfigurationSection("gui.inventory." + key);
            String slotType = slotConfig.getString("type");

            if (slotType.equals("INPUT")) {
                craftingMachine.addInput(Integer.parseInt(key), slotConfig.getInt("number"));
            } else if (slotType.equals("OUTPUT")) {
                craftingMachine.addOutput(Integer.parseInt(key), slotConfig.getInt("number"));
            }
        }

        for (String key : craftingConfiguration.getKeys(false)) {

            CustomCraftingRecipe customCraftingRecipe = new CustomCraftingRecipe(key);

            ConfigurationSection recipeConfig = craftingConfiguration.getConfigurationSection(key);
            ConfigurationSection output = recipeConfig.getConfigurationSection("output");
            ConfigurationSection ingredient = recipeConfig.getConfigurationSection("ingredient");

            for (String slot : output.getKeys(false)){
                customCraftingRecipe.addOutput(Integer.parseInt(slot), Material.valueOf(output.getString(slot)));
            }

            for (String slot : ingredient.getKeys(false)){
                customCraftingRecipe.addInput(Integer.parseInt(slot), Material.valueOf(ingredient.getString(slot)));
            }

            craftingMachine.addCustomRecipe(customCraftingRecipe);
        }

        return craftingMachine;
    }
}
