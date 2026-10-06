package edu.mc.telemetry;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public final class McTelemetryPlugin extends JavaPlugin implements Listener {
  private final Gson gson = new Gson(); private BlockingQueue<String> queue;
  private final AtomicLong dropped = new AtomicLong(); private final Map<UUID, Long> lastMove = new ConcurrentHashMap<>();
  private final Map<UUID, Location> lastBlock = new ConcurrentHashMap<>(); private KafkaProducer<String,String> producer; private Thread worker; private volatile boolean running;
  public void onEnable() { saveDefaultConfig(); queue = new LinkedBlockingQueue<>(getConfig().getInt("queue-capacity", 10000)); Properties p=new Properties(); p.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,getConfig().getString("bootstrap-servers")); p.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName()); p.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName()); p.put(ProducerConfig.ACKS_CONFIG,getConfig().getString("producer.acks","all")); p.put(ProducerConfig.LINGER_MS_CONFIG,getConfig().getInt("producer.linger-ms",10)); p.put(ProducerConfig.BATCH_SIZE_CONFIG,getConfig().getInt("producer.batch-size",32768)); producer=new KafkaProducer<>(p,new StringSerializer(),new StringSerializer()); running=true; worker=new Thread(this::drain,"mc-telemetry-kafka"); worker.start(); getServer().getPluginManager().registerEvents(this,this); long ticks=getConfig().getLong("stats-interval-seconds",5)*20; getServer().getScheduler().runTaskTimerAsynchronously(this,this::stats,ticks,ticks); getLogger().info("Telemetry enabled; async bounded queue active"); }
  private void drain(){ while(running||!queue.isEmpty()){ try { String s=queue.poll(250,TimeUnit.MILLISECONDS); if(s!=null) producer.send(new ProducerRecord<>(getConfig().getString("topic"),s),(m,e)->{if(e!=null)getLogger().warning("Kafka send failed: "+e.getMessage());}); } catch(Exception e){getLogger().warning("Producer loop failure: "+e.getMessage());} } producer.flush(); }
  private void emit(Map<String,Object> m){ if(!queue.offer(gson.toJson(m))) dropped.incrementAndGet(); }
  private Map<String,Object> base(String type,Player p){Map<String,Object> m=new HashMap<>();m.put("ts",System.currentTimeMillis());m.put("type",type); if(p!=null){m.put("player",p.getName());m.put("uuid",p.getUniqueId().toString());m.put("world",p.getWorld().getName());m.put("x",p.getLocation().getBlockX());m.put("y",p.getLocation().getBlockY());m.put("z",p.getLocation().getBlockZ());} return m;}
  @EventHandler public void join(PlayerJoinEvent e){emit(base("join",e.getPlayer()));}
  @EventHandler public void quit(PlayerQuitEvent e){emit(base("quit",e.getPlayer())); lastMove.remove(e.getPlayer().getUniqueId()); lastBlock.remove(e.getPlayer().getUniqueId());}
  @EventHandler public void breakBlock(BlockBreakEvent e){Map<String,Object>m=base("block_break",e.getPlayer());m.put("detail",e.getBlock().getType().name());m.put("x",e.getBlock().getX());m.put("y",e.getBlock().getY());m.put("z",e.getBlock().getZ());emit(m);}
  @EventHandler public void placeBlock(BlockPlaceEvent e){Map<String,Object>m=base("block_place",e.getPlayer());m.put("detail",e.getBlock().getType().name());emit(m);}
  @EventHandler public void death(PlayerDeathEvent e){Map<String,Object>m=base("death",e.getEntity());m.put("detail",e.getDeathMessage()==null?"unknown":e.getDeathMessage());emit(m);}
  @EventHandler public void chat(AsyncPlayerChatEvent e){Map<String,Object>m=base("chat",e.getPlayer());m.put("chat",e.getMessage());m.put("detail","chat");emit(m);}
  @EventHandler public void move(PlayerMoveEvent e){if(e.getTo()==null)return; UUID id=e.getPlayer().getUniqueId(); long now=System.currentTimeMillis(); long wait=getConfig().getLong("move-throttle-ms",1000); Location old=lastBlock.get(id), n=e.getTo(); if((old==null||old.getBlockX()!=n.getBlockX()||old.getBlockY()!=n.getBlockY()||old.getBlockZ()!=n.getBlockZ()) && now-lastMove.getOrDefault(id,0L)>=wait){lastMove.put(id,now);lastBlock.put(id,n.clone());emit(base("move",e.getPlayer()));}}
  private void stats(){Runtime r=Runtime.getRuntime();Map<String,Object>m=base("server_stats",null);m.put("tps",Bukkit.getTPS()[0]);m.put("used_ram_mb",(r.totalMemory()-r.freeMemory())/1048576.0);m.put("max_ram_mb",r.maxMemory()/1048576.0);m.put("online_players",Bukkit.getOnlinePlayers().size());m.put("dropped_events",dropped.get());emit(m);}
  public void onDisable(){running=false;try{if(worker!=null)worker.join(5000);}catch(InterruptedException e){Thread.currentThread().interrupt();}if(producer!=null)producer.close(Duration.ofSeconds(5));}
}