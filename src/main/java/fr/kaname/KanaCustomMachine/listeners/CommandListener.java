package fr.kaname.KanaCustomMachine.listeners;

import fr.kaname.KanaCustomMachine.KanaCustomMachine;
import fr.kaname.KanaCustomMachine.enums.Rotation;
import fr.kaname.KanaCustomMachine.objects.Structure;
import fr.kaname.KanaCustomMachine.objects.StructureBlocks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class CommandListener implements CommandExecutor {

    KanaCustomMachine plugin;

    public CommandListener(KanaCustomMachine itemsplugin) {
        plugin = itemsplugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("custommachines") && args.length >= 1) {

            String baseArgument = args[0];

            if (!(commandSender instanceof Player)) {
                return false;
            }

            Player player = (((Player) commandSender).getPlayer());

            if (baseArgument.equals("tool")) {

                ItemStack bene = new ItemStack(Material.SNOWBALL);
                ItemMeta beneMeta = bene.getItemMeta();
                beneMeta.setCustomModelData(101001);

                Component component = Component.text().color(TextColor.fromHexString("ffffff")).append(Component.text("KanaTool")).build();
                beneMeta.displayName(component);

                bene.setItemMeta(beneMeta);

                player.getInventory().addItem(bene);
            }

            if ((baseArgument.equals("copy") || baseArgument.equals("select")) || (baseArgument.equals("save"))) {

                Structure StructureBlocksList = this.plugin.getStructureManager().calculateStructureBlockInASelectionByPlayer(player);

                if (baseArgument.equals("copy")) {
                    this.plugin.getStructureManager().registerPlayerStructure(player, StructureBlocksList);
                }

                if (baseArgument.equals("save")) {
                    this.plugin.getStructureManager().registerPlayerStructure(player, StructureBlocksList, true);
                }

            }

            if (args.length > 1 && baseArgument.equals("view")) {

                Structure structure = this.plugin.getStructureManager().getPlayerStructure(player);
                Rotation rotation = Rotation.valueOf(args[1].toUpperCase());

                if (structure != null) {

                    for (StructureBlocks structureBlocks : structure.getAllBlocksInRelativesCoords(rotation)) {
                        player.sendMessage(Component.text(structureBlocks.toString()));
                    }

                    player.sendMessage(Component.text("Blocks : " + structure.getAllBlocksInRelativesCoords(rotation).size()));
                }


            }

            if (args.length > 1 && baseArgument.equals("paste")) {

                Rotation rotation = Rotation.valueOf(args[1].toUpperCase());

                Structure structureBlocks = this.plugin.getStructureManager().getPlayerStructure(player);
                Location reference = this.plugin.getStructureManager().getPlayerLoadedLocation(player).get("location1");

                if (structureBlocks != null && reference != null) {

                    Map<Location, Material> structureLocations = this.plugin.getStructureManager().getPlayerStructure(player)
                            .generateLocation(rotation, plugin.getStructureManager().getPlayerLoadedLocation(player).get("location1"));

                    for (Location loc : structureLocations.keySet()) {

                        player.sendMessage(Component.text(loc.toString()));
                        player.getWorld().getBlockAt(loc).setType(structureLocations.get(loc));

                    }

                }

            }

            if (baseArgument.equals("load") && args.length > 1) {

                String file = args[1] + ".dat";
                Structure structure = plugin.getStructureManager().loadStructureFile(file);

                this.plugin.getStructureManager().registerPlayerStructure(player, structure);



            }

        }

        return false;
    }
}
