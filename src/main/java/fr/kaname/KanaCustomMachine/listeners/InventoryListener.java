package fr.kaname.KanaCustomMachine.listeners;

import fr.kaname.KanaCustomMachine.KanaCustomMachine;
import fr.kaname.KanaCustomMachine.objects.CraftingMachine;
import fr.kaname.KanaCustomMachine.objects.CustomInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
            CraftingMachine machine = this.plugin.getMachineManager().getOpenMachine(player);
            int clickedSlot = event.getSlot();

            if (!machine.getInputSlot().containsKey(clickedSlot) && event.getClickedInventory().getHolder() instanceof CustomInventory) {
                event.setCancelled(true);
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                this.plugin.getMachineManager().processCrafting(machine, event.getInventory());
            }, 2);

            if (machine.getOutputSlot().containsValue(clickedSlot) && event.getClickedInventory().getHolder() instanceof CustomInventory) {

                ItemStack resultItemStack = inventory.getItem(clickedSlot);

                if (resultItemStack != null) {
                    player.getInventory().addItem(resultItemStack);
                    inventory.close();
                }
            }

        }


    }

}
