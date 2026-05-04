import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InitDb {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlite:pfetracker.db";
        String scriptPath = "src/main/resources/init.sql";
        
        System.out.println("Reading script...");
        String sql = new String(Files.readAllBytes(Paths.get(scriptPath)));
        
        System.out.println("Connecting to database...");
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
             
            // Execute statements split by semicolon
            String[] statements = sql.split(";");
            for (String s : statements) {
                if (!s.trim().isEmpty()) {
                    stmt.execute(s.trim() + ";");
                }
            }
            System.out.println("Database initialized successfully!");
        }
    }
}
