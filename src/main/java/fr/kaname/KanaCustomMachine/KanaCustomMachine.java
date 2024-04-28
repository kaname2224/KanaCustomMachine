package fr.kaname.KanaCustomMachine;

import fr.kaname.KanaCustomMachine.enums.Folders;
import fr.kaname.KanaCustomMachine.listeners.ClickListener;
import fr.kaname.KanaCustomMachine.listeners.CommandListener;
import fr.kaname.KanaCustomMachine.listeners.InventoryListener;
import fr.kaname.KanaCustomMachine.managers.MachineManager;
import fr.kaname.KanaCustomMachine.managers.StructureDetectionManager;
import fr.kaname.KanaCustomMachine.managers.StructureManager;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public final class KanaCustomMachine extends JavaPlugin {

    private StructureManager structureManager;
    private StructureDetectionManager structureDetectionManager;

    private MachineManager machineManager;

    private Map<Folders, File> folders;

    public MachineManager getMachineManager() {
        return machineManager;
    }

    @Override
    public void onEnable() {

        Objects.requireNonNull(this.getCommand("custommachines")).setExecutor(new CommandListener(this));

        this.getServer().getPluginManager().registerEvents(new ClickListener(this), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

        this.structureManager = new StructureManager(this);
        this.structureDetectionManager = new StructureDetectionManager(this);
        this.machineManager = new MachineManager(this);

        this.folders = new HashMap<>();
        this.saveDefaultConfig();
        this.loadFolders();
        this.getStructureManager().loadStructuresInFiles();
        this.getMachineManager().loadMachinesInFiles();


    }

    public File getFolder(Folders type) {
        if (this.folders.containsKey(type)) {
            return folders.get(type);
        }

        return null;
    }

    public void loadFolders() {

        // Primary folder
        folders.put(Folders.STRUCTURE, new File(getDataFolder(), "structures"));
        folders.put(Folders.MACHINES, new File(getDataFolder(), "machines"));
        folders.put(Folders.RECIPE, new File(getDataFolder(), "recipes"));

        // Subfolder
        folders.put(Folders.RECIPE_CRAFTING, new File(getDataFolder() + "/recipes", "crafting"));

        for (File folder : folders.values()) {
            if (!folder.exists()) {
                this.getLogger().log(Level.INFO, "Creating folder : " + folder.getPath());
                folder.mkdir();
            }
        }
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

    public StructureDetectionManager getStructureDetectionManager() {
        return structureDetectionManager;
    }


}
