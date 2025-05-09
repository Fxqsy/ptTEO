package main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDatabase {
    private static final String DB_URL = "jdbc:sqlite:scores.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load SQLite JDBC driver: " + e.getMessage());
            System.exit(1);
        }
    }

    public ScoreDatabase() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        String createFinalScoresTable = "CREATE TABLE IF NOT EXISTS final_scores (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player_name TEXT NOT NULL, " +
                "final_health INTEGER, " +
                "final_score INTEGER, " +
                "potions INTEGER, " +
                "current_level INTEGER, " +
                "player_x REAL DEFAULT 0, " +
                "player_y REAL DEFAULT 0, " +
                "completed_game INTEGER DEFAULT 0, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";

        String createCumulativeScoresTable = "CREATE TABLE IF NOT EXISTS cumulative_scores (" +
                "player_name TEXT PRIMARY KEY, " +
                "total_score INTEGER DEFAULT 0, " +
                "completed_game INTEGER DEFAULT 0, " +
                "highest_level_x REAL DEFAULT 0, " +
                "highest_level_y REAL DEFAULT 0, " +
                "highest_level INTEGER DEFAULT 0, " +
                "potion_h INTEGER DEFAULT 0" +
                ")";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Crearea tabelelor
            statement.execute(createFinalScoresTable);
            statement.execute(createCumulativeScoresTable);


            System.out.println("Tables successfully initialized.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveFinalScore(String playerName, int finalHealth, int finalScore, int potions, int currentLevel, boolean completedGame, float playerX, float playerY) {
        String insertFinalScore = "INSERT INTO final_scores (player_name, final_health, final_score, potions, "
                + "current_level, completed_game, player_x, player_y) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String updateCumulativeScore = "INSERT OR REPLACE INTO cumulative_scores (player_name, total_score, completed_game, " +
                "highest_level_x, highest_level_y, highest_level, potion_h) " +
                "VALUES (?, " +
                "COALESCE((SELECT total_score FROM cumulative_scores WHERE player_name = ?), 0) + ?, " +
                "COALESCE((SELECT COUNT(*) FROM final_scores WHERE player_name = ? AND completed_game = 1), 0), " +
                "?, ?, ?, ?)";

        String getHighestLevelQuery = "SELECT current_level, player_x, player_y, potions " +
                "FROM final_scores " +
                "WHERE player_name = ? " +
                "ORDER BY current_level DESC, potions DESC " +
                "LIMIT 1";

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement finalScoreStmt = connection.prepareStatement(insertFinalScore);
                 PreparedStatement cumulativeStmt = connection.prepareStatement(updateCumulativeScore);
                 PreparedStatement highestLevelStmt = connection.prepareStatement(getHighestLevelQuery)) {

                // Salvam scorurile finale
                finalScoreStmt.setString(1, playerName);
                finalScoreStmt.setInt(2, finalHealth);
                finalScoreStmt.setInt(3, finalScore);
                finalScoreStmt.setInt(4, potions);
                finalScoreStmt.setInt(5, currentLevel);
                finalScoreStmt.setInt(6, completedGame ? 1 : 0);
                finalScoreStmt.setFloat(7, playerX);
                finalScoreStmt.setFloat(8, playerY);
                finalScoreStmt.executeUpdate();

                // Calculam nivelul maxim si coordonatele asociate
                float highestLevelX = 0;
                float highestLevelY = 0;
                int highestLevel = 0;
                int highestPotions = 0;
                highestLevelStmt.setString(1, playerName);
                try (ResultSet resultSet = highestLevelStmt.executeQuery()) {
                    if (resultSet.next()) {
                        highestLevel = resultSet.getInt("current_level");
                        highestLevelX = resultSet.getFloat("player_x");
                        highestLevelY = resultSet.getFloat("player_y");
                        highestPotions = resultSet.getInt("potions");
                    }
                }

                // Actualizam tabela cumulative_scores
                cumulativeStmt.setString(1, playerName);
                cumulativeStmt.setString(2, playerName);
                cumulativeStmt.setInt(3, finalScore);
                cumulativeStmt.setString(4, playerName);
                cumulativeStmt.setFloat(5, highestLevelX);
                cumulativeStmt.setFloat(6, highestLevelY);
                cumulativeStmt.setInt(7, highestLevel);
                cumulativeStmt.setInt(8, highestPotions);
                cumulativeStmt.executeUpdate();

                // Confirmare tranzacție
                connection.commit();
                System.out.println("Scores and progress saved for: " + playerName);

            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Transaction failed and rolled back: " + e.getMessage());
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error saving progress: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<CumulativeScoreEntry> getCumulativeScores(int limit) {
        List<CumulativeScoreEntry> cumulativeScores = new ArrayList<>();
        String query = "SELECT player_name, total_score, completed_game, highest_level, potion_h " +
                "FROM cumulative_scores " +
                "ORDER BY total_score DESC " +
                "LIMIT ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, limit);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String playerName = resultSet.getString("player_name");
                int totalScore = resultSet.getInt("total_score");
                boolean completedGame = resultSet.getInt("completed_game") == 1;
                int highestLevel = resultSet.getInt("highest_level");
                int highestPotions = resultSet.getInt("potion_h");
                cumulativeScores.add(new CumulativeScoreEntry(playerName, totalScore, completedGame, highestLevel, highestPotions));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving cumulative scores: " + e.getMessage());
            e.printStackTrace();
        }

        return cumulativeScores;
    }

    public boolean playerNameExists(String playerName) {
        String query = "SELECT 1 FROM cumulative_scores WHERE player_name = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking player name: " + e.getMessage());
            return false;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static class CumulativeScoreEntry {
        private final String playerName;
        private final int totalScore;
        private final boolean completedGame;
        private final int highestLevel;
        private final int highestPotions;

        public CumulativeScoreEntry(String playerName, int totalScore, boolean completedGame, int highestLevel, int highestPotions) {
            this.playerName = playerName;
            this.totalScore = totalScore;
            this.completedGame = completedGame;
            this.highestLevel = highestLevel;
            this.highestPotions = highestPotions;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getTotalScore() {
            return totalScore;
        }

        public boolean isCompletedGame() {
            return completedGame;
        }

        public int getHighestLevel() {
            return highestLevel;
        }

        public int getHighestPotions() {
            return highestPotions;
        }

        @Override
        public String toString() {
            return playerName + ": " + totalScore + " (Completed: " + completedGame +
                    ", Highest Level: " + highestLevel +
                    ", Highest Potions: " + highestPotions + ")";
        }
    }

    public PlayerData getPlayerData(String playerName) {
        String query = "SELECT highest_level, highest_level_x, highest_level_y, completed_game, potion_h " +
                "FROM cumulative_scores WHERE player_name = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playerName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int highestLevel = resultSet.getInt("highest_level");
                    float highestLevelX = resultSet.getFloat("highest_level_x");
                    float highestLevelY = resultSet.getFloat("highest_level_y");
                    boolean completedGame = resultSet.getInt("completed_game") == 1;
                    int highestPotions = resultSet.getInt("potion_h");
                    return new PlayerData(highestLevel, highestLevelX, highestLevelY, completedGame, highestPotions);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching player data: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Clasa pentru stocarea datelor unui jucator
    public static class PlayerData {
        private final int highestLevel;
        private final float highestLevelX;
        private final float highestLevelY;
        private final boolean completedGame;
        private final int highestPotions;

        public PlayerData(int highestLevel, float highestLevelX, float highestLevelY, boolean completedGame, int highestPotions) {
            this.highestLevel = highestLevel;
            this.highestLevelX = highestLevelX;
            this.highestLevelY = highestLevelY;
            this.completedGame = completedGame;
            this.highestPotions = highestPotions;
        }

        public int getHighestLevel() {
            return highestLevel;
        }

        public float getHighestLevelX() {
            return highestLevelX;
        }

        public float getHighestLevelY() {
            return highestLevelY;
        }

        public boolean isCompletedGame() {
            return completedGame;
        }

        public int getHighestPotions() {
            return highestPotions;
        }
    }
}