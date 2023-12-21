package org.shroom;

import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        var charId = 0;

        var createSTMT = """
                CREATE TABLE IF NOT EXISTS `newyear` (
                    `id`  INTEGER PRIMARY KEY,
                    `senderid` int(10) NOT NULL DEFAULT -1,
                    `sendername` varchar(13) DEFAULT '',
                    `receiverid` int(10) NOT NULL DEFAULT -1,
                    `receivername` varchar(13) DEFAULT '',
                    `message` varchar(120) DEFAULT '',
                    `senderdiscard` tinyint(1) NOT NULL DEFAULT 0,
                    `receiverdiscard` tinyint(1) NOT NULL DEFAULT 0,
                    `received` tinyint(1) NOT NULL DEFAULT 0,
                    `timesent` bigint(20) NOT NULL,
                    `timereceived` bigint(20) NOT NULL
                )
                    """;

        try (var db = DatabaseProvider.getDB()) {
            db.createTable(createSTMT);

            var newyear = new NewYearRecord(1337, "abc", 0, "me", "you");
            var nyr_1 = db.insertOne(
                    "INSERT INTO newyear(senderid, sendername, receiverid, receivername, message, senderdiscard, receiverdiscard, received, timesent, timereceived) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    ps -> {
                        ps.setInt(1, newyear.senderId);
                        ps.setString(2, newyear.senderName);
                        ps.setInt(3, newyear.receiverId);
                        ps.setString(4, newyear.receiverName);

                        ps.setString(5, newyear.stringContent);

                        ps.setBoolean(6, newyear.senderDiscardCard);
                        ps.setBoolean(7, newyear.receiverDiscardCard);
                        ps.setBoolean(8, newyear.receiverReceivedCard);

                        ps.setLong(9, newyear.dateSent);
                        ps.setLong(10, newyear.dateReceived);
                    });

            var nyrs = db.selectMulti("SELECT * FROM newyear WHERE senderid = ? OR receiverid = ?",
                    (ps) -> {
                        ps.setInt(1, charId);
                        ps.setInt(2, charId);
                    },
                    (record) -> {
                        return new NewYearRecord(
                                record.getInt("senderid"),
                                record.getString("sendername"),
                                record.getInt("receiverid"),
                                record.getString("receivername"),
                                record.getString("message"));
                    });
            System.err.println(nyrs.size());

            db.delete("DELETE FROM newyear WHERE id = ?",
                    (ps) -> {
                        ps.setInt(1, nyr_1);
                    });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
