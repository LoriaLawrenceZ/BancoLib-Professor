package bancolib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Funcoes {

    public static int testeUpdate(String tabela, int id, String[] colunas, Object[] valores) {
        StringBuilder VALORES = new StringBuilder();

        for (int i = 0; i < colunas.length; i++) {
            if (valores[i] instanceof Number) {
                if (!VALORES.isEmpty()) {
                    VALORES.append(", ");
                }

                VALORES.append(colunas[i]).append("=").append(String.valueOf(valores[i]));
            } else if (valores[i] instanceof String) {
                if (!VALORES.isEmpty()) {
                    VALORES.append(", ");
                }

                VALORES.append(colunas[i]).append("=").append("'").append(String.valueOf(valores[i])).append("'");
            }
        }

        String SQL_SELECT = String.format(
                "UPDATE %s SET %s WHERE id=%d;",
                tabela, VALORES.toString(), id);

        // auto close connection and preparedStatement
        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:18745/BANCO", "USUARIO", "SENHA"); PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;

        }
    }

    public static int testeInsert(String tabela, int id, String[] colunas, Object[] valores) {
        StringBuilder COLUNAS = new StringBuilder();
        StringBuilder VALORES = new StringBuilder();

        for (String coluna : colunas) {
            if (!COLUNAS.isEmpty()) {
                COLUNAS.append(", ");
            }

            COLUNAS.append(coluna);
        }

        for (Object valor : valores) {
            if (valor instanceof Number) {
                if (!VALORES.isEmpty()) {
                    VALORES.append(", ");
                }

                VALORES.append(String.valueOf(valor));
            } else if (valor instanceof String) {
                if (!VALORES.isEmpty()) {
                    VALORES.append(", ");
                }

                VALORES.append("'").append(String.valueOf(valor)).append("'");
            }
        }

        String SQL_SELECT = String.format(
                "INSERT INTO %s (%s) VALUES (%s) WHERE id=%s;",
                tabela, COLUNAS, VALORES, id);

        // auto close connection and preparedStatement
        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:18745/BANCO", "USUARIO", "SENHA"); PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;

        }
    }

    public static int testeDelete(String tabela, int id) {
        String SQL_SELECT = String.format(
                "DELETE FROM %s WHERE id=%d;", tabela, id);

        // auto close connection and preparedStatement
        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:18745/BANCO", "USUARIO", "SENHA"); PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;

        }
    }
}
