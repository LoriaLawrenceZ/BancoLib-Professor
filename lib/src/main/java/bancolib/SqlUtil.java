package bancolib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    public <T> List<T> selectPorClasse(Class<T> clazz, String where) {
        String tabela = clazz.getSimpleName().toLowerCase();
        StringBuilder colunas = new StringBuilder();
        String[] colunasArray = new String[clazz.getFields().length];
        Class<?>[] tipos = new Class<?>[clazz.getFields().length];

        List<T> retorno = new ArrayList<>();

        int i = 0;
        for (Field campo : clazz.getFields()) {
            if (!colunas.isEmpty()) {
                colunas.append(", ");
            }

            colunas.append(campo.getName().toLowerCase());
            colunasArray[i] = campo.getName().toLowerCase();
            tipos[i] = campo.getType();

            i++;
        }

        String SQL_SELECT = String.format("SELECT %s FROM %s", colunas.toString(), tabela);

        if (where != null && where.length() > 0) {
            SQL_SELECT = String.format(SQL_SELECT + " WHERE %s", where);
        }

        i = 0;

        try (PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Object[] valoresLinha = new Object[colunasArray.length];

                for (int coluna = 0; coluna < colunasArray.length; coluna++) {
                    valoresLinha[coluna] = rs.getObject(coluna + 1);
                }
                
                T instance = clazz.getDeclaredConstructor(tipos).newInstance(valoresLinha);
            
                retorno.add(instance);
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retorno;
    }

    public int update(String tabela, int id, String[] colunas, Object[] valores) {
        StringBuilder VALORES = new StringBuilder();

        for (int i = 0; i < colunas.length; i++) {
            if (valores[i] instanceof String) {
                if (!VALORES.isEmpty()) {
                    VALORES.append(", ");
                }

                VALORES.append(colunas[i]).append("=").append("'").append(String.valueOf(valores[i])).append("'");
            } else {
                if (!VALORES.isEmpty()) {
                    VALORES.append(", ");
                }

                VALORES.append(colunas[i]).append("=").append(String.valueOf(valores[i]));
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
