package fr.kaname.itemsplugin;

import fr.kaname.itemsplugin.listeners.ClickListener;
import fr.kaname.itemsplugin.listeners.itemCommandListener;
import fr.kaname.itemsplugin.managers.StructureDetectionManager;
import fr.kaname.itemsplugin.managers.StructureManager;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class Itemsplugin extends JavaPlugin {

    StructureManager structureManager;
    private StructureDetectionManager structureDetectionManager;

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Starting itemPlugins");
        Objects.requireNonNull(this.getCommand("kanaitem")).setExecutor(new itemCommandListener(this));
        this.getServer().getPluginManager().registerEvents(new ClickListener(this), this);
        this.structureManager = new StructureManager(this);
        this.structureDetectionManager = new StructureDetectionManager(this);
        this.saveDefaultConfig();
        this.getStructureManager().loadStructuresInFiles();


    }

    public File loadStructureFolder() {

        File folder = new File(getDataFolder(), "structures");

        if (!folder.exists()) {
            folder.mkdir();
        }

        return folder;
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

    public StructureDetectionManager getStructureDetectionManager() {
        return structureDetectionManager;
    }
}
