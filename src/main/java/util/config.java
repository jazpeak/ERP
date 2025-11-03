package util;

import java.util.Properties;
public final class config {
    private static final Properties P = new Properties();
    static {
        try (var in = config.class.getResourceAsStream("/application.properties")) {
            if (in == null) throw new IllegalStateException("application.properties not found");
            P.load(in);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    private config() {}
    public static String get(String k){ return P.getProperty(k); }
}
