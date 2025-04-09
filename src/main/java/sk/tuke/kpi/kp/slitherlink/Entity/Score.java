package sk.tuke.kpi.kp.slitherlink.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.NamedQuery;

import java.io.Serializable;
import java.util.Date;

@Entity
@NamedQuery(
        name = "Score.getTopScores",
        query = "SELECT s FROM Score s WHERE s.game = :game ORDER BY s.points DESC"
)
@NamedQuery(
        name = "Score.resetScores",
        query = "DELETE FROM Score"
)
public class Score implements Serializable {
    @Id
    @GeneratedValue
    private int ident;

    private String game;
    private String player;
    private int points;

    @Column(name = "playedon")
    @Temporal(TemporalType.TIMESTAMP) // Це важливо для правильного збереження дати
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date playedOn;

    public Score(String game, String player, int points, Date playedOn) {
        this.game = game;
        this.player = player;
        this.points = points;
        this.playedOn = playedOn;
    }

    public Score() {}

    public int getIdent() {return ident;}

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getPlayer() {return player;}

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getPlayedOn() {
        return playedOn;
    }

    public void setPlayedOn(Date playedOn) {
        this.playedOn = playedOn;
    }

    @Override
    public String toString() {
        return "Score{" +
                "game='" + game + '\'' +
                ", player='" + player + '\'' +
                ", points=" + points +
                ", playedOn=" + playedOn +
                '}';
    }
}
