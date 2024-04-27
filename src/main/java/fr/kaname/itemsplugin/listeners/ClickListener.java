package fr.kaname.itemsplugin.listeners;

import fr.kaname.itemsplugin.Itemsplugin;
import net.Indyuce.mmoitems.MMOItems;
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

public class ClickListener implements Listener {

    Itemsplugin plugin;

    public ClickListener(Itemsplugin itemsplugin) {
        this.plugin = itemsplugin;
    }

    @EventHandler
    public void rightClickListener(PlayerInteractEvent playerInteractEvent) {

        Action action = Objects.requireNonNull(playerInteractEvent.getAction());
        ItemStack item = playerInteractEvent.getPlayer().getInventory().getItemInMainHand();
        Player player = playerInteractEvent.getPlayer();

        ItemStack MMOItems_DragonScute = MMOItems.plugin.getItem(MMOItems.plugin.getTypes().get("MATERIAL"), "DRAGON_SCUTE");

        if (playerInteractEvent.getClickedBlock() == null) {
            return;
        }

        if (action.isRightClick() && item.getItemMeta().equals(MMOItems_DragonScute.getItemMeta()) && playerInteractEvent.getHand() == EquipmentSlot.HAND) {
            Block block = playerInteractEvent.getClickedBlock();
            boolean isStructureValid = plugin.getStructureDetectionManager().createMachine(player, block.getType(), block.getLocation());

            if (isStructureValid) {
                item.setAmount(item.getAmount() - 1);
                Particle.DUST_COLOR_TRANSITION.builder().colorTransition(Color.RED, Color.WHITE).location(playerInteractEvent.getInteractionPoint()).count(100).offset(0.5, 0.5, 0.5).spawn();
                Particle.DUST_COLOR_TRANSITION.builder().colorTransition(Color.BLUE, Color.WHITE).location(playerInteractEvent.getInteractionPoint()).count(100).offset(0.8, 0.8, 0.8).spawn();

            }

            playerInteractEvent.setCancelled(true);
            return;
        }

        if (!item.hasItemMeta() || (item.hasItemMeta() && !item.getItemMeta().hasCustomModelData())) {
            return;
        }


        if (action.isRightClick() && item.getItemMeta().getCustomModelData() == 101001) {
            Location location = playerInteractEvent.getClickedBlock().getLocation();
            plugin.getStructureManager().addPlayerLoadedLocation(player, "location1", location);
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