package fr.kaname.KanaCustomMachine.objects;

import org.bukkit.Material;

public class StructureBlocks {

    private Material material;

    private int x;
    private int y;
    private int z;

    public StructureBlocks(Material material, int x, int y, int z) {
        this.material = material;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Material getMaterial() {
        return material;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "StructureBlocks{" +
                "material=" + material +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
