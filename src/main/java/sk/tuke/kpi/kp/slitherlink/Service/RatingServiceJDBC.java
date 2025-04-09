package sk.tuke.kpi.kp.slitherlink.Service;

import sk.tuke.kpi.kp.slitherlink.Entity.Rating;

import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    private static final String INSERT_OR_UPDATE =
            "INSERT INTO rating (player, game, rating, ratedon) VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT (player, game) DO UPDATE SET rating = EXCLUDED.rating, ratedon = EXCLUDED.ratedon";

    private static final String SELECT_AVG = "SELECT AVG(rating) FROM rating WHERE game = ?";
    private static final String SELECT_USER_RATING = "SELECT rating FROM rating WHERE game = ? AND player = ?";
    private static final String DELETE_ALL = "DELETE FROM rating";

    // Оновлений метод для додавання або оновлення рейтингу
    @Override
    public void setRating(Rating rating) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT_OR_UPDATE)
        ) {
            statement.setString(1, rating.getPlayer());
            statement.setString(2, rating.getGame());
            statement.setInt(3, rating.getRating());
            statement.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RatingException("Problem setting rating", e);
        }
    }

    // Метод для додавання рейтингу (аналогічний setRating)
    public void addRating(String game, String player, int ratingValue, Date ratedOn) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT_OR_UPDATE)
        ) {
            statement.setString(1, player);
            statement.setString(2, game);
            statement.setInt(3, ratingValue);
            statement.setTimestamp(4, new Timestamp(ratedOn.getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RatingException("Error adding rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_AVG)
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem loading average rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_RATING)
        ) {
            statement.setString(1, game);
            statement.setString(2, player);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RatingException("Problem loading user rating", e);
        }
    }
    @Override
    public void reset() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE_ALL);
        } catch (SQLException e) {
            throw new RatingException("Problem deleting ratings", e);
        }
    }

    @Override
    public void addRating(Rating rating) throws RatingException {
    }
}
