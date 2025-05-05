package sk.tuke.kpi.kp.slitherlink.Service;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.tuke.kpi.kp.slitherlink.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByPlayer(String player);
}
