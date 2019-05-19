import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class Event1vs1 implements Listener {
    private CNMCServer plugin;
    private int matchID;
    private World worldR;
    private World matchWorld;
    private PlayerCompetive player1;
    private PlayerCompetive player2;

    private Event1vs1 toThis = this;

    private boolean isInMatch = false;

    public Event1vs1(CNMCServer plugin, int matchID, World worldR, PlayerCompetive player1, PlayerCompetive player2) {
        this.plugin = plugin;
        this.matchID = matchID;
        this.worldR = worldR;
        this.player1 = player1;
        this.player2 = player2;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        initWorld();
        initPlayers();
        initInventory();
        startMatch();
    }

    private void notifyToCompetiveManager() {
        CompetiveManager competiveManager = this.plugin.getCompetiveManager();
        competiveManager.getPlayerMap().get(player2.getUuid()).getWaitingList().remove(player1.getUuid());
        competiveManager.getPlayerMap().get(player1.getUuid()).setInMatch(true);
        competiveManager.getPlayerMap().get(player2.getUuid()).setInMatch(true);
    }

    private void initWorld() {
        File copiedWorldFolder;
        try {
            worldR.save();
            copiedWorldFolder = new File(worldR.getWorldFolder().getAbsoluteFile() + "-" + matchID);
            if(copiedWorldFolder.exists())
                FileUtils.forceDelete(copiedWorldFolder);
            copiedWorldFolder.mkdir();
            FileUtils.copyDirectory(worldR.getWorldFolder(), copiedWorldFolder);
            new File(copiedWorldFolder, "uid.dat").delete();
            new File(copiedWorldFolder, "session.lock").delete();
            matchWorld = new WorldCreator(copiedWorldFolder.getName()).createWorld();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        matchWorld.setGameRuleValue("KeepInventory", "true");

    }

    private void initPlayers() {
        Bukkit.getPlayer(player1.getUuid()).setHealth(20);
        Bukkit.getPlayer(player2.getUuid()).setHealth(20);
        Bukkit.getPlayer(player1.getUuid()).setSaturation(20);
        Bukkit.getPlayer(player2.getUuid()).setSaturation(20);
        Bukkit.getPlayer(player1.getUuid()).setGameMode(GameMode.SURVIVAL);
        Bukkit.getPlayer(player2.getUuid()).setGameMode(GameMode.SURVIVAL);
        for (PotionEffect effect : Bukkit.getPlayer(player1.getUuid()).getActivePotionEffects()) {
            Bukkit.getPlayer(player1.getUuid()).removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : Bukkit.getPlayer(player2.getUuid()).getActivePotionEffects()) {
            Bukkit.getPlayer(player2.getUuid()).removePotionEffect(effect.getType());
        }
        teleportPlayers();
    }

    private void teleportPlayers() {
        Location loc1 = matchWorld.getSpawnLocation().clone();
        loc1.setX(42);
        loc1.setY(122);
        loc1.setZ(0);
        System.out.println(loc1);
        Location loc2 = loc1.clone();
        loc2.setX(-41);
        Bukkit.getPlayer(player1.getUuid()).teleport(loc1);
        Bukkit.getPlayer(player2.getUuid()).teleport(loc2);
    }

    private void initInventory() {
        ItemStack itemCookedChicken = new ItemStack(Material.COOKED_CHICKEN, 64);
        ItemStack itemSword = new ItemStack(Material.DIAMOND_SWORD);
        itemSword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        ItemStack itemHelmet = new ItemStack(Material.DIAMOND_HELMET);
        itemHelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        ItemStack itemChestPlate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        itemChestPlate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        ItemStack itemLeggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        itemLeggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        ItemStack itemBoots = new ItemStack(Material.DIAMOND_BOOTS);
        itemBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        ItemStack itemEnderPearl = new ItemStack(Material.ENDER_PEARL, 16);
        ItemStack itemSpeedPotion = new Potion(PotionType.SPEED, 1, false).toItemStack(1);
        ItemStack itemFireResistancePotion = new Potion(PotionType.FIRE_RESISTANCE, 1, false).toItemStack(1);
        ItemStack itemHealPotion = new Potion(PotionType.INSTANT_HEAL, 2, true).toItemStack(1);
        PlayerInventory inventory = Bukkit.getPlayer(player1.getUuid()).getInventory();
        for (int j = 0; j < 2; j++) {
            inventory.setItem(0, itemSword);
            inventory.setItem(1, itemEnderPearl);
            inventory.setItem(2, itemSpeedPotion);
            inventory.setItem(3, itemFireResistancePotion);
            for (int i = 4; i <= 7; i++) {
                inventory.setItem(i, itemHealPotion);
            }
            inventory.setItem(8, itemCookedChicken);
            for (int i = 9; i <= 35; i++) {
                inventory.setItem(i, itemHealPotion);
            }
            inventory.setItem(17, itemHealPotion);
            inventory.setItem(26, itemHealPotion);
            inventory.setItem(35, itemHealPotion);
            inventory.setBoots(itemBoots);
            inventory.setLeggings(itemLeggings);
            inventory.setChestplate(itemChestPlate);
            inventory.setHelmet(itemHelmet);
            inventory = Bukkit.getPlayer(player2.getUuid()).getInventory();
        }
    }

    private void startMatch() {

        new BukkitRunnable() {
            int counter = 10;
            @Override
            public void run() {
                if(counter-- == 0) {
                    isInMatch = true;
                    this.cancel();
                    return;
                }
                Bukkit.getPlayer(player1.getUuid()).sendMessage("Starting in ".concat(String.valueOf(counter)));
                Bukkit.getPlayer(player2.getUuid()).sendMessage("Starting in ".concat(String.valueOf(counter)));
                Bukkit.getPlayer(player1.getUuid()).playSound(Bukkit.getPlayer(player1.getUuid()).getLocation(), Sound.NOTE_PIANO, 10f, 16f);
                Bukkit.getPlayer(player2.getUuid()).playSound(Bukkit.getPlayer(player2.getUuid()).getLocation(), Sound.NOTE_PIANO, 10f, 16f);

            }
        }.runTaskTimer(plugin, 0, 14);
    }

    private void endMatch(Player player) {
        isInMatch = false;

        Bukkit.getPlayer(player1.getUuid()).playSound(Bukkit.getPlayer(player1.getUuid()).getLocation(), Sound.EXPLODE, 30f, 16f);
        Bukkit.getPlayer(player2.getUuid()).playSound(Bukkit.getPlayer(player2.getUuid()).getLocation(), Sound.EXPLODE, 30f, 16f);

        Bukkit.getPlayer(player1.getUuid()).sendMessage(ChatColor.YELLOW + "This match was ended. The Loser was " + ChatColor.RED + player.getDisplayName());
        Bukkit.getPlayer(player2.getUuid()).sendMessage(ChatColor.YELLOW + "This match was ended. The Loser was " + ChatColor.RED + player.getDisplayName());
        Bukkit.getPlayer(player1.getUuid()).getInventory().clear();
        Bukkit.getPlayer(player1.getUuid()).getInventory().setBoots(null);
        Bukkit.getPlayer(player1.getUuid()).getInventory().setLeggings(null);
        Bukkit.getPlayer(player1.getUuid()).getInventory().setChestplate(null);
        Bukkit.getPlayer(player1.getUuid()).getInventory().setHelmet(null);
        Bukkit.getPlayer(player2.getUuid()).getInventory().clear();
        Bukkit.getPlayer(player2.getUuid()).getInventory().setBoots(null);
        Bukkit.getPlayer(player2.getUuid()).getInventory().setLeggings(null);
        Bukkit.getPlayer(player2.getUuid()).getInventory().setChestplate(null);
        Bukkit.getPlayer(player2.getUuid()).getInventory().setHelmet(null);
        Bukkit.getPlayer(player1.getUuid()).sendMessage("Teleport to the previous location in 5s.");
        Bukkit.getPlayer(player2.getUuid()).sendMessage("Teleport to the previous location in 5s.");

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPlayer(player1.getUuid()).teleport(player1.getPrevLocation());
                Bukkit.getPlayer(player2.getUuid()).teleport(player2.getPrevLocation());
                Bukkit.getPlayer(player1.getUuid()).getInventory().clear();
                Bukkit.getPlayer(player2.getUuid()).getInventory().clear();
                Bukkit.getPlayer(player1.getUuid()).getInventory().setBoots(null);
                Bukkit.getPlayer(player1.getUuid()).getInventory().setLeggings(null);
                Bukkit.getPlayer(player1.getUuid()).getInventory().setChestplate(null);
                Bukkit.getPlayer(player1.getUuid()).getInventory().setHelmet(null);
                Bukkit.getPlayer(player2.getUuid()).getInventory().setBoots(null);
                Bukkit.getPlayer(player2.getUuid()).getInventory().setLeggings(null);
                Bukkit.getPlayer(player2.getUuid()).getInventory().setChestplate(null);
                Bukkit.getPlayer(player2.getUuid()).getInventory().setHelmet(null);

                Bukkit.getPlayer(player1.getUuid()).setHealth(20);
                Bukkit.getPlayer(player2.getUuid()).setHealth(20);
                Bukkit.getPlayer(player1.getUuid()).setSaturation(20);
                Bukkit.getPlayer(player2.getUuid()).setSaturation(20);
                Bukkit.getPlayer(player1.getUuid()).setGameMode(player1.getPrevGameMode());
                Bukkit.getPlayer(player2.getUuid()).setGameMode(player2.getPrevGameMode());

                for (PotionEffect effect : Bukkit.getPlayer(player1.getUuid()).getActivePotionEffects()) {
                    Bukkit.getPlayer(player1.getUuid()).removePotionEffect(effect.getType());
                }
                for (PotionEffect effect : Bukkit.getPlayer(player2.getUuid()).getActivePotionEffects()) {
                    Bukkit.getPlayer(player2.getUuid()).removePotionEffect(effect.getType());
                }

                safeDelete();
            }
        }.runTaskLater(plugin, 100);
    }

    private void safeDelete() {
        HandlerList.unregisterAll(toThis);
        removeWorld();
    }

    private void removeWorld() {
        Bukkit.unloadWorld(matchWorld.getName(), false);
        try {
            FileUtils.forceDelete(matchWorld.getWorldFolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void interapt() {
        Bukkit.getPlayer(player1.getUuid()).teleport(player1.getPrevLocation());
        Bukkit.getPlayer(player2.getUuid()).teleport(player2.getPrevLocation());
        Bukkit.getPlayer(player1.getUuid()).getInventory().clear();
        Bukkit.getPlayer(player2.getUuid()).getInventory().clear();
        Bukkit.getPlayer(player1.getUuid()).getInventory().setBoots(null);
        Bukkit.getPlayer(player1.getUuid()).getInventory().setLeggings(null);
        Bukkit.getPlayer(player1.getUuid()).getInventory().setChestplate(null);
        Bukkit.getPlayer(player1.getUuid()).getInventory().setHelmet(null);
        Bukkit.getPlayer(player2.getUuid()).getInventory().setBoots(null);
        Bukkit.getPlayer(player2.getUuid()).getInventory().setLeggings(null);
        Bukkit.getPlayer(player2.getUuid()).getInventory().setChestplate(null);
        Bukkit.getPlayer(player2.getUuid()).getInventory().setHelmet(null);

        Bukkit.getPlayer(player1.getUuid()).setHealth(20);
        Bukkit.getPlayer(player2.getUuid()).setHealth(20);
        Bukkit.getPlayer(player1.getUuid()).setSaturation(20);
        Bukkit.getPlayer(player2.getUuid()).setSaturation(20);
        Bukkit.getPlayer(player1.getUuid()).setGameMode(player1.getPrevGameMode());
        Bukkit.getPlayer(player2.getUuid()).setGameMode(player2.getPrevGameMode());

        for (PotionEffect effect : Bukkit.getPlayer(player1.getUuid()).getActivePotionEffects()) {
            Bukkit.getPlayer(player1.getUuid()).removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : Bukkit.getPlayer(player2.getUuid()).getActivePotionEffects()) {
            Bukkit.getPlayer(player2.getUuid()).removePotionEffect(effect.getType());
        }

        safeDelete();
    }

    @EventHandler
    public void onDamageEvent(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof Player && (e.getDamager().getUniqueId().equals(player1.getUuid()) || e.getDamager().getUniqueId().equals(player2.getUuid()))) {
            if(!isInMatch) {
                e.getDamager().sendMessage(ChatColor.RED + "You are not in the match.");
                e.setCancelled(true);
            } else {
                Player damager = (Player) e.getDamager();
                Entity p = e.getEntity();
                p.setVelocity(damager.getLocation().getDirection().setY(0.6).normalize().multiply(0.5));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getUniqueId().equals(player1.getUuid())) {
            event.getEntity().spigot().respawn();
            Location loc = Bukkit.getPlayer(player2.getUuid()).getLocation();
            event.getEntity().teleport(loc);
            event.getEntity().setVelocity(loc.getDirection().setY(2).normalize().multiply(2));
            isInMatch = false;
            endMatch(event.getEntity());
        } else if (event.getEntity().getUniqueId().equals(player2.getUuid())) {
            event.getEntity().spigot().respawn();
            Location loc = Bukkit.getPlayer(player1.getUuid()).getLocation();
            event.getEntity().teleport(loc);
            event.getEntity().setVelocity(loc.getDirection().setY(2).normalize().multiply(2));
            isInMatch = false;
            endMatch(event.getEntity());
        }
    }
}
