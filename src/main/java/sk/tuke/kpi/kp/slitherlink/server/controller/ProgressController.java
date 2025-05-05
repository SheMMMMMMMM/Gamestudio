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

    @PostMapping("/save")
    public ResponseEntity<Void> saveProgress(
            @RequestParam String playerName,
            @RequestBody Map<String, Object> progressData) {

        Integer level = Integer.parseInt(progressData.get("level").toString());
        Map<String, List<Integer>> completedMapsRaw = (Map<String, List<Integer>>) progressData.get("completedMaps");
        Map<Integer, List<Integer>> completedMaps = new HashMap<>();

        for (Map.Entry<String, List<Integer>> entry : completedMapsRaw.entrySet()) {
            completedMaps.put(Integer.parseInt(entry.getKey()), entry.getValue());
        }

        Optional<Progress> existingProgressOpt = ProgressRepository.findByPlayerName(playerName);
        Progress progress;

        if (existingProgressOpt.isPresent()) {
            progress = existingProgressOpt.get();
            progress.setLevel(level);

            Map<Integer, List<Integer>> currentMaps = progress.getCompletedMaps();
            for (Map.Entry<Integer, List<Integer>> entry : completedMaps.entrySet()) {
                currentMaps.putIfAbsent(entry.getKey(), new ArrayList<>());
                currentMaps.get(entry.getKey()).addAll(entry.getValue());
            }
            progress.setCompletedMaps(currentMaps);

            progressRepository.save(progress);
        } else {
            progress = new Progress();
            progress.setPlayerName(playerName);
            progress.setLevel(level);
            progress.setCompletedMaps(completedMaps);

            progressRepository.save(progress);
        }

        return ResponseEntity.ok().build();
    }

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
