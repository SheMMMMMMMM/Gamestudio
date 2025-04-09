package sk.tuke.kpi.kp.slitherlink.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import sk.tuke.kpi.kp.slitherlink.Entity.Comment;

import java.util.Date;
import java.util.List;


@Transactional
public class CommentServiceJPA implements CommentService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addComment(Comment comment) throws CommentException {
        try {
            if (comment.getCommentedOn() == null) {
                comment.setCommentedOn(new Date());
            }
            entityManager.persist(comment);
        } catch (Exception e) {
            throw new CommentException("Error saving comment", e);
        }
    }
    @Override
    public List<Comment> getComments(String game) throws CommentException {
        try {
            return entityManager.createNamedQuery("Comment.getCommentsForGame", Comment.class)
                    .setParameter("game", game)
                    .getResultList();
        } catch (Exception e) {
            throw new CommentException("Error fetching comments");
        }
    }

    @Override
    public void reset() throws CommentException {
        try {
            entityManager.createNamedQuery("Comment.resetComments").executeUpdate();
        } catch (Exception e) {
            throw new CommentException("Error resetting comments");
        }
    }
}
