# 地下城冒險遊戲 - Code Review 報告

## 📋 目錄
1. [程式碼優化建議](#程式碼優化建議)

---
## 🔍 程式碼優化建議

### 1. **重複程式碼問題**

#### 問題 1.1: 重複的 Slash Effect 方法
**位置**: `GameInterface.java` (lines 331-369)

**問題**: `addSlashEffect()` 和 `addSlashEffect2()` 幾乎完全相同，只有使用的標籤不同。

**建議**:
```java
public void addSlashEffect(int effectIndex) {
    JLabel slashLabel = (effectIndex == 1) ? slashLabel1 : slashLabel2;
    ImageIcon slashEffect = (effectIndex == 1) ? slashEffect1 : slashEffect2;
    
    Point position = monster.getPortraitLabel().getLocation();
    slashLabel.setLocation(position.x - 160, position.y - 130);
    
    javax.swing.JComponent parent = (javax.swing.JComponent) monster.getPortraitLabel().getParent();
    parent.add(slashLabel);
    parent.setComponentZOrder(slashLabel, 1);
    slashLabel.setVisible(true);
    parent.revalidate();
    parent.repaint();
    
    Timer timer = new Timer(1000, event -> {
        slashLabel.setVisible(false);
        parent.repaint();
    });
    timer.setRepeats(false);
    timer.start();
}
```

#### 問題 1.2: 魔法卡片字串匹配邏輯
**位置**: `GameLogic.java` (lines 302-338)

**問題**: 使用 `contains()` 進行字串匹配，容易出錯且不靈活。

**建議**: 使用枚舉或常數定義卡片類型：
```java
public enum MagicCardType {
    ATTACK_SPEED("攻速", 50, p -> p.increaseAttackSpeed(100)),
    ATTACK_POWER("攻擊力", 30, p -> p.increaseAttackPower(5)),
    HEALTH("生命值", 20, p -> p.increaseMaxHealth(20)),
    CRITICAL_RATE("暴擊率", 40, p -> p.increaseCriticalRate(0.1));
    
    private final String displayName;
    private final int cost;
    private final Consumer<Player> effect;
    
    // Constructor and methods...
}
```

---

### 2. **變數命名問題**

#### 問題 2.1: 單字母變數
**位置**: `GameLogic.java` (line 176)
```java
int d = player.getCriticalAttackPower(); // ❌ 不清晰
```
**建議**:
```java
int criticalDamage = player.getCriticalAttackPower(); // ✅ 清晰
```

#### 問題 2.2: 不清晰的參數名稱
**位置**: `Monster.java` (line 139)
```java
public void takeDamage(int damage, int odamage) { // ❌ odamage 不明確
```
**建議**:
```java
public void takeDamage(int damage, int originalDamage) { // ✅ 清晰
```

---

### 3. **硬編碼數值問題**

#### 問題 3.1: 魔法數字
**位置**: `GameLogic.java` (line 210)
```java
int ultimateDamage = 9999999; // ❌ 硬編碼
```
**建議**: 定義為常數或從配置檔讀取：
```java
private static final int ULTIMATE_DAMAGE_MULTIPLIER = 3;
int ultimateDamage = player.getAttackPower() * ULTIMATE_DAMAGE_MULTIPLIER;
```

#### 問題 3.2: 多處硬編碼數值
- `BuffEffects.java`: 各種 Buff 的數值應該從 JSON 或配置檔讀取
- `Player.java`: 初始屬性值應該可配置
- `GameInterface.java`: UI 尺寸和顏色應該定義為常數

---

### 4. **資源管理問題**

#### 問題 4.1: Timer 管理不統一
**位置**: 多個類別中都有 Timer，但沒有統一管理

**問題**:
- `GameLogic`: `playerAttackTimer`, `monsterAttackTimer`
- `BuffEffects`: `poisonTimer`, `regenerationTimer`
- `ActiveBuffsPanel`: `updateTimer`
- `DamageNumber`: `timer`
- 多處使用 `new Timer()` 但沒有保存引用

**建議**: 創建 `TimerManager` 類別統一管理：
```java
public class TimerManager {
    private List<Timer> timers = new ArrayList<>();
    
    public Timer createTimer() {
        Timer timer = new Timer();
        timers.add(timer);
        return timer;
    }
    
    public void cancelAll() {
        timers.forEach(Timer::cancel);
        timers.clear();
    }
}
```

#### 問題 4.2: 音訊資源未關閉
**位置**: `GameLogic.java` (lines 200-208)

**問題**: `Clip` 資源沒有正確關閉。

**建議**:
```java
try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(sound)) {
    Clip clip = AudioSystem.getClip();
    clip.open(audioIn);
    clip.start();
    // 使用 LineListener 在播放完成後關閉
    clip.addLineListener(event -> {
        if (event.getType() == LineEvent.Type.STOP) {
            clip.close();
        }
    });
}
```

---

### 5. **JSON 解析問題**

#### 問題 5.1: 手動字串解析
**位置**: `Monster.java` (lines 67-90)

**問題**: 使用字串操作解析 JSON，容易出錯且不穩定。

**建議**: 使用 JSON 庫（如 Gson 或 Jackson）：
```java
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

private void loadMonsterData() {
    try {
        String jsonContent = new String(Files.readAllBytes(
            Paths.get("src", "game", "Monster.json")));
        
        JsonObject root = JsonParser.parseString(jsonContent).getAsJsonObject();
        JsonObject levels = root.getAsJsonObject("levels");
        
        for (int level = 1; level <= 8; level++) {
            JsonObject levelData = levels.getAsJsonObject(String.valueOf(level));
            MonsterLevel monsterLevel = new MonsterLevel();
            monsterLevel.health = levelData.get("health").getAsInt();
            // ... 其他屬性
            monsterLevels.put(level, monsterLevel);
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
    }
}
```

---

### 6. **物件導向設計問題**

#### 問題 6.1: BuffEffects 使用 Switch-Case
**位置**: `BuffEffects.java` (lines 19-46)

**問題**: 每新增一個 Buff 都需要修改 switch-case，違反開放封閉原則。

**建議**: 使用策略模式：
```java
public interface BuffStrategy {
    void apply(Player player, GameLogic gameLogic, GameInterface gameInterface);
}

public class SpeedupBuffStrategy implements BuffStrategy {
    @Override
    public void apply(Player player, GameLogic gameLogic, GameInterface gameInterface) {
        player.increaseAttackSpeed(100);
    }
}

public class BuffEffects {
    private Map<String, BuffStrategy> strategies = new HashMap<>();
    
    public BuffEffects() {
        strategies.put("speedup", new SpeedupBuffStrategy());
        strategies.put("attack", new AttackBuffStrategy());
        // ...
    }
    
    public void applyBuffEffect(String buffId) {
        BuffStrategy strategy = strategies.get(buffId);
        if (strategy != null) {
            strategy.apply(player, gameLogic, gameInterface);
        }
    }
}
```

#### 問題 6.2: GameInterface 類別過大
**位置**: `GameInterface.java` (587 lines)

**問題**: 單一類別職責過多，包含 UI 設置、動畫、事件處理等。

**建議**: 拆分為多個類別：
- `GameUIComponents`: UI 元件管理
- `AnimationManager`: 動畫效果管理
- `UILayoutManager`: 布局管理

---

### 7. **錯誤處理問題**

#### 問題 7.1: 異常處理不足
**位置**: 多處

**問題**:
- `Monster.java`: JSON 解析失敗直接 `System.exit(1)`
- `GameLogic.java`: 音訊載入失敗只打印堆疊
- 圖片載入失敗沒有處理

**建議**: 使用適當的異常處理和日誌記錄：
```java
private void loadMonsterData() {
    try {
        // ... 載入邏輯
    } catch (IOException e) {
        logger.error("無法載入怪物數據", e);
        // 使用預設值或顯示錯誤訊息
        initializeDefaultMonsterData();
    } catch (JsonParseException e) {
        logger.error("JSON 解析失敗", e);
        initializeDefaultMonsterData();
    }
}
```

---

### 8. **其他優化建議**

#### 8.1: 常數定義
建議創建 `GameConstants.java`:
```java
public class GameConstants {
    public static final int INITIAL_PLAYER_HEALTH = 100;
    public static final int INITIAL_PLAYER_ATTACK = 30;
    public static final int INITIAL_PLAYER_COINS = 100;
    public static final int MAX_LEVEL = 8;
    // ... 其他常數
}
```

#### 8.2: 配置檔案
將遊戲配置（數值、路徑等）移到配置檔，方便調整。

#### 8.3: 日誌系統
使用日誌框架（如 Log4j 或 SLF4J）替代 `System.out.println` 和 `printStackTrace`。

---

## 🎮 新功能建議

### 功能 1: 存檔系統 (Save/Load System)

#### 功能描述
允許玩家保存遊戲進度，包括：
- 當前關卡等級
- 玩家屬性（血量、攻擊力、金幣等）
- 已獲得的 Buff
- 遊戲統計數據（總擊殺數、總傷害等）

#### 實作建議

**1.1 創建 SaveData 類別**
```java
public class SaveData implements Serializable {
    private int level;
    private int playerHealth;
    private int playerMaxHealth;
    private int playerAttackPower;
    private int playerCoins;
    private double playerCriticalRate;
    private List<String> activeBuffs;
    private int totalKills;
    private long playTime;
    private Date saveDate;
    
    // Getters and Setters
}
```

**1.2 創建 SaveManager 類別**
```java
public class SaveManager {
    private static final String SAVE_DIR = "saves/";
    private static final String SAVE_FILE = "savegame.dat";
    
    public void saveGame(Player player, GameLogic gameLogic, BuffManager buffManager) {
        SaveData saveData = new SaveData();
        // 填充數據...
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_DIR + SAVE_FILE))) {
            oos.writeObject(saveData);
            JOptionPane.showMessageDialog(null, "遊戲已保存！");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "保存失敗：" + e.getMessage());
        }
    }
    
    public SaveData loadGame() {
        // 載入邏輯...
    }
}
```

**1.3 UI 整合**
- 在主選單添加「繼續遊戲」選項
- 在遊戲中按 ESC 顯示暫停選單，包含「保存遊戲」選項
- 美化存檔/讀檔介面，顯示存檔時間、關卡進度等資訊

---

### 功能 2: 商店系統 (Shop System)

#### 功能描述
在關卡之間或主選單中，玩家可以使用金幣購買永久升級：
- 永久提升基礎攻擊力
- 永久提升最大生命值
- 永久提升暴擊率
- 購買特殊道具（一次性使用）
- 解鎖新的 Buff 選項

#### 實作建議

**2.1 創建 ShopItem 類別**
```java
public class ShopItem {
    private String id;
    private String name;
    private String description;
    private int cost;
    private ShopItemType type;
    private int value; // 提升的數值
    private boolean isPermanent;
    private ImageIcon icon;
    
    public enum ShopItemType {
        ATTACK_POWER,
        MAX_HEALTH,
        CRITICAL_RATE,
        ATTACK_SPEED,
        SPECIAL_ITEM
    }
}
```

**2.2 創建 ShopPanel 類別**
```java
public class ShopPanel extends JPanel {
    private Player player;
    private List<ShopItem> availableItems;
    private JLabel coinsLabel;
    
    public ShopPanel(Player player) {
        this.player = player;
        initializeShopItems();
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));
        
        // 標題
        JLabel title = new JLabel("商店", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        
        // 金幣顯示
        coinsLabel = new JLabel("金幣: " + player.getCoins());
        coinsLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        coinsLabel.setForeground(Color.YELLOW);
        
        // 商品列表（使用 JScrollPane）
        JPanel itemsPanel = createItemsPanel();
        
        add(title, BorderLayout.NORTH);
        add(coinsLabel, BorderLayout.SOUTH);
        add(new JScrollPane(itemsPanel), BorderLayout.CENTER);
    }
    
    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 15));
        panel.setBackground(new Color(30, 30, 30));
        
        for (ShopItem item : availableItems) {
            panel.add(createItemCard(item));
        }
        
        return panel;
    }
    
    private JPanel createItemCard(ShopItem item) {
        // 創建美觀的商品卡片
        // 包含圖示、名稱、描述、價格、購買按鈕
    }
}
```

**2.3 整合到遊戲流程**
- 在 `nextLevel()` 後顯示商店選項
- 在主選單添加「商店」選項
- 商店購買的升級會永久保存到玩家數據中

**2.4 UI 美化建議**
- 使用卡片式設計，每個商品一個卡片
- 添加購買動畫效果
- 顯示「已購買」標記
- 使用漸變色和陰影效果

---

### 功能 3: 特殊技能系統 (Special Skills System)

#### 功能描述
除了現有的「終極一擊」，添加更多主動技能：
- **閃避技能**: 短時間內免疫傷害
- **連擊技能**: 快速連續攻擊多次
- **治療技能**: 立即恢復生命值
- **護盾技能**: 短時間內減少傷害
- **時間緩慢**: 減慢怪物攻擊速度

每個技能有冷卻時間，需要策略性使用。

#### 實作建議

**3.1 創建 Skill 介面和實作**
```java
public interface Skill {
    String getName();
    String getDescription();
    int getCooldown(); // 冷卻時間（秒）
    int getManaCost(); // 魔力消耗（可選）
    ImageIcon getIcon();
    void execute(Player player, Monster monster, GameLogic gameLogic);
    boolean isOnCooldown();
    int getRemainingCooldown();
}

public class DodgeSkill implements Skill {
    private long lastUsedTime = 0;
    private static final int COOLDOWN = 10; // 10秒冷卻
    
    @Override
    public void execute(Player player, Monster monster, GameLogic gameLogic) {
        if (isOnCooldown()) return;
        
        // 實現閃避邏輯
        player.setInvulnerable(true);
        lastUsedTime = System.currentTimeMillis();
        
        // 3秒後取消無敵
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                player.setInvulnerable(false);
            }
        }, 3000);
    }
    
    @Override
    public boolean isOnCooldown() {
        return (System.currentTimeMillis() - lastUsedTime) < COOLDOWN * 1000;
    }
}
```

**3.2 創建 SkillManager 類別**
```java
public class SkillManager {
    private Map<String, Skill> availableSkills = new HashMap<>();
    private List<Skill> equippedSkills = new ArrayList<>();
    
    public SkillManager() {
        initializeSkills();
    }
    
    private void initializeSkills() {
        availableSkills.put("dodge", new DodgeSkill());
        availableSkills.put("combo", new ComboSkill());
        availableSkills.put("heal", new HealSkill());
        availableSkills.put("shield", new ShieldSkill());
        availableSkills.put("slowTime", new SlowTimeSkill());
    }
    
    public void useSkill(String skillId, Player player, Monster monster, GameLogic gameLogic) {
        Skill skill = availableSkills.get(skillId);
        if (skill != null && !skill.isOnCooldown()) {
            skill.execute(player, monster, gameLogic);
        }
    }
}
```

**3.3 創建 SkillBar UI**
```java
public class SkillBar extends JPanel {
    private SkillManager skillManager;
    private List<SkillButton> skillButtons = new ArrayList<>();
    
    public SkillBar(SkillManager skillManager) {
        this.skillManager = skillManager;
        setLayout(new FlowLayout());
        setOpaque(false);
        
        // 創建技能按鈕
        for (Skill skill : skillManager.getEquippedSkills()) {
            SkillButton button = new SkillButton(skill);
            skillButtons.add(button);
            add(button);
        }
        
        // 啟動冷卻時間更新計時器
        startCooldownTimer();
    }
    
    private class SkillButton extends JButton {
        private Skill skill;
        private JLabel cooldownLabel;
        
        public SkillButton(Skill skill) {
            this.skill = skill;
            setIcon(skill.getIcon());
            setToolTipText(skill.getDescription());
            // 設置按鈕樣式...
            
            addActionListener(e -> {
                if (!skill.isOnCooldown()) {
                    skillManager.useSkill(skill.getId(), player, monster, gameLogic);
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (skill.isOnCooldown()) {
                // 繪製冷卻遮罩
                Graphics2D g2d = (Graphics2D) g;
                double cooldownPercent = (double) skill.getRemainingCooldown() / skill.getCooldown();
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), (int)(getHeight() * cooldownPercent));
                
                // 顯示剩餘時間
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                String time = String.valueOf(skill.getRemainingCooldown());
                g2d.drawString(time, getWidth()/2 - 10, getHeight()/2);
            }
        }
    }
}
```

**3.4 UI 美化建議**
- 技能按鈕使用圓形或六邊形設計
- 冷卻時顯示半透明遮罩和倒數計時
- 技能使用時添加特效動畫
- 按鍵快捷鍵支持（如 1, 2, 3, 4）
- 技能圖示使用漸變色和發光效果

**3.5 整合到遊戲**
- 在 `GameInterface` 中添加技能欄
- 在 `GameLogic` 中處理技能效果
- 在 `Player` 中添加技能相關屬性（如無敵狀態）

---

## 📊 總結

### 優先級建議

**高優先級（立即改進）**:
1. Timer 資源管理
2. JSON 解析改用庫
3. 變數命名優化
4. 錯誤處理改進

**中優先級（近期改進）**:
1. 消除重複程式碼
2. 使用策略模式重構 Buff 系統
3. 拆分過大的類別
4. 添加常數定義

**低優先級（長期優化）**:
1. 添加日誌系統
2. 配置檔案化
3. 單元測試

### 新功能實作順序建議

1. **存檔系統** - 提升遊戲體驗，讓玩家可以中斷後繼續
2. **商店系統** - 增加遊戲深度和策略性
3. **技能系統** - 增加戰鬥多樣性和趣味性

---

## 🎨 UI 美化建議（適用於所有新功能）

1. **統一設計風格**: 使用一致的顏色主題（深色背景 + 亮色強調）
2. **動畫效果**: 添加過渡動畫、按鈕懸停效果
3. **字體優化**: 使用更美觀的字體，添加文字陰影
4. **圖示系統**: 為每個功能添加圖示
5. **音效反饋**: 按鈕點擊、購買成功等音效
6. **粒子效果**: 技能使用、購買等場合的粒子特效

---

*報告生成時間: 2024*
*審查者: 資深 Java 遊戲開發者*

