import org.telegram.telegrambots.api.objects.Message;

import java.sql.*;

public class UserBot {
    private long ChatId;
    private String FirstName;
    private String LastName;
    private String UserName;

    public StringBuilder getUserInfo(Message message) {
        StringBuilder nameOfUser = new StringBuilder();

        setChatId(message.getChatId());
        setUserName(message.getFrom().getUserName());
        setFirstName(message.getFrom().getFirstName());
        setLastName(message.getFrom().getLastName());
        if (getUserName() != null) {
            return nameOfUser.append(getUserName());
        } else {
            if (getFirstName() != null & getLastName() != null) {
                nameOfUser.append(getFirstName());
                nameOfUser.append(" ");
                nameOfUser.append(getLastName());
                return nameOfUser;
            } else {
                if (getFirstName() != null) {
                    return nameOfUser.append(getFirstName());
                } else {
                    return nameOfUser.append("Незнакомец");
                }
            }
        }
    }

    public void messageToUsers(String text) {
        Bot bot = new Bot();

        DBWorker dbWorker = new DBWorker();

        String query = "SELECT DISTINCT `ChatId` FROM `usercityinfo` WHERE 1";
        try {
            Statement statement = dbWorker.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String chatId = resultSet.getString(1);
                bot.sendMsgToAll(chatId, text);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void userInfoToBD(Message message) {
        DBWorker dbWorker = new DBWorker();
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String query = "INSERT INTO `usercityinfo`(`ChatId`, `FirstName`, `LastName`, `UserName`) VALUES (?,?,?,?)";
        connection = dbWorker.getConnection();
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, message.getChatId().toString());
            preparedStatement.setString(2, message.getFrom().getFirstName());
            preparedStatement.setString(3, message.getFrom().getLastName());
            preparedStatement.setString(4, message.getFrom().getUserName());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public void userCityToDB(Message message, String city) {
        DBWorker dbWorker = new DBWorker();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        connection = dbWorker.getConnection();

        String query = "UPDATE `usercityinfo` set `City`= ? WHERE `ChatId` = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, city);
            preparedStatement.setString(2, message.getChatId().toString());
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getUserCity(String chatId) {
        String city = "";
        DBWorker dbWorker = new DBWorker();
        String query = "SELECT `City` FROM `usercityinfo` WHERE `ChatId`=" + chatId;
        try {
            Statement statement = dbWorker.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.first();
            city = resultSet.getString(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return city;
    }

    public long getChatId() {
        return ChatId;
    }

    public void setChatId(long chatId) {
        ChatId = chatId;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
