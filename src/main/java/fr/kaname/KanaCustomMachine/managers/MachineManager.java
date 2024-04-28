package fr.kaname.KanaCustomMachine.managers;

import fr.kaname.KanaCustomMachine.KanaCustomMachine;
import fr.kaname.KanaCustomMachine.enums.Folders;
import fr.kaname.KanaCustomMachine.enums.Rotation;
import fr.kaname.KanaCustomMachine.objects.*;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MachineManager {

    KanaCustomMachine plugin;
    Map<String, CraftingMachine> MachinesInMemory = new HashMap<>();
    Map<Player, PlayerMachine> PlayerMachinesOpen = new HashMap<>();
    Map<Player, CraftingMachine> MachineOpen = new HashMap<>();
    Map<Location, PlayerMachine> PlayerMachinesInMemory = new HashMap<>();

    public MachineManager(KanaCustomMachine plugin) {
        this.plugin = plugin;
    }

    public void loadMachinesInFiles() {

        File[] fileList = this.plugin.getFolder(Folders.MACHINES).listFiles();

        for (File file : fileList) {
            loadMachineFile(file);
        }


    }

    public Map<Location, PlayerMachine> getPlayerMachinesInMemory() {
        return PlayerMachinesInMemory;
    }

    public void openMachinePlayer(Player player, PlayerMachine machine) {
        PlayerMachinesOpen.put(player, machine);
        player.openInventory(machine.getMachine().getInventory());
        plugin.getLogger().log(Level.INFO, this.PlayerMachinesOpen.toString());
    }

    public void closeAndSaveMachinePlayer(Player player) {
        if (this.PlayerMachinesOpen.containsKey(player)) {
            this.PlayerMachinesOpen.remove(player);
        }
    }

    public void openCraftingMachine(Player player, CraftingMachine machine) {
        MachineOpen.put(player, machine);
        player.openInventory(machine.getInventory());
    }

    private void addMachineToMemory(CraftingMachine machine) {
        if (!this.MachinesInMemory.containsKey(machine.getName())) {
            this.MachinesInMemory.put(machine.getName(), machine);
        }
    }

    public Map<String, CraftingMachine> getMachinesInMemory() {
        return MachinesInMemory;
    }

    public CraftingMachine getMachine(String name) {
        if (this.MachinesInMemory.containsKey(name)) {
            return this.MachinesInMemory.get(name);
        }

        return null;
    }

    public void loadMachineFile(File file) {

        YamlConfiguration machineConfig = YamlConfiguration.loadConfiguration(file);
        this.plugin.getLogger().log(Level.INFO, "Loading : " + file.getName());

        CraftingMachine craftingMachine = this.loadCraftingMachine(machineConfig, file.getName());
        this.addMachineToMemory(craftingMachine);


    }

    public PlayerMachine getPlayerMachineOpen(Player player) {
        if (this.PlayerMachinesOpen.containsKey(player)) {
            return this.PlayerMachinesOpen.get(player);
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

                int inputCraftingSlot = machine.getInputSlot().get(inputSlot);

                if (customCraftingRecipe.isInputMaterialValid(inputCraftingSlot, inputItemStack)) {
                    crafting = true;
                } else {
                    crafting = false;
                    break;
                }

            }

            if (crafting) {
                ItemStack itemStack = customCraftingRecipe.getOutputItemStack(0);
                inventory.setItem(machine.getOutputSlot().get(0), itemStack);
                break;
            } else {
                ItemStack itemStack = new ItemStack(Material.AIR);
                inventory.setItem(machine.getOutputSlot().get(0), itemStack);
            }

        }

    }

    public CustomCraftingRecipe loadCraftingRecipe(ConfigurationSection craftingConfiguration, String name) {

        CustomCraftingRecipe customCraftingRecipe = new CustomCraftingRecipe(name);
        plugin.getLogger().log(Level.INFO, "Registering Crafting : " + name);

        ConfigurationSection output = craftingConfiguration.getConfigurationSection("output");
        ConfigurationSection ingredient = craftingConfiguration.getConfigurationSection("ingredient");

        String itemType = null;

        for (String slot : output.getKeys(false)) {

            if (output.getConfigurationSection(slot) != null) {
                itemType = output.getConfigurationSection(slot).getString("item_type");
            }

            ItemStack outputItemStack;

            if (itemType != null && itemType.equals("MMOItems")) {
                String type = output.getConfigurationSection(slot).getString("mmoitem_type");
                String id = output.getConfigurationSection(slot).getString("mmoitem_id");
                outputItemStack = MMOItems.plugin.getItem(Type.get(type), id);
            } else {
                outputItemStack = new ItemStack(Material.valueOf(output.getString(slot)));
                customCraftingRecipe.addOutput(Integer.parseInt(slot), outputItemStack);
            }

            customCraftingRecipe.addOutput(Integer.parseInt(slot), outputItemStack);

        }

        if (ingredient != null) {
            for (String slot : ingredient.getKeys(false)) {

                ItemStack itemStack;
                ConfigurationSection itemConfig = ingredient.getConfigurationSection(slot);
                itemType = null;

                if (itemConfig != null) {
                    itemType = itemConfig.getString("item_type");
                }

                if (itemType != null && itemType.equals("MMOItems")) {

                    String type = ingredient.getConfigurationSection(slot).getString("mmoitem_type");
                    String id = ingredient.getConfigurationSection(slot).getString("mmoitem_id");
                    itemStack = MMOItems.plugin.getItem(Type.get(type), id);

                } else {
                    itemStack = new ItemStack(Material.valueOf(ingredient.getString(slot)));
                }


                customCraftingRecipe.addInput(Integer.parseInt(slot), itemStack);
            }
        }

        return customCraftingRecipe;

    }

    public CraftingMachine loadCraftingMachine(String name) {
        File file = new File(this.plugin.getFolder(Folders.MACHINES), name);
        YamlConfiguration machineConf = YamlConfiguration.loadConfiguration(file);

        return loadCraftingMachine(machineConf, file.getName());
    }
    public CraftingMachine loadCraftingMachine(YamlConfiguration config, String name) {

        String recipe_file = config.getString("recipe_file");
        String activationItemName = config.getString("activation_item");
        String activationItemType = config.getString("activation_item.item_type");
        int size = config.getInt("gui.size");
        Material material = Material.valueOf(config.getString("gui.fill"));

        Structure structure = plugin.getStructureManager().getStructureByUUID(UUID.fromString(config.getString("structure")));

        if (structure == null) {
            return null;
        }

        CraftingMachine craftingMachine = new CraftingMachine(name, recipe_file, size, material, new CustomInventory(this.plugin, size*9), structure);
        File file = new File(this.plugin.getFolder(Folders.RECIPE_CRAFTING), recipe_file);
        YamlConfiguration craftingConfiguration = YamlConfiguration.loadConfiguration(file);
        ItemStack activationItemStack = null;

        // Gestion de l'item d'activation

        if (activationItemName != null) {
            if (activationItemType != null && activationItemType.equals("MMOItems")) {
                String type = config.getString("activation_item.mmoitem_type");
                String id = config.getString("activation_item.mmoitem_id");

                activationItemStack = MMOItems.plugin.getItem(Type.get(type), id);

            } else {
                activationItemStack = new ItemStack(Material.valueOf(activationItemName));
            }
        }

        craftingMachine.setActivationItem(activationItemStack);

        // Création de la machine

        for (String key : config.getConfigurationSection("gui.inventory").getKeys(false)) {

            ConfigurationSection slotConfig = config.getConfigurationSection("gui.inventory." + key);
            String slotType = slotConfig.getString("type");

            if (slotType.equals("INPUT")) {
                craftingMachine.addInput(Integer.parseInt(key), slotConfig.getInt("number"));
            } else if (slotType.equals("OUTPUT")) {
                craftingMachine.addOutput(Integer.parseInt(key), slotConfig.getInt("number"));
            }
        }

        // Création des recettes
        for (String recipe_name : craftingConfiguration.getKeys(false)) {
            ConfigurationSection recipeConfig = craftingConfiguration.getConfigurationSection(recipe_name);
            CustomCraftingRecipe customCraftingRecipe = loadCraftingRecipe(recipeConfig, recipe_name);
            craftingMachine.addCustomRecipe(customCraftingRecipe);
        }

        craftingMachine.regenerateGUI();
        return craftingMachine;
    }

    public boolean referenceMachineBlockExists(Location reference) {
        return this.getPlayerMachinesInMemory().containsKey(reference);
    }

    public void createPlayerMachine(CraftingMachine machine, Rotation rotation, Location reference) {
        PlayerMachine playerMachine = new PlayerMachine(machine, rotation, reference);
        if (!this.getPlayerMachinesInMemory().containsKey(reference)) {
            this.PlayerMachinesInMemory.put(reference, playerMachine);
        }
    }

    public void deletePlayerMachine(PlayerMachine playerMachine) {
        if (this.getPlayerMachinesInMemory().containsKey(playerMachine.getReference())) {
            this.PlayerMachinesInMemory.remove(playerMachine.getReference());
            playerMachine.getReference().getWorld().createExplosion(playerMachine.getReference().add(0, 1, 0), 10);
        }
    }

    public void processExistingMachineClicked(PlayerMachine machine, Player player) {

    }
}
