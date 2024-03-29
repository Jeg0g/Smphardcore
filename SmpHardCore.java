package me.jeg0g.smphardcore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public final class SmpHardCore extends JavaPlugin implements Listener {
    public int countItems(Inventory inven){
        if (inven.getContents()==null){
            return 0;
        }
        int count=0;
        for (ItemStack i:inven.getContents()){
            if (i!=null){
                count+=i.getAmount();
            }
        }
        return count;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("SMPHardCore Has Started");
        Path p = Paths.get(getDataFolder().getAbsolutePath());
        Path p2 = Paths.get(getDataFolder().getAbsolutePath()+"\\playerdata");
        if (!Files.exists(p)){
            try{
                Files.createDirectories(p);
                Files.createDirectories(p2);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }else if(!Files.exists(p2)){
            try{
                Files.createDirectories(p2);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        File cfgFile = new File(getDataFolder().getAbsolutePath()+"\\config.yml");
        if (!cfgFile.exists()){
            try{
                cfgFile.createNewFile();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(cfgFile);
        String[] storageTypes={"chest","shulker_box","barrel","blast_furnace","dispenser","dropper","furnace","hopper","trapped_chest","smoker","item_frame","chest_minecart","hopper_minecart","armor_stand","item_frame"};
        for (String st:storageTypes){
            if (cfg.get("enabled_storage_clears."+st)==null){
                cfg.set("enabled_storage_clears."+st,true);
            }
        }
        if (cfg.get("noitemdrops")==null){
            cfg.set("noitemdrops",true);
        }
        if (cfg.get("clearenderchest")==null){
            cfg.set("clearenderchest",true);
        }
        if (cfg.get("printclearsondeath")==null){
            cfg.set("printclearsondeath",true);
        }
        if (cfg.get("printnumitemscleared")==null){
            cfg.set("printnumitemscleared",true);
        }
        try{
            cfg.save(cfgFile);
        }catch (IOException e) {
            e.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(this,this);
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if ((event.getBlockPlaced().getType().name().equals("CHEST"))
                || event.getBlockPlaced().getType().name().equals("SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("WHITE_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("ORANGE_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("MAGENTA_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("LIGHT_BLUE_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("YELLOW_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("LIME_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("PINK_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("GRAY_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("LIGHT_GRAY_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("CYAN_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("PURPLE_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("BLUE_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("BROWN_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("GREEN_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("RED_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("BLACK_SHULKER_BOX")
                || event.getBlockPlaced().getType().name().equals("BARREL")
                || event.getBlockPlaced().getType().name().equals("BLAST_FURNACE")
                || event.getBlockPlaced().getType().name().equals("DISPENSER")
                || event.getBlockPlaced().getType().name().equals("DROPPER")
                || event.getBlockPlaced().getType().name().equals("FURNACE")
                || event.getBlockPlaced().getType().name().equals("HOPPER")
                || event.getBlockPlaced().getType().name().equals("TRAPPED_CHEST")
                || event.getBlockPlaced().getType().name().equals("SMOKER")){
            PlayerMemory memory = PlayerUtility.getPlayerMemory(event.getPlayer());
            memory.addStorageCord(String.valueOf(event.getBlockPlaced().getLocation().getWorld().getName())+" "+String.valueOf(event.getBlockPlaced().getLocation().getBlockX())+" "
                    +String.valueOf(event.getBlockPlaced().getLocation().getBlockY())+" "+String.valueOf(event.getBlockPlaced().getLocation().getBlockZ()));
            File f = new File((getDataFolder().getAbsolutePath() +"\\playerdata\\"+event.getPlayer().getUniqueId() + ".yml"));
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            cfg.set("stats.storagecords", memory.getStringCords());
            try{
                cfg.save(f);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (event.getBlock().getType().name().equals("CHEST")
                || event.getBlock().getType().name().equals("SHULKER_BOX")
                || event.getBlock().getType().name().equals("WHITE_SHULKER_BOX")
                || event.getBlock().getType().name().equals("ORANGE_SHULKER_BOX")
                || event.getBlock().getType().name().equals("MAGENTA_SHULKER_BOX")
                || event.getBlock().getType().name().equals("LIGHT_BLUE_SHULKER_BOX")
                || event.getBlock().getType().name().equals("YELLOW_SHULKER_BOX")
                || event.getBlock().getType().name().equals("LIME_SHULKER_BOX")
                || event.getBlock().getType().name().equals("PINK_SHULKER_BOX")
                || event.getBlock().getType().name().equals("GRAY_SHULKER_BOX")
                || event.getBlock().getType().name().equals("LIGHT_GRAY_SHULKER_BOX")
                || event.getBlock().getType().name().equals("CYAN_SHULKER_BOX")
                || event.getBlock().getType().name().equals("PURPLE_SHULKER_BOX")
                || event.getBlock().getType().name().equals("BLUE_SHULKER_BOX")
                || event.getBlock().getType().name().equals("BROWN_SHULKER_BOX")
                || event.getBlock().getType().name().equals("GREEN_SHULKER_BOX")
                || event.getBlock().getType().name().equals("RED_SHULKER_BOX")
                || event.getBlock().getType().name().equals("BLACK_SHULKER_BOX")
                || event.getBlock().getType().name().equals("BARREL")
                || event.getBlock().getType().name().equals("BLAST_FURNACE")
                || event.getBlock().getType().name().equals("DISPENSER")
                || event.getBlock().getType().name().equals("DROPPER")
                || event.getBlock().getType().name().equals("FURNACE")
                || event.getBlock().getType().name().equals("HOPPER")
                || event.getBlock().getType().name().equals("TRAPPED_CHEST")
                || event.getBlock().getType().name().equals("SMOKER")){
            PlayerMemory memory = PlayerUtility.getPlayerMemory(event.getPlayer());
            if (memory.getStringCords().contains(String.valueOf(event.getBlock().getLocation().getWorld().getName())+" "
                    +String.valueOf(event.getBlock().getLocation().getBlockX())+" "+String.valueOf(event.getBlock().getLocation().getBlockY())+" "+String.valueOf(event.getBlock().getLocation().getBlockZ()))){
                memory.removeStorageCord(String.valueOf(event.getBlock().getLocation().getWorld().getName())+" "
                        +String.valueOf(event.getBlock().getLocation().getBlockX())+" "+String.valueOf(event.getBlock().getLocation().getBlockY())+" "+String.valueOf(event.getBlock().getLocation().getBlockZ()));
                File f = new File((getDataFolder().getAbsolutePath() +"\\playerdata\\"+event.getPlayer().getUniqueId() + ".yml"));
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                cfg.set("stats.storagecords", memory.getStringCords());
                try{
                    cfg.save(f);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                File folder = new File(getDataFolder().getAbsolutePath()+"\\playerdata");
                File[] fileList = folder.listFiles();
                for (File fff:fileList){
                    FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(fff);
                    String StorageString = cfg2.getString("stats.storagecords");
                    ArrayList<String> StorageLst = new ArrayList<String>(Arrays.asList(StorageString.split(",")));
                    boolean found=false;
                    for (int i=0;i<StorageLst.size();i++){
                        if (StorageLst.get(i).equals(String.valueOf(event.getBlock().getLocation().getWorld().getName())+" "
                                +String.valueOf(event.getBlock().getLocation().getBlockX())+" "+String.valueOf(event.getBlock().getLocation().getBlockY())+" "+String.valueOf(event.getBlock().getLocation().getBlockZ()))){
                            StorageLst.remove(i);
                            found=true;
                            break;
                        }
                    }
                    if (found) {
                        String tempstr = "";
                        for (String str : StorageLst) {
                            tempstr += (str+",");
                        }
                        StorageString = tempstr;
                        cfg2.set("stats.storagecords", StorageString);
                        try {
                            cfg2.save(fff);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        File cfgFile = new File(getDataFolder().getAbsolutePath()+"\\config.yml");
        FileConfiguration cfg1 = YamlConfiguration.loadConfiguration(cfgFile);
        if (cfg1.getBoolean("clearenderchest")){
            event.getEntity().getEnderChest().clear();
        }
        if (cfg1.getBoolean("noitemdrops")){
            event.getDrops().clear();
        }
        int count = 0;
        int itemcount=0;
        PlayerMemory memory = PlayerUtility.getPlayerMemory(event.getEntity());
        File f = new File((getDataFolder().getAbsolutePath() +"\\playerdata\\"+event.getEntity().getUniqueId() + ".yml"));
        if (f.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            if (cfg.getString("stats.storagecords") != null) {
                memory.setStringCords(cfg.getString("stats.storagecords"));
            }
            if (cfg.getString("minecarts")!=null){
                String StorageString = cfg.getString("minecarts");
                ArrayList<String> StorageLst = new ArrayList<String>(Arrays.asList(StorageString.split(",")));
                if (cfg1.getBoolean("enabled_storage_clears.chest_minecart")){
                    for (World world : Bukkit.getWorlds()) {
                        Collection<StorageMinecart> entity = world.getEntitiesByClass(StorageMinecart.class);
                        for (StorageMinecart cart : entity) {
                            if (StorageLst.contains(cart.getUniqueId().toString())){
                                itemcount+=countItems(cart.getInventory());
                                cart.getInventory().clear();
                                count++;
                            }
                        }
                    }
                }
                if (cfg1.getBoolean("enabled_storage_clears.hopper_minecart")){
                    for (World world : Bukkit.getWorlds()) {
                        Collection<HopperMinecart> entity = world.getEntitiesByClass(HopperMinecart.class);
                        for (HopperMinecart cart : entity) {
                            if (StorageLst.contains(cart.getUniqueId().toString())){
                                itemcount+=countItems(cart.getInventory());
                                cart.getInventory().clear();
                                count++;
                            }
                        }
                    }
                }
                if (cfg1.getBoolean("enabled_storage_clears.armor_stand")){
                    for (World world : Bukkit.getWorlds()) {
                        Collection<ArmorStand> entity = world.getEntitiesByClass(ArmorStand.class);
                        for (ArmorStand c : entity) {
                            if (StorageLst.contains(c.getUniqueId().toString())){
                                for (ItemStack i:c.getEquipment().getArmorContents()){
                                    if (i.getType()!=Material.AIR){
                                        itemcount++;
                                    }
                                }
                                c.getEquipment().clear();
                                count++;
                            }
                        }
                    }
                }
                if (cfg1.getBoolean("enabled_storage_clears.item_frame")){
                    for (World world : Bukkit.getWorlds()) {
                        Collection<ItemFrame> entity = world.getEntitiesByClass(ItemFrame.class);
                        for (ItemFrame c : entity) {
                            if (StorageLst.contains(c.getUniqueId().toString())){
                                if (c.getItem().getType()!=Material.AIR){
                                    itemcount++;
                                }
                                c.setItem(null);
                                count++;
                            }
                        }
                    }
                    for (World world : Bukkit.getWorlds()) {
                        Collection<GlowItemFrame> entity = world.getEntitiesByClass(GlowItemFrame.class);
                        for (ItemFrame c : entity) {
                            if (StorageLst.contains(c.getUniqueId().toString())){
                                if (c.getItem().getType()!=Material.AIR){
                                    itemcount++;
                                }
                                c.setItem(null);
                                count++;
                            }
                        }
                    }
                }
            }
        }
        for (String str:memory.getStorageCords()){
            String[] strs = str.split(" ");
            if (strs.length==4) {
                Location loc = new Location(Bukkit.getWorld(strs[0]), Double.parseDouble(strs[1]), Double.parseDouble(strs[2]), Double.parseDouble(strs[3]));
                if ((loc.getBlock().getType().name().equals("CHEST") &&cfg1.getBoolean("enabled_storage_clears.chest"))||(loc.getBlock().getType().name().equals("TRAPPED_CHEST"))&&cfg1.getBoolean("enabled_storage_clears.trapped_chest")){
                    Chest c = (Chest) loc.getBlock().getState();
                    itemcount+=countItems(c.getInventory());
                    c.getInventory().clear();
                    count++;
                }else if((loc.getBlock().getType().name().equals("SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("WHITE_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("ORANGE_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("MAGENTA_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("LIGHT_BLUE_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("YELLOW_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("LIME_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("PINK_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("GRAY_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("LIGHT_GRAY_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("CYAN_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("PURPLE_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("BLUE_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("BROWN_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("GREEN_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("RED_SHULKER_BOX")
                        || loc.getBlock().getType().name().equals("BLACK_SHULKER_BOX"))&&cfg1.getBoolean("enabled_storage_clears.shulker_box")){
                    ShulkerBox c = (ShulkerBox) loc.getBlock().getState();
                    itemcount+=countItems(c.getInventory());
                    c.getInventory().clear();
                    count++;
                }else if(loc.getBlock().getType().name().equals("BARREL") &&cfg1.getBoolean("enabled_storage_clears.barrel")){
                    Barrel c = (Barrel) loc.getBlock().getState();
                    itemcount+=countItems(c.getInventory());
                    c.getInventory().clear();
                    count++;
                }else if(loc.getBlock().getType().name().equals("BLAST_FURNACE") &&cfg1.getBoolean("enabled_storage_clears.blast_furnace")){
                    BlastFurnace c = (BlastFurnace) loc.getBlock().getState();
                    itemcount+=countItems(c.getInventory());
                    c.getInventory().clear();
                    count++;
                }else if(loc.getBlock().getType().name().equals("DISPENSER") &&cfg1.getBoolean("enabled_storage_clears.dispenser")){
                    Dispenser c = (Dispenser) loc.getBlock().getState();
                    itemcount+=countItems(c.getInventory());
                    c.getInventory().clear();
                    count++;
                }else if(loc.getBlock().getType().name().equals("DROPPER") &&cfg1.getBoolean("enabled_storage_clears.dropper")) {
                    Dropper c = (Dropper) loc.getBlock().getState();
                    itemcount+=countItems(c.getInventory());
                    c.getInventory().clear();
                    count++;
                }else if(loc.getBlock().getType().name().equals("FURNACE") &&cfg1.getBoolean("enabled_storage_clears.furnace")){
                    Furnace c = (Furnace) loc.getBlock().getState();
                    itemcount+=countItems(c.getInventory());
                    c.getInventory().clear();
                    count++;
                }else if(loc.getBlock().getType().name().equals("HOPPER") &&cfg1.getBoolean("enabled_storage_clears.hopper")){
                    Hopper c = (Hopper) loc.getBlock().getState();
                    itemcount+=countItems(c.getInventory());
                    c.getInventory().clear();
                    count++;
                }else if(loc.getBlock().getType().name().equals("SMOKER") &&cfg1.getBoolean("enabled_storage_clears.smoker")) {
                    Smoker c = (Smoker) loc.getBlock().getState();
                    itemcount+=countItems(c.getInventory());
                    c.getInventory().clear();
                    count++;
                }
            }
        }
        if (cfg1.getBoolean("printclearsondeath")){
            if (cfg1.getBoolean("printnumitemscleared")){
                if (count==1){
                    Bukkit.broadcastMessage("Cleared "+itemcount+" items from 1 Storage Container");
                }
                else if (count>0){
                    Bukkit.broadcastMessage("Cleared "+itemcount+" items from "+count+" Storage Containers");
                }
            }else{
                if (count==1){
                    Bukkit.broadcastMessage("Cleared 1 Storage Container");
                }
                else if (count>0){
                    Bukkit.broadcastMessage("Cleared "+count+" Storage Containers");
                }
            }
        }
    }
    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event){
        if (event.getVehicle().getType()==EntityType.MINECART_CHEST||event.getVehicle().getType()==EntityType.MINECART_HOPPER){
            File folder = new File(getDataFolder().getAbsolutePath()+"\\playerdata");
            File[] fileList = folder.listFiles();
            for (File fff:fileList){
                FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(fff);
                if (cfg2.getString("minecarts")!=null){
                    String StorageString = cfg2.getString("minecarts");
                    ArrayList<String> StorageLst = new ArrayList<String>(Arrays.asList(StorageString.split(",")));
                    boolean found=false;
                    for (int i=0;i<StorageLst.size();i++){
                        if (event.getVehicle().getUniqueId().toString().equals(StorageLst.get(i))){
                            StorageLst.remove(i);
                            found=true;
                            break;
                        }
                    }
                    if (found) {
                        String tempstr = "";
                        for (String str : StorageLst) {
                            tempstr += (str + ",");
                        }
                        StorageString = tempstr;
                        cfg2.set("minecarts", StorageString);
                        try {
                            cfg2.save(fff);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if (event.getEntity().getType()==EntityType.ARMOR_STAND){
            File folder = new File(getDataFolder().getAbsolutePath()+"\\playerdata");
            File[] fileList = folder.listFiles();
            for (File fff:fileList){
                FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(fff);
                if (cfg2.getString("minecarts")!=null){
                    String StorageString = cfg2.getString("minecarts");
                    ArrayList<String> StorageLst = new ArrayList<String>(Arrays.asList(StorageString.split(",")));
                    boolean found=false;
                    for (int i=0;i<StorageLst.size();i++){
                        if (event.getEntity().getUniqueId().toString().equals(StorageLst.get(i))){
                            StorageLst.remove(i);
                            found=true;
                            break;
                        }
                    }
                    if (found) {
                        String tempstr = "";
                        for (String str : StorageLst) {
                            tempstr += (str + ",");
                        }
                        StorageString = tempstr;
                        cfg2.set("minecarts", StorageString);
                        try {
                            cfg2.save(fff);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event){
        if (event.getEntity().getType()==EntityType.ITEM_FRAME||event.getEntity().getType()==EntityType.GLOW_ITEM_FRAME){
            File folder = new File(getDataFolder().getAbsolutePath()+"\\playerdata");
            File[] fileList = folder.listFiles();
            for (File fff:fileList){
                FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(fff);
                if (cfg2.getString("minecarts")!=null){
                    String StorageString = cfg2.getString("minecarts");
                    ArrayList<String> StorageLst = new ArrayList<String>(Arrays.asList(StorageString.split(",")));
                    boolean found=false;
                    for (int i=0;i<StorageLst.size();i++){
                        if (event.getEntity().getUniqueId().toString().equals(StorageLst.get(i))){
                            StorageLst.remove(i);
                            found=true;
                            break;
                        }
                    }
                    if (found) {
                        String tempstr = "";
                        for (String str : StorageLst) {
                            tempstr += (str + ",");
                        }
                        StorageString = tempstr;
                        cfg2.set("minecarts", StorageString);
                        try {
                            cfg2.save(fff);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction()== Action.RIGHT_CLICK_BLOCK){
            if ((event.getPlayer().getTargetBlock(null,5).getType().name().equals("RAIL")
                    ||event.getPlayer().getTargetBlock(null,5).getType().name().equals("POWERED_RAIL")
                    ||event.getPlayer().getTargetBlock(null,5).getType().name().equals("DETECTOR_RAIL")
                    ||event.getPlayer().getTargetBlock(null,5).getType().name().equals("ACTIVATOR_RAIL"))
                    &&(event.getPlayer().getInventory().getItemInMainHand().getType().name().equals("CHEST_MINECART")||event.getPlayer().getInventory().getItemInOffHand().getType().name().equals("CHEST_MINECART")
                    ||event.getPlayer().getInventory().getItemInMainHand().getType().name().equals("HOPPER_MINECART")||event.getPlayer().getInventory().getItemInOffHand().getType().name().equals("HOPPER_MINECART"))){
                Location loc = event.getPlayer().getTargetBlock(null, 5).getLocation();
                loc.setX(loc.getX()+0.5);
                loc.setY(loc.getY()+0.5);
                loc.setZ(loc.getZ()+0.5);
                event.setCancelled(true);
                Entity cart;
                if (event.getPlayer().getInventory().getItemInMainHand().getType().name().equals("CHEST_MINECART")){
                    cart = loc.getWorld().spawnEntity(loc, EntityType.MINECART_CHEST);
                    event.getPlayer().getInventory().setItemInMainHand(null);
                }else if (event.getPlayer().getInventory().getItemInOffHand().getType().name().equals("CHEST_MINECART")&&(!event.getPlayer().getInventory().getItemInMainHand().getType().name().equals("HOPPER_MINECART"))){
                    cart = loc.getWorld().spawnEntity(loc, EntityType.MINECART_CHEST);
                    event.getPlayer().getInventory().setItemInOffHand(null);
                }else if (event.getPlayer().getInventory().getItemInMainHand().getType().name().equals("HOPPER_MINECART")){
                    cart = loc.getWorld().spawnEntity(loc, EntityType.MINECART_HOPPER);
                    event.getPlayer().getInventory().setItemInMainHand(null);
                }
                else{
                    cart = loc.getWorld().spawnEntity(loc, EntityType.MINECART_HOPPER);
                    event.getPlayer().getInventory().setItemInOffHand(null);
                }
                File f = new File((getDataFolder().getAbsolutePath() +"\\playerdata\\"+event.getPlayer().getUniqueId() + ".yml"));
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                String cfgstr="";
                if (cfg.getString("minecarts")!=null){
                    cfgstr+=cfg.getString("minecarts");
                }
                cfgstr+=cart.getUniqueId()+",";
                cfg.set("minecarts",cfgstr);
                try{
                    cfg.save(f);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (event.getPlayer().getInventory().getItemInMainHand().getType().name().equals("ARMOR_STAND")){
                Location loc = event.getPlayer().getTargetBlock(null, 5).getLocation();
                loc.setDirection(event.getPlayer().getLocation().getDirection().multiply(-1));
                loc.add(event.getBlockFace().getDirection());
                loc.setX(loc.getX()+0.5);
                loc.setZ(loc.getZ()+0.5);
                event.setCancelled(true);
                if (!event.getBlockFace().getDirection().equals(new Vector(0, -1, 0))){
                    Entity c = loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
                    if (event.getPlayer().getInventory().getItemInMainHand().equals("ARMOR_STAND")){
                        event.getPlayer().getInventory().setItemInMainHand(null);
                    }else{
                        event.getPlayer().getInventory().setItemInOffHand(null);
                    }
                    File f = new File((getDataFolder().getAbsolutePath() +"\\playerdata\\"+event.getPlayer().getUniqueId() + ".yml"));
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                    String cfgstr="";
                    if (cfg.getString("minecarts")!=null){
                        cfgstr+=cfg.getString("minecarts");
                    }
                    cfgstr+=c.getUniqueId()+",";
                    cfg.set("minecarts",cfgstr);
                    try{
                        cfg.save(f);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if (event.getPlayer().getInventory().getItemInMainHand().getType().name().equals("ITEM_FRAME")){
                Location loc = event.getPlayer().getTargetBlock(null, 5).getLocation();
                loc.setDirection(event.getBlockFace().getDirection());
                loc.add(event.getBlockFace().getDirection());
                loc.setX(loc.getX()+0.5);
                loc.setZ(loc.getZ()+0.5);
                event.setCancelled(true);
                ItemFrame c = (ItemFrame) loc.getWorld().spawnEntity(loc, EntityType.ITEM_FRAME);
                c.setFacingDirection(event.getBlockFace());
                if (event.getPlayer().getInventory().getItemInMainHand().equals("ITEM_FRAME")){
                    event.getPlayer().getInventory().setItemInMainHand(null);
                }else{
                    event.getPlayer().getInventory().setItemInOffHand(null);
                }
                File f = new File((getDataFolder().getAbsolutePath() +"\\playerdata\\"+event.getPlayer().getUniqueId() + ".yml"));
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                String cfgstr="";
                if (cfg.getString("minecarts")!=null){
                    cfgstr+=cfg.getString("minecarts");
                }
                cfgstr+=c.getUniqueId()+",";
                cfg.set("minecarts",cfgstr);
                try{
                    cfg.save(f);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (event.getPlayer().getInventory().getItemInMainHand().getType().name().equals("GLOW_ITEM_FRAME")){
                Location loc = event.getPlayer().getTargetBlock(null, 5).getLocation();
                loc.setDirection(event.getBlockFace().getDirection());
                loc.add(event.getBlockFace().getDirection());
                loc.setX(loc.getX()+0.5);
                loc.setZ(loc.getZ()+0.5);
                event.setCancelled(true);
                ItemFrame c = (ItemFrame) loc.getWorld().spawnEntity(loc, EntityType.GLOW_ITEM_FRAME);
                c.setFacingDirection(event.getBlockFace());
                if (event.getPlayer().getInventory().getItemInMainHand().equals("GLOW_ITEM_FRAME")){
                    event.getPlayer().getInventory().setItemInMainHand(null);
                }else{
                    event.getPlayer().getInventory().setItemInOffHand(null);
                }
                File f = new File((getDataFolder().getAbsolutePath() +"\\playerdata\\"+event.getPlayer().getUniqueId() + ".yml"));
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                String cfgstr="";
                if (cfg.getString("minecarts")!=null){
                    cfgstr+=cfg.getString("minecarts");
                }
                cfgstr+=c.getUniqueId()+",";
                cfg.set("minecarts",cfgstr);
                try{
                    cfg.save(f);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        PlayerMemory memory = new PlayerMemory();
        File f = new File((getDataFolder().getAbsolutePath() +"\\playerdata\\"+event.getPlayer().getUniqueId() + ".yml"));
        if (f.exists()){
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            if (cfg.getString("stats.storagecords")!=null) {
                memory.setStringCords(cfg.getString("stats.storagecords"));
            }
        }else{
            try{
                f.createNewFile();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration cfg3 = YamlConfiguration.loadConfiguration(f);
        cfg3.set("stats.storagecords", memory.getStringCords());
        try{
            cfg3.save(f);
        }catch (IOException e) {
            e.printStackTrace();
        }
        PlayerUtility.setPlayerMemory(event.getPlayer(), memory);
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        PlayerMemory memory = PlayerUtility.getPlayerMemory(event.getPlayer());
        File f = new File((getDataFolder().getAbsolutePath() +"\\playerdata\\"+event.getPlayer().getUniqueId() + ".yml"));
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        cfg.set("stats.storagecords", memory.getStringCords());
        try{
            cfg.save(f);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Shutting Down SMPHardCore");
    }
}