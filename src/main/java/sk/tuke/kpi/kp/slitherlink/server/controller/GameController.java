package sk.tuke.kpi.kp.slitherlink.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import sk.tuke.kpi.kp.slitherlink.Service.UserService;
import sk.tuke.kpi.kp.slitherlink.core.Field;
import sk.tuke.kpi.kp.slitherlink.core.Maps;
import sk.tuke.kpi.kp.slitherlink.Entity.Comment;
import sk.tuke.kpi.kp.slitherlink.Entity.Rating;
import sk.tuke.kpi.kp.slitherlink.Entity.Score;

import java.util.*;

@Controller
@RequestMapping("/game")
@SessionAttributes({"field", "playerName", "difficulty", "mapIndex", "currentLevel"})
public class GameController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpServletRequest request;

    private final String apiUrl = "http://localhost:8082/api";

    @Autowired
    private UserService userService;

    // Імітація ігрового поля (замініть на вашу реальну логіку)
    private Map<String, Object> gameState = new HashMap<>();

    public GameController() {
        System.out.println("GameController ініціалізовано");
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Контролер працює!";
    }

    @GetMapping
    public String game(HttpSession session, Model model) {
        System.out.println("GET /game викликано");
        String playerName = (String) session.getAttribute("playerName");
        if (playerName == null) {
            playerName = "Гравець";
            session.setAttribute("playerName", playerName);
        }

        Field field = (Field) session.getAttribute("field");
        if (field == null) {
            model.addAttribute("showDifficulty", true);
        } else {
            model.addAttribute("showDifficulty", false);
            model.addAttribute("field", field);
        }

        System.out.println("game() викликано: currentLevel=" + session.getAttribute("currentLevel") + ", mapIndex=" + session.getAttribute("mapIndex"));
        return "game";
    }

    @GetMapping("/session-data")
    @ResponseBody
    public Map<String, Object> getSessionData(HttpSession session) {
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("currentLevel", session.getAttribute("currentLevel"));
        sessionData.put("mapIndex", session.getAttribute("mapIndex"));
        System.out.println("getSessionData() викликано: " + sessionData);
        return sessionData;
    }

    @PostMapping("/start")
    @ResponseBody
    public String startGame(@RequestParam("difficulty") int difficulty, @RequestParam("level") int level, @RequestParam("mapIndex") int mapIndex, HttpSession session) {
        int maxMaps = difficulty == 1 ? 12 : 6;
        int startIndex = difficulty == 1 ? 0 : difficulty == 2 ? 12 : 18;
        if (mapIndex < startIndex || mapIndex >= startIndex + maxMaps) {
            return "<div>Помилка: Некоректний індекс карти: " + mapIndex + " для складності: " + difficulty + "</div>";
        }

        // Сбрасываем текущее поле
        Field oldField = (Field) session.getAttribute("field");
        if (oldField != null) {
            oldField.clearMap(); // Очищаем старое поле
        }

        // Очищаем сессию
        session.removeAttribute("field");
        session.removeAttribute("difficulty");
        session.removeAttribute("mapIndex");
        session.removeAttribute("currentLevel");

        // Создаем новое поле
        Field field = generateFieldByDifficulty(difficulty, mapIndex);
        if (field == null) {
            return "<div>Помилка: Не вдалося згенерувати поле для складності: " + difficulty + ", індекс карти: " + mapIndex + "</div>";
        }

        // Обновляем сессию
        session.setAttribute("field", field);
        session.setAttribute("difficulty", difficulty);
        session.setAttribute("mapIndex", mapIndex);
        session.setAttribute("currentLevel", level);
        session.setAttribute("playerName", session.getAttribute("playerName") != null ? session.getAttribute("playerName") : "Гравець");

        return renderField(field);
    }




    @PostMapping("/restart-session")
    @ResponseBody
    public String restartSession(
            @RequestParam("difficulty") int difficulty,
            @RequestParam("level") int level,
            @RequestParam("mapIndex") int mapIndex,
            HttpSession session,
            SessionStatus status) {
        System.out.println("restart-session викликано з параметрами: difficulty=" + difficulty + ", level=" + level + ", mapIndex=" + mapIndex);

        // Перевірка правильності параметрів
        if (difficulty < 1 || difficulty > 3 || level != difficulty) {
            return "<div>Помилка: Некоректна складність або рівень: " + difficulty + "</div>";
        }

        int maxMaps = difficulty == 1 ? 12 : 6;
        int startIndex = difficulty == 1 ? 0 : difficulty == 2 ? 12 : 18;
        if (mapIndex < startIndex || mapIndex >= startIndex + maxMaps) {
            return "<div>Помилка: Некоректний індекс карти: " + mapIndex + " для складності: " + difficulty + "</div>";
        }

        // Збереження прогресу в БД перед перезапуском
        String playerName = (String) session.getAttribute("playerName");


        // Перезапуск сесії
        session.invalidate();
        session = request.getSession(true);  // Створюємо нову сесію
        status.setComplete();  // Очищаємо сесію, щоб почати нову гру

        // Оновлюємо сесію з новими даними
        session.setAttribute("playerName", playerName != null ? playerName : "Гравець");
        session.setAttribute("currentLevel", level);
        session.setAttribute("mapIndex", mapIndex);
        session.setAttribute("difficulty", difficulty);

        // Генерація нової карти
        Field field = generateFieldByDifficulty(difficulty, mapIndex);
        if (field == null) {
            return "<div>Помилка: Не вдалося згенерувати поле для складності: " + difficulty + ", індекс карти: " + mapIndex + "</div>";
        }

        session.setAttribute("field", field);

        System.out.println("restart-session завершено: currentLevel=" + session.getAttribute("currentLevel") + ", mapIndex=" + session.getAttribute("mapIndex") + ", difficulty=" + session.getAttribute("difficulty"));
        return renderField(field);
    }

    @PostMapping("/save-map")
    @ResponseBody
    public ResponseEntity<String> saveMap(
            @RequestParam("level") int level,
            @RequestParam("mapIndex") int mapIndex,
            @RequestParam("playerName") String playerName,
            HttpSession session) {
        System.out.println("save-map викликано: level=" + level + ", mapIndex=" + mapIndex + ", playerName=" + playerName);

        int difficulty;
        if (mapIndex >= 0 && mapIndex <= 11) {
            difficulty = 1;
        } else if (mapIndex >= 12 && mapIndex <= 17) {
            difficulty = 2;
        } else if (mapIndex >= 18 && mapIndex <= 23) {
            difficulty = 3;
        } else {
            return ResponseEntity.badRequest().body("Некоректний індекс карти: " + mapIndex);
        }

        if (level < 1 || level > 3 || level != difficulty) {
            return ResponseEntity.badRequest().body("Некоректний рівень: " + level);
        }

        session.setAttribute("currentLevel", level);
        session.setAttribute("mapIndex", mapIndex);
        session.setAttribute("difficulty", difficulty);

        Field field = generateFieldByDifficulty(difficulty, mapIndex);
        if (field == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Не вдалося згенерувати поле для складності: " + difficulty + ", індекс карти: " + mapIndex);
        }

        session.setAttribute("field", field);

        System.out.println("Мапа збережена: currentLevel=" + session.getAttribute("currentLevel") + ", mapIndex=" + session.getAttribute("mapIndex") + ", difficulty=" + session.getAttribute("difficulty"));
        return ResponseEntity.ok("Мапа успішно збережена.");
    }

    @PostMapping("/draw/{row}/{col}/{direction}")
    @ResponseBody
    public String drawLine(
            @PathVariable int row,
            @PathVariable int col,
            @PathVariable String direction,
            HttpSession session,
            @RequestParam(value = "elapsedTime", required = false) Integer elapsedTime) {
        Integer difficulty = (Integer) session.getAttribute("difficulty");
        Integer mapIndex = (Integer) session.getAttribute("mapIndex");
        Integer level = (Integer) session.getAttribute("currentLevel");
        System.out.println("Сесійні атрибути: difficulty=" + difficulty + ", mapIndex=" + mapIndex + ", level=" + level);

        if (difficulty == null || mapIndex == null || level == null) {
            System.out.println("Помилка: difficulty, mapIndex або level не встановлені.");
            return "<div>Помилка: Складність гри, індекс карти або рівень не встановлено. Будь ласка, збережіть мапу.</div>";
        }

        System.out.println("drawLine викликано: difficulty=" + difficulty + ", mapIndex=" + mapIndex + ", row=" + row + ", col=" + col + ", direction=" + direction);

        Field field = (Field) session.getAttribute("field");
        if (field == null || field.getMapIndex() != mapIndex) {
            System.out.println("Поле не відповідає поточному mapIndex або відсутнє. Перегенеровуємо поле...");
            field = generateFieldByDifficulty(difficulty, mapIndex);
            if (field == null) {
                return "<div>Помилка: Не вдалося згенерувати поле для складності: " + difficulty + ", індекс карти: " + mapIndex + "</div>";
            }
            session.setAttribute("field", field);
        }

        if ("horizontal".equals(direction)) {
            if (row < 0 || row > field.getRows()) {
                return "<div>Помилка: Некоректний рядок для горизонтальної лінії: " + row + "</div>";
            }
            if (col < 0 || col >= field.getCols()) {
                return "<div>Помилка: Некоректний стовпець для горизонтальної лінії: " + col + "</div>";
            }
        } else if ("vertical".equals(direction)) {
            if (row < 0 || row >= field.getRows()) {
                return "<div>Помилка: Некоректний рядок для вертикальної лінії: " + row + "</div>";
            }
            if (col < 0 || col > field.getCols()) {
                return "<div>Помилка: Некоректний стовпець для вертикальної лінії: " + col + "</div>";
            }
        } else {
            return "<div>Помилка: Некоректний напрямок: " + direction + "</div>";
        }

        try {
            boolean success = field.drawLine(row, col, direction);
            if (!success) {
                return "<div>Помилка: Лінія не намальована. Вона може вже існувати.</div>";
            }

            if (field.checkVictory(123)) {
                System.out.println("Перемога досягнута!");
                if (elapsedTime != null) {
                    saveScore(session, elapsedTime);
                }
                int elapsedSeconds = elapsedTime != null ? elapsedTime : 0;
                String finalTime = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60);
                return "<script>startVictoryAnimation('" + finalTime + "');</script>";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "<div>Помилка: Виникла внутрішня помилка сервера під час малювання лінії: " + e.getMessage() + "</div>";
        }

        session.setAttribute("field", field);
        return renderField(field);
    }

    @PostMapping("/save-progress")
    @ResponseBody
    public Map<String, String> saveProgress(
            @RequestBody Map<String, Object> progressData,
            HttpSession session) {
        String playerName = (String) progressData.get("playerName");
        Integer level = Integer.parseInt(progressData.get("level").toString());
        Map<String, Object> completedMapsObj = (Map<String, Object>) progressData.get("completedMaps");
        Map<Integer, List<Integer>> completedMaps = new HashMap<>();

        for (Map.Entry<String, Object> entry : completedMapsObj.entrySet()) {
            Integer lvl = Integer.parseInt(entry.getKey());
            List<Integer> maps = new ArrayList<>();
            for (Object map : (List<?>) entry.getValue()) {
                maps.add(Integer.parseInt(map.toString()));
            }
            completedMaps.put(lvl, maps);
        }

        System.out.println("Збереження прогресу: playerName=" + playerName + ", level=" + level + ", completedMaps=" + completedMaps);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    apiUrl + "/progress/save?playerName=" + playerName,
                    new HashMap<String, Object>() {{
                        put("level", level);
                        put("completedMaps", completedMaps);
                    }},
                    Void.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("API повернув помилку: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Помилка при збереженні прогресу: " + e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Помилка при збереженні прогресу: " + e.getMessage());
            return response;
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Прогрес збережено");
        return response;
    }

    @GetMapping("/load-progress")
    @ResponseBody
    public Map<String, Object> loadProgress(
            @RequestParam("playerName") String playerName,
            HttpSession session) {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    apiUrl + "/progress?playerName=" + playerName, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("API повернув помилку: " + response.getStatusCode());
            }

            Map<String, Object> progressMap = response.getBody();
            if (progressMap == null) {
                progressMap = new HashMap<>();
                progressMap.put("level", 1);
                progressMap.put("completedMaps", new HashMap<>());
                restTemplate.postForEntity(apiUrl + "/progress/save?playerName=" + playerName, progressMap, Void.class);
            }

            Map<Integer, List<Integer>> completedMaps = new HashMap<>();
            Map<String, Object> rawCompletedMaps = (Map<String, Object>) progressMap.get("completedMaps");
            if (rawCompletedMaps != null) {
                for (Map.Entry<String, Object> entry : rawCompletedMaps.entrySet()) {
                    Integer lvl = Integer.parseInt(entry.getKey());
                    List<Integer> maps = new ArrayList<>();
                    for (Object map : (List<?>) entry.getValue()) {
                        maps.add(Integer.parseInt(map.toString()));
                    }
                    completedMaps.put(lvl, maps);
                }
            }

            Map<String, Object> progress = new HashMap<>();
            progress.put("level", progressMap.getOrDefault("level", 1));
            progress.put("completedMaps", completedMaps);
            System.out.println("Завантаження прогресу для " + playerName + ": " + progress);
            return progress;
        } catch (Exception e) {
            System.out.println("Помилка при завантаженні прогресу: " + e.getMessage());
            Map<String, Object> progress = new HashMap<>();
            progress.put("level", 1);
            progress.put("completedMaps", new HashMap<>());
            return progress;
        }
    }

    @PostMapping("/reset")
    @ResponseBody
    public String resetGame(HttpSession session) {
        Field field = (Field) session.getAttribute("field");
        if (field == null) {
            return "<div>Помилка: Гру не ініціалізовано.</div>";
        }
        field.clearMap();
        session.setAttribute("field", field);
        return renderField(field);
    }

    @GetMapping("/exit")
    public String exitGame(HttpSession session) {
        session.removeAttribute("field");
        session.removeAttribute("difficulty");
        session.removeAttribute("mapIndex");
        session.removeAttribute("currentLevel");
        return "redirect:/home";
    }

    @GetMapping("/field")
    @ResponseBody
    public String getField(HttpSession session) {
        Integer difficulty = (Integer) session.getAttribute("difficulty");
        Integer mapIndex = (Integer) session.getAttribute("mapIndex");
        Integer currentLevel = (Integer) session.getAttribute("currentLevel");

        if (difficulty == null || mapIndex == null || currentLevel == null) {
            return "<div>Помилка: Сесія не ініціалізована. Спробуйте перезапустити гру.</div>";
        }

        Field field = (Field) session.getAttribute("field");
        if (field == null || field.getMapIndex() != mapIndex) {
            field = generateFieldByDifficulty(difficulty, mapIndex);
            if (field == null) {
                return "<div>Помилка: Не вдалося згенерувати поле.</div>";
            }
            session.setAttribute("field", field);
        }

        return renderField(field);
    }

    @PostMapping("/remove/{row}/{col}/{direction}")
    @ResponseBody
    public String removeLine(
            @PathVariable int row,
            @PathVariable int col,
            @PathVariable String direction,
            HttpSession session) {
        Integer difficulty = (Integer) session.getAttribute("difficulty");
        Integer mapIndex = (Integer) session.getAttribute("mapIndex");
        if (difficulty == null || mapIndex == null) {
            return "<div>Помилка: Складність гри або індекс карти не встановлено. Будь ласка, збережіть мапу.</div>";
        }

        Field field = (Field) session.getAttribute("field");
        if (field == null || field.getMapIndex() != mapIndex) {
            field = generateFieldByDifficulty(difficulty, mapIndex);
            if (field == null) {
                return "<div>Помилка: Не вдалося згенерувати поле для складності: " + difficulty + ", індекс карти: " + mapIndex + "</div>";
            }
            session.setAttribute("field", field);
        }

        if ("horizontal".equals(direction)) {
            if (row < 0 || row > field.getRows()) {
                return "<div>Помилка: Некоректний рядок для горизонтальної лінії: " + row + "</div>";
            }
            if (col < 0 || col >= field.getCols()) {
                return "<div>Помилка: Некоректний стовпець для горизонтальної лінії: " + col + "</div>";
            }
        } else if ("vertical".equals(direction)) {
            if (row < 0 || row >= field.getRows()) {
                return "<div>Помилка: Некоректний рядок для вертикальної лінії: " + row + "</div>";
            }
            if (col < 0 || col > field.getCols()) {
                return "<div>Помилка: Некоректний стовпець для вертикальної лінії: " + col + "</div>";
            }
        } else {
            return "<div>Помилка: Некоректний напрямок: " + direction + "</div>";
        }

        field.removeLine(row, col, direction);
        session.setAttribute("field", field);
        return renderField(field);
    }

    @PostMapping("/block/{row}/{col}/{direction}")
    @ResponseBody
    public String blockLine(
            @PathVariable int row,
            @PathVariable int col,
            @PathVariable String direction,
            HttpSession session) {
        Integer difficulty = (Integer) session.getAttribute("difficulty");
        Integer mapIndex = (Integer) session.getAttribute("mapIndex");
        if (difficulty == null || mapIndex == null) {
            return "<div>Помилка: Складність гри або індекс карти не встановлено. Будь ласка, збережіть мапу.</div>";
        }

        Field field = (Field) session.getAttribute("field");
        if (field == null || field.getMapIndex() != mapIndex) {
            field = generateFieldByDifficulty(difficulty, mapIndex);
            if (field == null) {
                return "<div>Помилка: Не вдалося згенерувати поле для складності: " + difficulty + ", індекс карти: " + mapIndex + "</div>";
            }
            session.setAttribute("field", field);
        }

        if ("horizontal".equals(direction)) {
            if (row < 0 || row > field.getRows()) {
                return "<div>Помилка: Некоректний рядок для горизонтальної лінії: " + row + "</div>";
            }
            if (col < 0 || col >= field.getCols()) {
                return "<div>Помилка: Некоректний стовпець для горизонтальної лінії: " + col + "</div>";
            }
        } else if ("vertical".equals(direction)) {
            if (row < 0 || row >= field.getRows()) {
                return "<div>Помилка: Некоректний рядок для вертикальної лінії: " + row + "</div>";
            }
            if (col < 0 || col > field.getCols()) {
                return "<div>Помилка: Некоректний стовпець для вертикальної лінії: " + col + "</div>";
            }
        } else {
            return "<div>Помилка: Некоректний напрямок: " + direction + "</div>";
        }

        field.blockLine(row, col, direction);
        session.setAttribute("field", field);
        return renderField(field);
    }

    @GetMapping("/reset")
    public String resetGameGet() {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "GET метод не підтримується для /game/reset. Використовуйте POST.");
    }

    @PostMapping("/comment")
    @ResponseBody
    public ResponseEntity<?> addComment(
            @RequestParam("comment") String commentText,
            HttpSession session) {
        String playerName = (String) session.getAttribute("playerName");
        Comment comment = new Comment("Slitherlink", playerName, commentText, new Date());
        restTemplate.postForEntity(apiUrl + "/comments", comment, Comment.class);
        return ResponseEntity.ok("Коментар додано.");
    }

    @PostMapping("/rate")
    @ResponseBody
    public ResponseEntity<?> rateGame(
            @RequestParam("rating") int ratingValue,
            HttpSession session) {
        try {
            String playerName = (String) session.getAttribute("playerName");
            System.out.println("Player name from session: " + playerName);
            if (playerName == null || playerName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Ім’я гравця не знайдено в сесії.");
            }
            if (ratingValue < 1 || ratingValue > 5) {
                return ResponseEntity.badRequest().body("Рейтинг має бути від 1 до 5.");
            }
            Rating rating = new Rating("Slitherlink", playerName, ratingValue, new Date());
            System.out.println("Rating object: " + rating);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    apiUrl + "/ratings", rating, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Помилка API: " + response.getBody());
            }
            return ResponseEntity.ok("Оцінку додано.");
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body("Помилка при додаванні оцінки: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Помилка при додаванні оцінки: " + e.getMessage());
        }
    }

    private void saveScore(HttpSession session, int elapsedSeconds) {
        String playerName = (String) session.getAttribute("playerName");
        Score score = new Score("Slitherlink", playerName, elapsedSeconds, new Date());
        restTemplate.postForEntity(apiUrl + "/score", score, Score.class);
    }

    private Field generateFieldByDifficulty(int difficulty, Integer mapIndex) {
        System.out.println("generateFieldByDifficulty викликано: difficulty=" + difficulty + ", mapIndex=" + mapIndex);

        int startIndex, endIndex;
        if (difficulty == 1) {
            startIndex = 0;
            endIndex = 11;
        } else if (difficulty == 2) {
            startIndex = 12;
            endIndex = 17;
        } else if (difficulty == 3) {
            startIndex = 18;
            endIndex = 23;
        } else {
            System.out.println("Некоректна складність: " + difficulty);
            return null;
        }

        if (mapIndex < startIndex || mapIndex > endIndex) {
            System.out.println("mapIndex " + mapIndex + " виходить за межі для складності " + difficulty + " [" + startIndex + "-" + endIndex + "]");
            return null;
        }

        int selectedMapIndex = mapIndex;
        if (difficulty == 2) selectedMapIndex -= 12;
        else if (difficulty == 3) selectedMapIndex -= 18;

        String[][] map = Maps.getMap(difficulty, selectedMapIndex);
        if (map == null) {
            System.out.println("Не вдалося завантажити карту для складності: " + difficulty + ", індекс карти: " + selectedMapIndex);
            return null;
        }

        String[][] horizontalLines = new String[Maps.HorizontalLines[0].length][Maps.HorizontalLines[0][0].length];
        String[][] verticalLines = new String[Maps.VerticalLines[0].length][Maps.VerticalLines[0][0].length];
        for (int i = 0; i < horizontalLines.length; i++) {
            for (int j = 0; j < horizontalLines[i].length; j++) {
                horizontalLines[i][j] = Maps.HorizontalLines[0][i][j];
            }
        }
        for (int i = 0; i < verticalLines.length; i++) {
            for (int j = 0; j < verticalLines[i].length; j++) {
                verticalLines[i][j] = Maps.VerticalLines[0][i][j];
            }
        }

        Field field = new Field(map.length, map[0].length, horizontalLines, verticalLines);
        field.setMapIndex(mapIndex);
        field.setDifficulty(difficulty);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                field.setCell(i, j, map[i][j].equals("•") ? "" : map[i][j]);
            }
        }
        System.out.println("Згенеровано поле для складності: " + difficulty + ", індекс карти: " + mapIndex);
        return field;
    }

    private String renderField(Field field) {
        StringBuilder html = new StringBuilder();
        html.append("<div>");

        html.append("<div style='margin-left: 5px;'>");
        for (int j = 0; j < field.getCols(); j++) {
            html.append(String.format("<span class='col-number'>%d</span>", j + 1));
        }
        html.append("</div>");

        html.append("<div style='margin-left: 5px;'>");
        for (int j = 0; j < field.getCols(); j++) {
            String line = field.getHorizontalLines()[0][j];
            boolean isActive = line.contains("\033[95m");
            boolean isBlocked = line.contains("\033[91m");
            String className = isActive ? "active" : isBlocked ? "line-blocked" : "";
            String content = isActive ? "──" : isBlocked ? "××" : "  ";
            html.append("<span class='dot'>•</span>");
            html.append(String.format("<span class='line-horizontal %s' data-id='%d-%d-horizontal' data-content='%s'></span>", className, 0, j, content));
        }
        html.append("<span class='dot'>•</span></div>");

        for (int i = 0; i < field.getRows(); i++) {
            html.append("<div>");
            html.append(String.format("<span class='row-number'>%d</span>", i + 1));

            for (int j = 0; j < field.getCols(); j++) {
                String vLine = field.getVerticalLines()[i][j];
                boolean isVActive = vLine.contains("\033[95m");
                boolean isVBlocked = vLine.contains("\033[91m");
                String vClassName = isVActive ? "active" : isVBlocked ? "line-blocked" : "";
                String vContent = isVActive ? "│" : isVBlocked ? "×" : " ";
                String cellValue = field.getFieldValues()[i][j] == null || field.getFieldValues()[i][j].equals("•") ? "" : field.getFieldValues()[i][j];
                html.append(String.format("<span class='line-vertical %s' data-id='%d-%d-vertical' data-content='%s'></span>", vClassName, i, j, vContent));
                html.append(String.format("<span class='cell'>%s</span>", cellValue));
            }

            String lastVLine = field.getVerticalLines()[i][field.getCols()];
            boolean isLastVActive = lastVLine.contains("\033[95m");
            boolean isLastVBlocked = lastVLine.contains("\033[91m");
            String lastVClassName = isLastVActive ? "active" : isLastVBlocked ? "line-blocked" : "";
            String lastVContent = isLastVActive ? "│" : isLastVBlocked ? "×" : " ";
            html.append(String.format("<span class='line-vertical %s' data-id='%d-%d-vertical' data-content='%s'></span>", lastVClassName, i, field.getCols(), lastVContent));
            html.append("</div>");

            if (i < field.getRows()) {
                html.append("<div style='margin-left: 5px;'>");
                for (int j = 0; j < field.getCols(); j++) {
                    String line = field.getHorizontalLines()[i + 1][j];
                    boolean isActive = line.contains("\033[95m");
                    boolean isBlocked = line.contains("\033[91m");
                    String className = isActive ? "active" : isBlocked ? "line-blocked" : "";
                    String content = isActive ? "──" : isBlocked ? "××" : "  ";
                    html.append("<span class='dot'>•</span>");
                    html.append(String.format("<span class='line-horizontal %s' data-id='%d-%d-horizontal' data-content='%s'></span>", className, i + 1, j, content));
                }
                html.append("<span class='dot'>•</span></div>");
            }
        }

        html.append("</div>");
        System.out.println("Відображений ігровий простір: " + html);
        return html.toString();
    }
}