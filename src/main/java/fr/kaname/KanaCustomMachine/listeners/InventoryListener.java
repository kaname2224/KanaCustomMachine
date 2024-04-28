package fr.kaname.KanaCustomMachine.listeners;

import fr.kaname.KanaCustomMachine.KanaCustomMachine;
import fr.kaname.KanaCustomMachine.objects.CraftingMachine;
import fr.kaname.KanaCustomMachine.objects.CustomInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.logging.Level;

public class InventoryListener implements Listener {

    private KanaCustomMachine plugin;

    public InventoryListener(KanaCustomMachine plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void InventoryListener(InventoryClickEvent event) {

        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof CustomInventory) || event.getClickedInventory() == null) {
            return;
        }


        if (event.getWhoClicked() instanceof Player) {
            Player player = (((Player) event.getWhoClicked()).getPlayer());
            CraftingMachine machine = this.plugin.getMachineManager().getPlayerMachineOpen(player).getMachine();
            int clickedSlot = event.getSlot();

            if (!machine.getInputSlot().containsKey(clickedSlot) && event.getClickedInventory().getHolder() instanceof CustomInventory) {
                event.setCancelled(true);
            }

            if (inventory.getSize() > clickedSlot && inventory.getItem(clickedSlot) == null) {
                event.setCancelled(true);
                return;
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> this.plugin.getMachineManager().processCrafting(machine, event.getInventory()), 5);


            if (machine.getOutputSlot().containsValue(clickedSlot)) {

                if (player.getItemOnCursor().getType().equals(Material.AIR)) {
                    player.setItemOnCursor(inventory.getItem(clickedSlot));
                } else if ((player.getItemOnCursor().hasItemMeta() && inventory.getItem(clickedSlot).hasItemMeta())) {
                    if (player.getItemOnCursor().getItemMeta().equals(inventory.getItem(clickedSlot).getItemMeta())) {
                        player.getItemOnCursor().add(inventory.getItem(clickedSlot).getAmount());
                    } else {
                        return;
                    }
                } else {
                    if (player.getItemOnCursor().getType().equals(inventory.getItem(clickedSlot).getType())) {
                        player.getItemOnCursor().add(inventory.getItem(clickedSlot).getAmount());
                    } else {
                        return;
                    }
                }


            }

            if (machine.getOutputSlot().containsValue(clickedSlot) && event.getClickedInventory().getHolder() instanceof CustomInventory) {

                ItemStack resultItemStack = inventory.getItem(clickedSlot);

                if (resultItemStack != null) {
                    for (int inputSlot : machine.getInputSlot().keySet()) {
                        ItemStack inputItemStack = inventory.getContents()[inputSlot];
                        if (inputItemStack != null) {
                            inputItemStack.add(-1);
                        }
                    }
                }
            }

        }


    }

    @EventHandler
    public void InventoryCloseListener(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof CustomInventory)) {
            return;
        }

        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            this.plugin.getMachineManager().closeAndSaveMachinePlayer(player);

        }

    }

}
