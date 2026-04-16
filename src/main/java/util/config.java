package util;

import java.util.Properties;
public final class config {
    private static final Properties P = new Properties();
    static {
        try {
            var in = config.class.getResourceAsStream("/application.properties");
            if (in == null) in = config.class.getResourceAsStream("/application.example.properties");
            if (in == null) throw new IllegalStateException("No application properties found");
            P.load(in);
            in.close();
        } catch (Exception e) { throw new RuntimeException("Failed to load application: " + e.getMessage(), e); }
    }
    private config() {}
    public static String get(String k){ return P.getProperty(k); }
}
