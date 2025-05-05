package sk.tuke.kpi.kp.slitherlink.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.tuke.kpi.kp.slitherlink.Entity.Progress;
import sk.tuke.kpi.kp.slitherlink.Service.ProgressRepository;

import java.util.*;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressRepository progressRepository;

    // Метод для збереження прогресу
    @PostMapping("/save")
    public ResponseEntity<Void> saveProgress(
            @RequestParam String playerName,
            @RequestBody Map<String, Object> progressData) {

        Integer level = Integer.parseInt(progressData.get("level").toString());
        Map<String, List<Integer>> completedMapsRaw = (Map<String, List<Integer>>) progressData.get("completedMaps");
        Map<Integer, List<Integer>> completedMaps = new HashMap<>();

        // Перетворення raw-даних в структуру мапи
        for (Map.Entry<String, List<Integer>> entry : completedMapsRaw.entrySet()) {
            completedMaps.put(Integer.parseInt(entry.getKey()), entry.getValue());
        }

        // Перевіряємо, чи є прогрес для цього гравця
        Optional<Progress> existingProgressOpt = ProgressRepository.findByPlayerName(playerName);
        Progress progress;

        if (existingProgressOpt.isPresent()) {
            // Якщо прогрес для гравця вже існує, оновлюємо його
            progress = existingProgressOpt.get();
            progress.setLevel(level);

            // Оновлюємо completedMaps
            Map<Integer, List<Integer>> currentMaps = progress.getCompletedMaps();
            for (Map.Entry<Integer, List<Integer>> entry : completedMaps.entrySet()) {
                currentMaps.putIfAbsent(entry.getKey(), new ArrayList<>());
                currentMaps.get(entry.getKey()).addAll(entry.getValue()); // Додаємо нові мапи
            }
            progress.setCompletedMaps(currentMaps);

            // Оновлюємо прогрес у базі даних
            progressRepository.save(progress);
        } else {
            // Якщо прогрес відсутній, створюємо новий запис
            progress = new Progress();
            progress.setPlayerName(playerName);
            progress.setLevel(level);
            progress.setCompletedMaps(completedMaps);

            // Вставляємо новий запис у базу даних
            progressRepository.save(progress);
        }

        return ResponseEntity.ok().build();
    }

    // Метод для отримання прогресу
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProgress(@RequestParam String playerName) {
        Optional<Progress> progressOpt = ProgressRepository.findByPlayerName(playerName);
        Map<String, Object> progressMap = new HashMap<>();
        if (progressOpt.isPresent()) {
            Progress progress = progressOpt.get();
            progressMap.put("level", progress.getLevel());
            progressMap.put("completedMaps", progress.getCompletedMaps());
        } else {
            progressMap.put("level", 1);
            progressMap.put("completedMaps", new HashMap<>());
        }
        return ResponseEntity.ok(progressMap);
    }
}
