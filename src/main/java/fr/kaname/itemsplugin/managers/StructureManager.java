package fr.kaname.itemsplugin.managers;

import fr.kaname.itemsplugin.Itemsplugin;
import fr.kaname.itemsplugin.objects.Rotation;
import fr.kaname.itemsplugin.objects.Structure;
import fr.kaname.itemsplugin.objects.StructureBlocks;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class StructureManager {

    private final Itemsplugin plugin;

    private final Map<Player, Map<String, Location>> PlayerMapLocation = new HashMap<>();
    private final Map<Player, Structure> PlayerStructureInMemory = new HashMap<>();
    private final List<Structure> structureInMemory = new ArrayList<>();

    public StructureManager(Itemsplugin plugin) {
        this.plugin = plugin;
    }

    public void loadStructuresInFiles() {

        File[] fileList = this.plugin.loadStructureFolder().listFiles();

        for (File file : fileList) {
            Structure structure = loadStructureFile(file.getName());
            this.plugin.getLogger().log(Level.INFO, "Registered Structure : " + structure.getUuid().toString());
            structureInMemory.add(structure);

        }


    }

    public List<Structure> getStructureInMemory() {
        return structureInMemory;
    }

    public Map<String, Location> getPlayerLoadedLocation(Player player) {
        return PlayerMapLocation.get(player);
    }

    public Structure getPlayerRegisteredStructure(Player player) {
        return PlayerStructureInMemory.get(player);
    }

    public void addPlayerLoadedLocation(Player player, String locationName, Location location) {

        if (PlayerMapLocation.containsKey(player)) {
            Map<String, Location> map = PlayerMapLocation.get(player);
            map.put(locationName, location);

        } else {
            Map<String, Location> map = new HashMap<>();
            map.put(locationName, location);
            PlayerMapLocation.put(player, map);
        }


        player.sendMessage(PlayerMapLocation.toString());
    }

    public Structure calculateStructureBlockInASelectionByPlayer(Player player) {

        Map<String, Location> playerLocs = this.getPlayerLoadedLocation(player);
        Location location1 = playerLocs.get("location1");
        Location location2 = playerLocs.get("location2");

        // Extraire les coordonnées de chaque point
        double x1 = location1.x(), y1 = location1.y(), z1 = location1.z();
        double x2 = location2.x(), y2 = location2.y(), z2 = location2.z();

        // Déterminer les bornes minimales et maximales de chaque dimension
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minY = Math.min(y1, y2);
        double maxY = Math.max(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxZ = Math.max(z1, z2);

        // Initialiser une liste pour stocker les coordonnées des blocs
        List<Location> coordonneesBlocs = new ArrayList<>();

        // Itérer à travers chaque dimension et générer les coordonnées
        for (double x = minX; x <= maxX; x++) {
            for (double y = minY; y <= maxY; y++) {
                for (double z = minZ; z <= maxZ; z++) {
                    coordonneesBlocs.add(new Location(player.getWorld(), x, y, z));
                }
            }
        }

        if (coordonneesBlocs.size() > 1728) {
            return null;
        }

        assert player != null;

        Structure structure = new Structure();
        Location reference = new Location(player.getWorld(), maxX, minY, maxZ);

        for (Location loc : coordonneesBlocs) {
            Block block = player.getWorld().getBlockAt(loc);

            structure.addBlock(new StructureBlocks(
                    block.getType(),
                    block.getX() - reference.getBlockX(),
                    block.getY() - reference.getBlockY(),
                    block.getZ() - reference.getBlockZ()),
                    Rotation.DEFAULT
            );

            structure.addBlock(new StructureBlocks(
                    block.getType(),
                    - (block.getZ() - reference.getBlockZ()),
                    block.getY() - reference.getBlockY(),
                    block.getX() - reference.getBlockX()),
                    Rotation.ROTATE90
            );

            structure.addBlock(new StructureBlocks(
                    block.getType(),
                    (block.getZ() - reference.getBlockZ()),
                    block.getY() - reference.getBlockY(),
                    -(block.getX() - reference.getBlockX())),
                    Rotation.ROTATE_MINUS_90
            );

            structure.addBlock(new StructureBlocks(
                    block.getType(),
                    -(block.getX() - reference.getBlockX()),
                    block.getY() - reference.getBlockY(),
                    -(block.getZ() - reference.getBlockZ())),

                    Rotation.ROTATE180
            );


        }

        return structure;

    }

    public Structure getPlayerStructure(Player player) {
        if (this.PlayerStructureInMemory.containsKey(player)) {
            return this.PlayerStructureInMemory.get(player);
        }

        return null;
    }

    public void registerPlayerStructure(Player player, Structure structure) {

        registerPlayerStructure(player, structure, false);
    }

    public List<Structure> getStructuresByMaterial(Material material) {
        List<Structure> structureList = new ArrayList<>();
        for (Structure structure : this.getStructureInMemory()) {
            if (structure.hasMaterial(material)) {
                structureList.add(structure);
            }
        }

        return structureList;
    }

    public void registerPlayerStructure(Player player, Structure structure, boolean saveInFile) {

        this.PlayerStructureInMemory.put(player, structure);

        if (saveInFile) {

            player.sendMessage(Component.text("Saving..." + structure.getUuid()));
            String fileName = structure.getUuid() + ".dat";

            try {

                this.plugin.getLogger().log(Level.INFO, this.plugin.getDataFolder().getPath());

                File file = new File(this.plugin.getDataFolder().getPath() + "/structures", fileName);

                if(!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream  fos = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fos);
                out.writeObject(structure.serialize());
                out.close();
                fos.close();

                this.structureInMemory.add(structure);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public Structure loadStructureFile(String fileName) {

        Structure structure = null;

        try {

            File file = new File(this.plugin.getDataFolder().getPath() + "/structures", fileName);

            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            Map<String, Object> data =  (Map<String, Object>) ois.readObject();
            structure = Structure.deserialize(data);

            ois.close();
            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return structure;
    }

}
