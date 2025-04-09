package sk.tuke.kpi.kp.slitherlink.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;


import java.io.Serializable;
import java.util.Date;
@Entity
@NamedQuery(
        name = "Comment.getCommentsForGame",
        query = "SELECT c FROM Comment c WHERE c.game = :game ORDER BY c.commentedOn DESC"
)
@NamedQuery(
        name = "Comment.resetComments",
        query = "DELETE FROM Comment"
)

public class Comment implements Serializable {
    @Id
    @GeneratedValue
    private int ident;

    private String game;
    private String player;
    private String comment;

    @Column(name = "commentedon")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS") // Вказуємо формат дати
    private Date commentedOn;

    public Comment(String game, String player, String comment, Date commentedOn) {
        this.game = game;
        this.player = player;
        this.comment = comment;
        this.commentedOn = commentedOn;
    }

    public Comment() {}

    public int getIdent() {return ident;}

    public void setIdent(int ident) {this.ident = ident;}

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCommentedOn() {
        return commentedOn;
    }

    public void setCommentedOn(Date commentedOn) {
        this.commentedOn = commentedOn;
    }
    public Date getDate() {
        return commentedOn;
    }
    public void setDate(Date commentedOn) {
        this.commentedOn = commentedOn;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "game='" + game + '\'' +
                ", player='" + player + '\'' +
                ", comment='" + comment + '\'' +
                ", commentedOn=" + commentedOn +
                '}';
    }
}
