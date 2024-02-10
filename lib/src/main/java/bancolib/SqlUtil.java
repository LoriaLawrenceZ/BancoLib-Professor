package bancolib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlUtil {

    Connection conn;

    public SqlUtil(String HOST, String PORTA, String BANCO, String USUARIO, String SENHA) {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://" + HOST + ":" + PORTA + "/" + BANCO, USUARIO, SENHA);
        } catch (SQLException ex) {
            throw new RuntimeException("Não foi possivel estabelecer uma conexao com o Banco de Dados!", ex);
        }
    }

    public int update(String tabela, int id, String[] colunas, Object[] valores) {
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
        try (PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;

        }
    }

    public int insert(String tabela, String[] colunas, Object[] valores) {
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

        String SQL_SELECT = "INSERT INTO " + tabela
                + " (" + COLUNAS.toString() + ") VALUES (" + VALORES.toString() + ");";

        // auto close connection and preparedStatement
        try (PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;

        }
    }

    public int delete(String tabela, int id) {
        String SQL_SELECT = String.format(
                "DELETE FROM %s WHERE id=%d;", tabela, id);

        // auto close connection and preparedStatement
        try (PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

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
