package sk.tuke.kpi.kp.slitherlink.Entity;


import jakarta.persistence.*;

import java.util.Map;
import java.util.List;

@Entity
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String playerName;
    private Integer level;

    @ElementCollection
    @CollectionTable(name = "completed_maps", joinColumns = @JoinColumn(name = "progress_id"))
    @MapKeyColumn(name = "map_id")
    @Column(name = "completed_map_values")
    private Map<Integer, List<Integer>> completedMaps;

    // геттери та сеттери
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Map<Integer, List<Integer>> getCompletedMaps() {
        return completedMaps;
    }

    public void setCompletedMaps(Map<Integer, List<Integer>> completedMaps) {
        this.completedMaps = completedMaps;
    }
}
