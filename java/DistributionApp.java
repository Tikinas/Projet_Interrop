import java.sql.*;
import java.util.Random;

public class DistributionApp {

    public static void main(String[] args) throws Exception {
        // 🔧 Charger le driver H2 manuellement
        Class.forName("org.h2.Driver");

        // Connexion à une base H2 en mémoire
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:");

        // Créer une fonction-table virtuelle DISTRIBUTION
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE ALIAS IF NOT EXISTS DISTRIBUTION FOR \"" + DistributionApp.class.getName() + ".distribution\"");

        // Exécuter une requête test
        ResultSet rs = stmt.executeQuery("SELECT * FROM DISTRIBUTION(10, 0.0, 1.0)");
        while (rs.next()) {
            System.out.printf("(%.4f, %.4f)%n", rs.getDouble("X"), rs.getDouble("Y"));
        }

        rs.close();
        stmt.close();
        conn.close();
    }

    // Fonction utilisée comme table virtuelle
    public static ResultSet distribution(int n, double mu, double sigma) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE TEMP_DISTRIBUTION(X DOUBLE, Y DOUBLE)");

        Random random = new Random();
        PreparedStatement insert = conn.prepareStatement("INSERT INTO TEMP_DISTRIBUTION VALUES (?, ?)");

        for (int i = 0; i < n; ++i) {
            double x = mu + sigma * random.nextGaussian();
            double y = mu + sigma * random.nextGaussian();
            insert.setDouble(1, x);
            insert.setDouble(2, y);
            insert.executeUpdate();
        }

        return conn.createStatement().executeQuery("SELECT * FROM TEMP_DISTRIBUTION");
    }
}
