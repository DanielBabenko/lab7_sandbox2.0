package server.databaseManager;

import server.exceptions.InvalidFieldY;
import server.exceptions.NullX;
import server.object.Coordinates;
import server.object.LabWork;
import server.object.Person;
import server.object.enums.Color;
import server.object.enums.Difficulty;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class LabWorksDatabaseManager {
    private final ConnectionManager connectionManager;

    public LabWorksDatabaseManager (String url, String login, String password) {
        connectionManager = new ConnectionManager(url, login, password);
    }

    public LabWorksDatabaseManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    public Map<Integer, Person> loadAuthors() throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM authors");
        ResultSet rs = ps.executeQuery();

        Map<Integer,Person> authors = new HashMap<>();

        while (rs.next()){
            Integer id = rs.getInt("id");
            Person author = new Person(
                    rs.getString("name"), Color.valueOf(rs.getString("eye_color")),
                    rs.getDouble("height"), rs.getDate("birthday").toString()
            );

            authors.put(id,author);
        }

        conn.close();
        return authors;
    }

    public Map<Integer,Coordinates> loadCoordinates() throws SQLException, NullX, InvalidFieldY {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM coordinates");
        ResultSet rs = ps.executeQuery();

        Map<Integer,Coordinates> coordinatesMap = new HashMap<>();

        while (rs.next()){
            Integer id = rs.getInt("id");
            Coordinates cords = new Coordinates(
                    rs.getInt("X"),
                    rs.getDouble("Y")
            );

            coordinatesMap.put(id,cords);
        }

        conn.close();
        return coordinatesMap;
    }

    public List<LabWork> loadLabWorks() throws SQLException, NullX, InvalidFieldY {
        Map<Integer,Person> authors = loadAuthors();
        Map<Integer,Coordinates> coordinates = loadCoordinates();
        List<LabWork> labWorkList = new LinkedList<>();

        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM labWorks");
        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            int authorId = rs.getInt("person_id");
            Person author = authors.get(authorId);

            int coordinatesId = rs.getInt("coordinates_id");
            Coordinates cords = coordinates.get(coordinatesId);

            LabWork lab = new LabWork(
                    rs.getInt("id"),rs.getString("name"),
                    rs.getInt("minimal_point"), rs.getLong("tuned_in_works"),
                    Difficulty.valueOf(rs.getString("difficulty")),
                    cords, author,
                    rs.getTimestamp("creation_date").toLocalDateTime().toString()
            );
            labWorkList.add(lab);
        }

        conn.close();
        return labWorkList;
    }

    public int addAuthors(Person author) throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO authors(name, eye_color, height, birthday)"
                + " VALUES(?,?,?,?)"
        );
        ps.setString(1,author.getName());
        ps.setString(2,author.getEyeColor().toString());
        ps.setDouble(3,author.getHeight());
        ps.setDate(4, Date.valueOf(author.getBirthday()));

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    public int addCoordinates(Coordinates coordinates) throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO coordinates(X,Y)"
                        + " VALUES(?,?)"
        );
        ps.setInt(1,coordinates.getX());
        ps.setDouble(2,coordinates.getY());

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    public int addLabWork(LabWork labWork) throws SQLException{
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO labWork(name, minimal_point,tuned_in_works,difficulty,coordinates_id,person_id)"
                        + " VALUES(?,?,?,?,?,?)"
        );
        ps.setString(1,labWork.getName());
        ps.setInt(2,labWork.getMinimalPoint());

        if (labWork.getTunedInWorks() == null) ps.setNull(3, Types.INTEGER);
        else ps.setInt(3, labWork.getTunedInWorks());
        ps.setString(4,labWork.getDifficulty().toString());

        int coordinates_id = addCoordinates(labWork.getCoordinates());
        ps.setInt(5, coordinates_id);

        int person_id = addAuthors(labWork.getAuthor());
        ps.setInt(6, person_id);

        ResultSet rs = ps.executeQuery();
        conn.close();
        rs.next();

        return rs.getInt(1);
    }

    public int upgradeAuthor(int personId, Person newAuthor) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE authors "
                + "SET name = ?, eye_color = ?, height = ? , birthday = ? "
                + "WHERE id = ?"
        );

        ps.setString(1,newAuthor.getName());
        ps.setString(2,newAuthor.getEyeColor().toString());
        ps.setDouble(3,newAuthor.getHeight());
        ps.setDate(4, Date.valueOf(newAuthor.getBirthday()));
        ps.setInt(5,personId);

        int res = ps.executeUpdate();
        conn.close();
        return res;
    }

    public int upgradeCoordinates(int coordinatesId, Coordinates newCoordinates) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE coordinates "
                + "SET X = ?, Y = ? "
                + "WHERE id = ?"
        );

        ps.setInt(1,newCoordinates.getX());
        ps.setDouble(2,newCoordinates.getY());
        ps.setInt(3,coordinatesId);

        int res = ps.executeUpdate();
        conn.close();
        return res;
    }

    public int upgradeLabWorks(int labWorkId, LabWork newLabWork) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE LabWorks "
                + "name = ?, minimal_point = ?,tuned_in_works = ?,difficulty = ?,coordinates_id = ?,person_id = ? "
                + "WHERE id = ?"
        );

        ps.setString(1,newLabWork.getName());
        ps.setInt(2,newLabWork.getMinimalPoint());

        if (newLabWork.getTunedInWorks() == null) ps.setNull(3, Types.INTEGER);
        else ps.setInt(3, newLabWork.getTunedInWorks());
        ps.setString(4,newLabWork.getDifficulty().toString());

        int coordinates_id = upgradeCoordinates(labWorkId ,newLabWork.getCoordinates());
        ps.setInt(5, coordinates_id);

        int person_id = upgradeAuthor(labWorkId,newLabWork.getAuthor());
        ps.setInt(6, person_id);

        ps.setInt(7,labWorkId);

        int res = ps.executeUpdate();
        conn.close();
        return res;
    }

    public int clearWorkers() throws SQLException {
        Connection connection = getConnection();

        PreparedStatement statement_labWork = connection.prepareStatement(
                "DELETE FROM labWorks"
        );
        statement_labWork.executeUpdate();

        PreparedStatement statement_author = connection.prepareStatement(
                "DELETE FROM authors"
        );
        statement_author.executeUpdate();

        PreparedStatement statement_coordinates = connection.prepareStatement(
                "DELETE FROM coordinates"
        );

        int res = statement_coordinates.executeUpdate();
        connection.close();
        return res;
    }

    public int removeLabWork(LabWork labWork) throws SQLException {
        Connection connection = getConnection();

        PreparedStatement statement_labWork = connection.prepareStatement(
                "DELETE FROM labWorks WHERE id = ?"
        );

        statement_labWork.setInt(1, labWork.getId());
        int res = statement_labWork.executeUpdate();
        connection.close();
        return res;
    }

}
