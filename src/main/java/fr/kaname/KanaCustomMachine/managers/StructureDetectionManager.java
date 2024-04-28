package fr.kaname.KanaCustomMachine.managers;

import fr.kaname.KanaCustomMachine.KanaCustomMachine;
import fr.kaname.KanaCustomMachine.enums.Rotation;
import fr.kaname.KanaCustomMachine.objects.Structure;
import fr.kaname.KanaCustomMachine.objects.StructureBlocks;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class StructureDetectionManager {

    private final KanaCustomMachine plugin;

    public StructureDetectionManager(KanaCustomMachine plugin) {
        this.plugin = plugin;
    }

    public List<Location> getPotentialReferenceBlockLocation(Location blockPlaced, Structure structure, Rotation rotation) {

        List<Location> locationList = new ArrayList<>();

        for (StructureBlocks structureBlocks : structure.getAllBlocksInRelativesCoords(rotation)) {
            Location location = new Location(
                    blockPlaced.getWorld(),
                    structureBlocks.getX() + blockPlaced.getBlockX(),
                    structureBlocks.getY() + blockPlaced.getBlockY(),
                    structureBlocks.getZ() + blockPlaced.getBlockZ()
            );

            Location locationMinus = new Location(
                    blockPlaced.getWorld(),
                    blockPlaced.getBlockX() - structureBlocks.getX(),
                    blockPlaced.getBlockY() + structureBlocks.getY(),
                    blockPlaced.getBlockZ() - structureBlocks.getZ()
            );

            Location locationUpsideDown = new Location(
                    blockPlaced.getWorld(),
                    structureBlocks.getX() + blockPlaced.getBlockX(),
                    blockPlaced.getBlockY() - structureBlocks.getY(),
                    structureBlocks.getZ() + blockPlaced.getBlockZ()
            );

            Location locationUpsideDownMinus = new Location(
                    blockPlaced.getWorld(),
                    blockPlaced.getBlockX() - structureBlocks.getX(),
                    blockPlaced.getBlockY() - structureBlocks.getY(),
                    blockPlaced.getBlockZ() - structureBlocks.getZ()
            );


            Material blockType = blockPlaced.getWorld().getBlockAt(location).getType();
            Material blockTypeUpsideDown = blockPlaced.getWorld().getBlockAt(locationUpsideDown).getType();
            Material blockTypeMinusUpsideDown = blockPlaced.getWorld().getBlockAt(locationUpsideDownMinus).getType();
            Material blockTypeMinus = blockPlaced.getWorld().getBlockAt(locationMinus).getType();

            if (blockType == structure.getReferenceBlock(rotation).getMaterial()) {
                locationList.add(location);
            }

            if (blockTypeUpsideDown == structure.getReferenceBlock(rotation).getMaterial()) {
                if (!locationList.contains(locationUpsideDown)) {
                    locationList.add(locationUpsideDown);
                }
            }

            if (blockTypeMinusUpsideDown == structure.getReferenceBlock(rotation).getMaterial()) {
                if (!locationList.contains(locationUpsideDownMinus)) {
                    locationList.add(locationUpsideDownMinus);
                }
            }

            if (blockTypeMinus == structure.getReferenceBlock(rotation).getMaterial()) {
                if (!locationList.contains(locationMinus)) {
                    locationList.add(locationMinus);
                }
            }
        }

        return locationList;

    }

    public void getPlaceBlockEventDebugInfo(Player player, Material material, Structure structure, Rotation rotation) {

        player.sendMessage(Component.text(rotation.toString()));
        player.sendMessage(Component.text(structure.getReferenceBlock(rotation).toString()));

        player.sendMessage(Component.text(
                "MAX : " +
                        structure.getMaxX(rotation) + " / " +
                        structure.getMaxY(rotation) + " / " +
                        structure.getMaxZ(rotation)
        ));

        player.sendMessage(Component.text(
                "MIN : " +
                        structure.getMinX(rotation) + " / " +
                        structure.getMinY(rotation) + " / " +
                        structure.getMinZ(rotation)
        ));

    }

    public boolean isLocationInStructure(Structure structure, Location reference, Rotation rotation, Location location) {
        return (reference.getBlockX() >= location.getBlockX() && location.getBlockX() >= reference.getBlockX() + structure.getMinX(rotation)) &&
                (reference.getBlockY() <= location.getBlockY() && location.getBlockY() <= reference.getBlockY() + structure.getMaxY(rotation)) &&
                        (reference.getBlockZ() >= location.getBlockZ() && location.getBlockZ() >= reference.getBlockZ() + structure.getMinZ(rotation));
    }

    public boolean checkStructureIntegrity(Structure structure, Location referenceBlockLocation, Rotation rotation) {
        Map<Location, Material> structureLocation = structure.generateLocation(rotation, referenceBlockLocation);
        for (Location realLocation : structureLocation.keySet()) {
            if (referenceBlockLocation.getWorld().getBlockAt(realLocation).getType() != structureLocation.get(realLocation)) {
                return false;
            }
        }

        return true;
    }

    public Structure createStructure(Material material, Location blockPlacedLocation) {

        Location structureReferenceBlockLocation = null;
        Rotation structureRotation = null;

        for (Structure structure : this.plugin.getStructureManager().getStructuresByMaterial(material)) {
            boolean isStructureValid = false;
            for (Rotation rotation : Rotation.values()) {

                List<Location> locationList = this.plugin.getStructureDetectionManager().getPotentialReferenceBlockLocation(
                        blockPlacedLocation,
                        structure,
                        rotation
                );

                for (Location referenceBlockLocation : locationList) {
                    isStructureValid = checkStructureIntegrity(structure, referenceBlockLocation, rotation);
                    if (isStructureValid) {
                        structureReferenceBlockLocation = referenceBlockLocation;
                        break;
                    }
                }
                if (isStructureValid) {
                    structureRotation = rotation;
                    break;
                }
            }
            if (isStructureValid) {
                structure.setRotation(structureRotation);
                structure.setReferenceBlockLocation(structureReferenceBlockLocation);

                return structure;
            }

        }

        return null;

    }
}
