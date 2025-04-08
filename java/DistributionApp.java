import jep.SharedInterpreter;
import java.sql.*;

public class DistributionAppNumpy {

    public static boolean useNumpy = true;

    public static void main(String[] args) throws Exception {
        Class.forName("org.h2.Driver");

        Connection conn = DriverManager.getConnection("jdbc:h2:mem:");
        Statement stmt = conn.createStatement();

        // Création de la fonction-table virtuelle
        stmt.execute("CREATE ALIAS IF NOT EXISTS DISTRIBUTION FOR \"" + DistributionAppNumpy.class.getName() + ".distribution\"");

        // Exécution d'une requête SQL qui appelle Python via JEP
        ResultSet rs = stmt.executeQuery("SELECT * FROM DISTRIBUTION(10, 0.0, 1.0)");
        while (rs.next()) {
            System.out.printf("(%.4f, %.4f)%n", rs.getDouble("X"), rs.getDouble("Y"));
        }

        rs.close();
        stmt.close();
        conn.close();
    }

    public static ResultSet distribution(int n, double mu, double sigma) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE TEMP_DISTRIBUTION(X DOUBLE, Y DOUBLE)");

        PreparedStatement insert = conn.prepareStatement("INSERT INTO TEMP_DISTRIBUTION VALUES (?, ?)");

        if (useNumpy) {
            try (SharedInterpreter jep = new SharedInterpreter()) {
                jep.eval("import numpy as np");
                jep.eval("x = np.random.normal(" + mu + ", " + sigma + ", " + n + ")");
                jep.eval("y = np.random.normal(" + mu + ", " + sigma + ", " + n + ")");
                NDArray x_vals = (NDArray) jep.getValue("x");
                NDArray y_vals = (NDArray) jep.getValue("y");   




                for (int i = 0; i < n; i++) {
                    insert.setDouble(1, x_vals[i]);
                    insert.setDouble(2, y_vals[i]);
                    insert.executeUpdate();
                }
            } catch (Exception e) {
                throw new SQLException("Erreur JEP/Numpy", e);
            }
        }

        return conn.createStatement().executeQuery("SELECT * FROM TEMP_DISTRIBUTION");
    }
}
