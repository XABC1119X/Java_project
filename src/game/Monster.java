package src.game;

import java.awt.Point;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Monster {
    private int health;
    private int maxHealth;
    private int attackPower;
    private int ultimatePower;
    private int attackSpeed;
    private int level;
    private Map<Integer, MonsterLevel> monsterLevels;

    private ImageIcon normalPortrait;
    private ImageIcon defeatPortrait;
    private JLabel portraitLabel;

    // 內部類來存儲每個等級的數據
    private static class MonsterLevel {
        int health;
        int attackPower;
        int ultimatePower;
        int attackSpeed;
        String normalImage;
        String defeatImage;
    }

    public Monster() {
        loadMonsterData();
        initializeLevel(1);
    }
    
    private void loadMonsterData() {
        monsterLevels = new HashMap<>();
        try {
            String jsonContent = new String(Files.readAllBytes(
                Paths.get("src", "game", "Monster.json")));
            
            // 簡單的JSON解析，尋找關鍵字並提取數值
            for (int level = 1; level <= 8; level++) {
                MonsterLevel levelData = new MonsterLevel();
                String levelSection = extractLevelSection(jsonContent, level);
                
                levelData.health = extractIntValue(levelSection, "health");
                levelData.attackPower = extractIntValue(levelSection, "attackPower");
                levelData.ultimatePower = extractIntValue(levelSection, "ultimatePower");
                levelData.attackSpeed = extractIntValue(levelSection, "attackSpeed");
                levelData.normalImage = extractStringValue(levelSection, "normal");
                levelData.defeatImage = extractStringValue(levelSection, "defeat");
                
                monsterLevels.put(level, levelData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // 從JSON字符串中提取特定等級的部分
    private String extractLevelSection(String json, int level) {
        int start = json.indexOf("\"" + level + "\"");
        int end = json.indexOf("}", start);
        return json.substring(start, end);
    }

    // 從JSON字符串中提取整數值
    private int extractIntValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        start = json.indexOf(":", start) + 1;
        int end = json.indexOf(",", start);
        if (end == -1) end = json.indexOf("}", start);
        return Integer.parseInt(json.substring(start, end).trim());
    }

    // 從JSON字符串中提取字符串值
    private String extractStringValue(String json, String key) {
        int start = json.indexOf("\"" + key + "\"");
        start = json.indexOf(":", start) + 1;
        start = json.indexOf("\"", start) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end).trim();
    }

    private void initializeLevel(int level) {
        MonsterLevel levelData = monsterLevels.get(level);
        this.level = level;
        this.health = levelData.health;
        this.maxHealth = levelData.health;
        this.attackPower = levelData.attackPower;
        this.ultimatePower = levelData.ultimatePower;
        this.attackSpeed = levelData.attackSpeed;

        // 加載圖片
        normalPortrait = new ImageIcon(levelData.normalImage);
        defeatPortrait = new ImageIcon(levelData.defeatImage);

        if (portraitLabel == null) {
            portraitLabel = new JLabel(normalPortrait);
            portraitLabel.setBounds(450, 100, 300, 350);
        } else {
            portraitLabel.setIcon(normalPortrait);
        }
    }

    public int getDropCoins() {
        return 10 * level; // Drops more coins in higher levels
    }
    
    private void moveMonster(int deltaX) {
        Point position = portraitLabel.getLocation();
        portraitLabel.setLocation(position.x + deltaX, position.y);
    }

    public void showAttackAnimation() {
        moveMonster(-150); 
        
        // Reset after delay
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                moveMonster(150);
            }
        }, 500);
    }
    public void showDefeatAnimation() {
        portraitLabel.setIcon(defeatPortrait);
    }

    
    
    public void takeDamage(int damage , int odamage) {
        this.health = Math.max(0, this.health - damage);
        boolean isCritical = (damage / 2) >= odamage;
    
        // 創建並顯示傷害數字
        DamageNumber damageNumber = new DamageNumber(damage, isCritical, portraitLabel.getParent());
        damageNumber.setStartPosition(
        portraitLabel.getX() + portraitLabel.getWidth() / 2,
        portraitLabel.getY() + portraitLabel.getHeight() / 2
        );
        portraitLabel.getParent().add(damageNumber);
        portraitLabel.getParent().setComponentZOrder(damageNumber, 0);
    }

    public void upgradeForNewLevel(int newLevel) {
        initializeLevel(newLevel);
    }

    // Getters
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttackPower() { return attackPower; }
    public int getUltimatePower() { return ultimatePower; }
    public int getAttackSpeed() { return attackSpeed; }
    public int getLevel() { return level; }
    public JLabel getPortraitLabel() {
        return portraitLabel;
    }
}