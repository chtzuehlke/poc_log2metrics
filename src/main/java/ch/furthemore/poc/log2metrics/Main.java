package ch.furthemore.poc.log2metrics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import io.javalin.Javalin;

public class Main {
    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws InterruptedException, IOException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
        
        final String started = stamp();
        
        final String addr;
        if (System.getenv("ADDR") != null) {
            addr = System.getenv("ADDR");
        }
        else {
            addr = InetAddress.getLocalHost().toString();
        }
        
        int port;
        try {
            port = Integer.parseInt(System.getenv("PORT"));
        }
        catch (NumberFormatException e) {
            port = 8181;
        }
        final boolean echo = !"true".equals(System.getenv("NO_ECHO"));
        final boolean openMetrics = !"true".equals(System.getenv("PLAIN"));
        
        String patternCsv = System.getenv("PATTERNS");
        if (patternCsv == null) {
            patternCsv = "Dumped,Error,Logged";
        }
        final Map<String,AtomicLong> counterByPattern = new HashMap<>();
        for (String pattern : patternCsv.split(",")) {
            counterByPattern.put(pattern, new AtomicLong(0));
        }
        
        Javalin app = Javalin.create().start(port); 
        app.get("/", ctx -> {
            StringBuilder sb = new StringBuilder();
            
            for (Entry<String, AtomicLong> e : counterByPattern.entrySet()) {
                if (openMetrics) {
                    String name = e.getKey().toLowerCase().replaceAll(" ", "");
                    
                    sb.append("# TYPE "+name+" counter\n");
                    sb.append(name + "_total{a=\""+addr+"\"} "+e.getValue()+" " + stamp() + "\n");
                    sb.append(name + "_created{a=\""+addr+"\"} "+started+"\n");
                }
                else {
                    sb.append(e.getKey() + ": " + e.getValue().get() + "\n");
                }
            }
            ctx.result(sb.toString());
        });
        
        try (DataInputStream in = new DataInputStream(System.in); DataOutputStream out = new DataOutputStream(System.out)) {
//            out.writeChars("Begin\n"); //DEBUG
            
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                for (Entry<String, AtomicLong> e : counterByPattern.entrySet()) {
                    if (line.indexOf(e.getKey()) != -1) {
                        e.getValue().incrementAndGet();
                    }
                }
                
                if (echo) {
                    out.writeChars(line + "\n");
                }
            }
            
//            out.writeChars("End\n"); //DEBUG
        }
        
        app.stop();
    }

    private static String stamp() {
        return new BigDecimal(System.currentTimeMillis()).divide(new BigDecimal(1000)).toString();
    }
}
