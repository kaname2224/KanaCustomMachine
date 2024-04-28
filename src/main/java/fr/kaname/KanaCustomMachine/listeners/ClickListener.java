package fr.kaname.KanaCustomMachine.listeners;

import fr.kaname.KanaCustomMachine.KanaCustomMachine;
import fr.kaname.KanaCustomMachine.objects.CraftingMachine;
import fr.kaname.KanaCustomMachine.objects.PlayerMachine;
import fr.kaname.KanaCustomMachine.objects.Structure;
import net.Indyuce.mmoitems.MMOItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.logging.Level;

public class ClickListener implements Listener {

    KanaCustomMachine plugin;

    public ClickListener(KanaCustomMachine itemsplugin) {
        this.plugin = itemsplugin;
    }

    @EventHandler
    public void adminToolListener(PlayerInteractEvent playerInteractEvent) {

        ItemStack item = playerInteractEvent.getPlayer().getInventory().getItemInMainHand();
        Action action = Objects.requireNonNull(playerInteractEvent.getAction());
        Player player = playerInteractEvent.getPlayer();

        if (!item.hasItemMeta() || (item.hasItemMeta() && !item.getItemMeta().hasCustomModelData())) {
            return;
        }

        if (action.isRightClick() && item.getItemMeta().getCustomModelData() == 101001) {
            Location location = playerInteractEvent.getClickedBlock().getLocation();
            plugin.getStructureManager().addPlayerLoadedLocation(player, "location1", location);
        }

    }

    @EventHandler
    public void itemClickOnNewMachineListener(PlayerInteractEvent playerInteractEvent) {

        Action action = Objects.requireNonNull(playerInteractEvent.getAction());
        ItemStack item = playerInteractEvent.getPlayer().getInventory().getItemInMainHand();
        Structure structure = null;
        Player player = playerInteractEvent.getPlayer();

        if (playerInteractEvent.getClickedBlock() == null) {
            return;
        }

        // Action faite par la MainHand (Evite les double clique)

        if (action.isRightClick() && playerInteractEvent.getHand() == EquipmentSlot.HAND) {

            // Vérification de la machine utilisée et appel de la fonction si la machine existe
            for (PlayerMachine playerMachine : this.plugin.getMachineManager().getPlayerMachinesInMemory().values()) {
                if (playerInteractEvent.getClickedBlock() != null) {

                    boolean test = plugin.getStructureDetectionManager().isLocationInStructure(
                            playerMachine.getMachine().getStructure(),
                            playerMachine.getReference(),
                            playerMachine.getRotation(),
                            playerInteractEvent.getClickedBlock().getLocation()
                    );

                    if (test) {

                        this.plugin.getMachineManager().openMachinePlayer(player, playerMachine);
                    }

                }
            }

            // Si la machine existe pas vérification que la machine cliquée est valide, vérification de l'item + enregistrement de la nouvelle machine

            Block block = playerInteractEvent.getClickedBlock();
            CraftingMachine craftingMachine = null;

            for (CraftingMachine machine : this.plugin.getMachineManager().getMachinesInMemory().values()) {
                if (machine.checkActivationItem(item)) {
                    structure = plugin.getStructureDetectionManager().createStructure(block.getType(), block.getLocation());
                    craftingMachine = this.plugin.getMachineManager().loadCraftingMachine(machine.getName());

                    if (!structure.getUuid().equals(machine.getStructure().getUuid())) {
                        return;
                    }
                }
            }

            if (structure != null) {
                if (craftingMachine.hasActivationItem() && !this.plugin.getMachineManager().referenceMachineBlockExists(structure.getReferenceBlockLocation())) {
                    item.setAmount(item.getAmount() - 1);
                    Particle.DUST_COLOR_TRANSITION.builder().colorTransition(Color.RED, Color.WHITE).location(playerInteractEvent.getInteractionPoint()).count(100).offset(0.5, 0.5, 0.5).spawn();
                    Particle.DUST_COLOR_TRANSITION.builder().colorTransition(Color.BLUE, Color.WHITE).location(playerInteractEvent.getInteractionPoint()).count(100).offset(0.8, 0.8, 0.8).spawn();
                    this.plugin.getMachineManager().createPlayerMachine(craftingMachine, structure.getRotation(), structure.getReferenceBlockLocation());
                }

                playerInteractEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void BreakBlockEvent(BlockBreakEvent blockBreakEvent) {

        ItemStack item = blockBreakEvent.getPlayer().getInventory().getItemInMainHand();
        Location location = blockBreakEvent.getBlock().getLocation();

        if (!item.hasItemMeta() || (item.hasItemMeta() && !item.getItemMeta().hasCustomModelData())) {
            return;
        }

        if (item.getItemMeta().getCustomModelData() != 101001) {
            return;
        }

        blockBreakEvent.setCancelled(true);

        plugin.getStructureManager().addPlayerLoadedLocation(blockBreakEvent.getPlayer(), "location2", location);


    }

    @EventHandler
    public void PlaceBlockEvent(BlockPlaceEvent blockPlaceEvent) {
        return;
    }

}