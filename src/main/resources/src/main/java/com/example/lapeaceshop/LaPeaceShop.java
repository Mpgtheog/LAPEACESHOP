package com.example.lapeaceshop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class LaPeaceShop extends JavaPlugin implements Listener, CommandExecutor {

    private final String SHOP_TITLE = ChatColor.BLUE + "" + ChatColor.BOLD + "LA PEACE SHOP";

    @Override
    public void onEnable() {
        getCommand("shop").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("LA PEACE Shop enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can open the shop!");
            return true;
        }

        openShopGUI(player);
        return true;
    }

    private void openShopGUI(Player player) {
        Inventory shop = Bukkit.createInventory(null, 27, SHOP_TITLE);

        addShopItem(shop, 11, Material.DIAMOND_SWORD, ChatColor.GOLD + "Diamond Sword", 10, 1);
        addShopItem(shop, 13, Material.GOLDEN_APPLE, ChatColor.YELLOW + "Golden Apple", 5, 2);
        addShopItem(shop, 15, Material.ENDER_PEARL, ChatColor.LIGHT_PURPLE + "Ender Pearls", 8, 4);

        player.openInventory(shop);
    }

    private void addShopItem(Inventory inv, int slot, Material material, String name, int costInLapis, int itemAmount) {
        ItemStack item = new ItemStack(material, itemAmount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Price: " + ChatColor.BLUE + costInLapis + " Lapis Lazuli");
            lore.add(ChatColor.GREEN + "Click to Purchase!");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        inv.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(SHOP_TITLE)) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        int cost = 0;
        ItemStack rewardItem = null;

        switch (event.getSlot()) {
            case 11 -> {
                cost = 10;
                rewardItem = new ItemStack(Material.DIAMOND_SWORD, 1);
            }
            case 13 -> {
                cost = 5;
                rewardItem = new ItemStack(Material.GOLDEN_APPLE, 2);
            }
            case 15 -> {
                cost = 8;
                rewardItem = new ItemStack(Material.ENDER_PEARL, 4);
            }
            default -> {
                return;
            }
        }

        if (hasEnoughLapis(player, cost)) {
            removeLapis(player, cost);
            player.getInventory().addItem(rewardItem);
            player.sendMessage(ChatColor.GREEN + "[LA PEACE] Purchase successful!");
        } else {
            player.sendMessage(ChatColor.RED + "[LA PEACE] You don't have enough Lapis Lazuli! Required: " + cost);
        }
    }

    private boolean hasEnoughLapis(Player player, int requiredAmount) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.LAPIS_LAZULI) {
                count += item.getAmount();
            }
        }
        return count >= requiredAmount;
    }

    private void removeLapis(Player player, int amountToRemove) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.LAPIS_LAZULI) {
                int amount = item.getAmount();
                if (amount <= amountToRemove) {
                    amountToRemove -= amount;
                    item.setAmount(0);
                } else {
                    item.setAmount(amount - amountToRemove);
                    amountToRemove = 0;
                }
                if (amountToRemove <= 0) break;
            }
        }
    }
}
