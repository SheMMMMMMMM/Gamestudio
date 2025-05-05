package sk.tuke.kpi.kp.slitherlink.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.tuke.kpi.kp.slitherlink.Entity.Progress;
import sk.tuke.kpi.kp.slitherlink.Entity.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String apiUrl = "http://localhost:8082/api";

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean userExists(String player) {
        return userRepository.findByPlayer(player) != null;
    }

    public void registerUser(String player, String password) {
        System.out.println("Реєстрація користувача: " + player);
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setPlayer(player);
        user.setPassword(encodedPassword);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        System.out.println("Користувача " + player + " успішно зареєстровано");
    }

    public boolean authenticate(String username, String password) {
        User user = userRepository.findByPlayer(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        return false;
    }

    public void updateProgress(String player, Integer level, Map<Integer, List<Integer>> completedMaps) {
        System.out.println("Оновлення прогресу: player=" + player + ", level=" + level + ", completedMaps=" + completedMaps);

        // Отримуємо поточний прогрес гравця
        Progress progress = ProgressRepository.findByPlayerName(player).orElse(new Progress());

        // Оновлюємо рівень
        progress.setLevel(level);

        // Оновлюємо completedMaps для рівня
        Map<Integer, List<Integer>> currentMaps = progress.getCompletedMaps();
        for (Map.Entry<Integer, List<Integer>> entry : completedMaps.entrySet()) {
            // Якщо рівень ще не існує в мапі, створюємо його
            currentMaps.putIfAbsent(entry.getKey(), new ArrayList<>());
            // Додаємо нові мапи до існуючих
            currentMaps.get(entry.getKey()).addAll(entry.getValue());
        }

        // Оновлюємо мапу
        progress.setCompletedMaps(currentMaps);

        // Зберігаємо оновлений прогрес у базі даних
        progressRepository.save(progress);
        System.out.println("Прогрес для гравця " + player + " успішно оновлено");
    }

    public Map<String, Object> getProgress(String player) {
        System.out.println("Завантаження прогресу для користувача: " + player);
        try {
            Optional<Progress> progressOpt = ProgressRepository.findByPlayerName(player);
            if (progressOpt.isPresent()) {
                Progress progress = progressOpt.get();
                Map<String, Object> progressMap = new HashMap<>();
                progressMap.put("level", progress.getLevel());
                progressMap.put("completedMaps", progress.getCompletedMaps());
                return progressMap;
            } else {
                Map<String, Object> defaultProgress = new HashMap<>();
                defaultProgress.put("level", 1);
                defaultProgress.put("completedMaps", new HashMap<>());
                return defaultProgress;
            }
        } catch (Exception e) {
            System.out.println("Помилка при завантаженні прогресу: " + e.getMessage());
            Map<String, Object> errorProgress = new HashMap<>();
            errorProgress.put("level", 1);
            errorProgress.put("completedMaps", new HashMap<>());
            return errorProgress;
        }
    }

    public User getUserByPlayer(String player) {
        return userRepository.findByPlayer(player);
    }
}
