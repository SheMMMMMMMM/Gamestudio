package sk.tuke.kpi.kp.slitherlink.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import sk.tuke.kpi.kp.slitherlink.Entity.Progress;

import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    static Optional<Progress> findByPlayerName(String playerName) {
        return Optional.empty();
    }
}
