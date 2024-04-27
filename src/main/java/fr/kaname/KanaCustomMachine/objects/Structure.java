package fr.kaname.KanaCustomMachine.objects;

import fr.kaname.KanaCustomMachine.enums.Rotation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Structure {

    private final Map<Rotation, Map<Integer, Map<Integer, Map<Integer, Material>>>> MapList;
    private String Name;
    private final UUID uuid;

    private final List<Material> materialList = new ArrayList<>();

    public Structure() {

        Map<Integer, Map<Integer, Map<Integer, Material>>> structureMap = new HashMap<>(1);
        Map<Integer, Map<Integer, Map<Integer, Material>>> structureMap90 = new HashMap<>(1);
        Map<Integer, Map<Integer, Map<Integer, Material>>> structureMapMinus90 = new HashMap<>(1);
        Map<Integer, Map<Integer, Map<Integer, Material>>> structureMap180 = new HashMap<>(1);
        this.MapList = new HashMap<>();
        this.uuid = UUID.randomUUID();

        MapList.put(Rotation.DEFAULT, structureMap);
        MapList.put(Rotation.ROTATE90, structureMap90);
        MapList.put(Rotation.ROTATE_MINUS_90, structureMapMinus90);
        MapList.put(Rotation.ROTATE180, structureMap180);
    }

    public Structure(UUID uuid, String name, Map<Rotation, Map<Integer, Map<Integer, Map<Integer, Material>>>> structureData) {
        this.MapList = structureData;
        this.uuid = uuid;
        this.Name = name;

        for (StructureBlocks structureBlocks : this.getAllBlocksInRelativesCoords(Rotation.DEFAULT)) {
            addMaterial(structureBlocks.getMaterial());
        }

    }

    public void addMaterial(Material material) {
        if (!this.materialList.contains(material) && material != Material.AIR) {
            this.materialList.add(material);
        }
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

    private void addHeight(int y, Rotation rotation) {
        if (!this.hasHeight(y, rotation)) {

            Map<Integer, Map<Integer, Material>> map = new HashMap<>(1);

            MapList.get(rotation).put(y, map);
        }
    }

    private void addRow(int x, int y, Rotation rotation) {
        if (!MapList.get(rotation).containsKey(y) || !MapList.get(rotation).get(y).containsKey(x)) {
            Map<Integer, Material> map = new HashMap<>(1);
            MapList.get(rotation).get(y).put(x, map);
        }
    }

    private boolean hasHeight(int y, Rotation rotation) {
        return MapList.get(rotation).containsKey(y);
    }

    private boolean hasRow(int x, int y, Rotation rotation) {
        if (this.hasHeight(y, rotation)) {
            return MapList.get(rotation).get(y).containsKey(x);
        }

        return false;
    }

    private boolean hasBlock(int x, int y, int z, Rotation rotation) {
        return hasHeight(y, rotation) && hasRow(x, y, rotation) && MapList.get(rotation)
                        .get(y)
                        .get(x)
                        .containsKey(z);
    }

    public boolean hasMaterial(Material material) {
        return this.getMaterialList().contains(material);
    }

    public void addBlock(StructureBlocks structureBlocks,  Rotation rotation) {

        if (!this.hasHeight(structureBlocks.getY(), rotation)) {
            this.addHeight(structureBlocks.getY(), rotation);
        }

        if (!this.hasRow(structureBlocks.getX(), structureBlocks.getY(), rotation)) {
            this.addRow(structureBlocks.getX(), structureBlocks.getY(), rotation);
        }

        MapList.get(rotation)
                .get(structureBlocks.getY())
                .get(structureBlocks.getX())
                .put(structureBlocks.getZ(), structureBlocks.getMaterial());

        addMaterial(structureBlocks.getMaterial());

    }

    public StructureBlocks getBlock(Rotation rotation, int x, int y, int z) {

        if (hasBlock(x, y, z, rotation)) {
            Material material = MapList.get(rotation).get(y).get(x).get(z);
            return new StructureBlocks(material, x, y, z);
        }
        return null;
    }

    public Map<Location, Material> generateLocation(Rotation rotation, Location reference) {

        Map<Location, Material> locationMaterialMap = new HashMap<>();

        for (StructureBlocks structureBlocks : this.getAllBlocksInRelativesCoords(rotation)) {
            Location loc = new Location(reference.getWorld(),
                    structureBlocks.getX() + reference.getBlockX(),
                    structureBlocks.getY() + reference.getBlockY(),
                    structureBlocks.getZ() + reference.getBlockZ()
            );

            locationMaterialMap.put(loc, structureBlocks.getMaterial());
        }

        return locationMaterialMap;
    }

    public List<StructureBlocks> getAllBlocksInRelativesCoords(Rotation rotation) {

        List<StructureBlocks> structureBlocksList = new ArrayList<>();

        for (int y : this.MapList.get(rotation).keySet()) {
            for (int x : this.MapList.get(rotation).get(y).keySet()) {
                for (int z : this.MapList.get(rotation).get(y).get(x).keySet()) {
                    structureBlocksList.add(
                            new StructureBlocks(
                                    this.MapList.get(rotation).get(y).get(x).get(z), x, y, z
                            ));
                }
            }
        }

        return structureBlocksList;

    }

    public StructureBlocks getReferenceBlock(Rotation rotation) {
        for (StructureBlocks structureBlocks : getAllBlocksInRelativesCoords(rotation)) {
            if (structureBlocks.getX() == 0 && structureBlocks.getY() == 0 && structureBlocks.getZ() == 0) {
                return structureBlocks;
            }
        }

        return null;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getMaxY(Rotation rotation) {
        int y = this.MapList.get(rotation).keySet().iterator().next();

        for (int i : this.MapList.get(rotation).keySet()) {
            if (y < i){
                y = i;
            }
        }

        return y;

    }

    public int getMinY(Rotation rotation) {
        int y = this.MapList.get(rotation).keySet().iterator().next();

        for (int i : this.MapList.get(rotation).keySet()) {
            if (y > i){
                y = i;
            }
        }

        return y;

    }

    public int getMaxX(Rotation rotation) {

        int y = this.MapList.get(rotation).keySet().iterator().next();
        int x = this.MapList.get(rotation).get(y).keySet().iterator().next();

        for (int i : this.MapList.get(rotation).get(y).keySet()) {
            if (x < i){
                x = i;
            }
        }

        return x;

    }

    public int getMinX(Rotation rotation) {
        int y = this.MapList.get(rotation).keySet().iterator().next();
        int x = this.MapList.get(rotation).get(y).keySet().iterator().next();

        for (int i : this.MapList.get(rotation).get(y).keySet()) {
            if (x > i){
                x = i;
            }
        }

        return x;

    }

    public int getMaxZ(Rotation rotation) {

        int y = this.MapList.get(rotation).keySet().iterator().next();
        int x = this.MapList.get(rotation).get(y).keySet().iterator().next();
        int z = this.MapList.get(rotation).get(y).get(x).keySet().iterator().next();

        for (int i : this.MapList.get(rotation).get(y).get(x).keySet()) {
            if (z < i){
                z = i;
            }
        }

        return z;

    }

    public int getMinZ(Rotation rotation) {

        int y = this.MapList.get(rotation).keySet().iterator().next();
        int x = this.MapList.get(rotation).get(y).keySet().iterator().next();
        int z = this.MapList.get(rotation).get(y).get(x).keySet().iterator().next();

        for (int i : this.MapList.get(rotation).get(y).get(x).keySet()) {
            if (z > i){
                z = i;
            }
        }

        return z;

    }

    @Override
    public String toString() {
        return "Structure{" +
                "Map=" + MapList +
                '}';
    }

    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("Name", getName());
        data.put("Uuid", getUuid());
        data.put("Structure", this.MapList);

        return data;
    }

    public static Structure deserialize(Map<String, Object> args) {

        return new Structure(
                (UUID) args.get("Uuid"),
                (String) args.get("Name"),
                (Map<Rotation, Map<Integer, Map<Integer, Map<Integer, Material>>>>) args.get("Structure")
        );

    }
}
