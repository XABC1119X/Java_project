package src.game;

import java.util.*;

public class BuffManager {
    private Map<String, ActiveBuff> activeBuffs = new HashMap<>();
    private Set<String> availableBuffs = new HashSet<>();

    private BuffEffects buffEffects;
    
    public BuffManager(BuffEffects buffEffects) {
        this.buffEffects = buffEffects;
        initializeAvailableBuffs();
    }
    
    private void initializeAvailableBuffs() {
        // Initialize all possible buffs
        availableBuffs.add("speedup");
        availableBuffs.add("attack");
        availableBuffs.add("health");
        availableBuffs.add("poison");
        availableBuffs.add("tripleCrit");
        availableBuffs.add("defense");
        availableBuffs.add("regeneration");
        availableBuffs.add("vampire");
        // Add more buffs here as needed
    }
    
    // Get three random available buffs
    public List<BuffChoice> getRandomBuffChoices() {
        if (availableBuffs.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> buffList = new ArrayList<>(availableBuffs);
        Collections.shuffle(buffList);
        
        List<BuffChoice> choices = new ArrayList<>();
        int count = Math.min(3, buffList.size());
        
        for (int i = 0; i < count; i++) {
            String buffId = buffList.get(i);
            choices.add(getBuffChoice(buffId));
        }
        
        return choices;
    }
    
    private BuffChoice getBuffChoice(String buffId) {
        switch (buffId) {
            case "speedup":
                return new BuffChoice(buffId, "攻速提升", "每秒多攻擊一次");
            case "attack":
                return new BuffChoice(buffId, "攻擊力提升", "提升基礎傷害");
            case "health":
                return new BuffChoice(buffId, "生命提升", "提升最大生命值");
            case "poison":
                return new BuffChoice(buffId, "毒性攻擊", "攻擊使敵人中毒，每秒損失3點生命值");
            case "tripleCrit":
                return new BuffChoice(buffId, "致命打擊", "暴擊傷害提升至三倍");
            case "defense":
                return new BuffChoice(buffId, "防禦增強", "受到的傷害-10%");
            case "regeneration":
                return new BuffChoice(buffId, "生命恢復", "每秒恢復1%最大生命值");
            case "vampire":
                return new BuffChoice(buffId, "吸血效果", "攻擊時恢復造成傷害的10%生命值");
            default:
                throw new IllegalArgumentException("Unknown buff ID: " + buffId);
        }
    }
    
    public void applyBuff(String buffId) {
        if (!availableBuffs.contains(buffId)) {
            return;
        }
        
        // Remove the buff from available pool
        availableBuffs.remove(buffId);
        
        // Create and store the active buff
        BuffChoice buffChoice = getBuffChoice(buffId);
        activeBuffs.put(buffId, new ActiveBuff(buffChoice.getName(), buffChoice.getDescription()));
        
        // Apply the buff effect
        buffEffects.applyBuffEffect(buffId);
    }
    public void clearAllBuffs() {
        buffEffects.stopAllEffects();
        activeBuffs.clear();
        initializeAvailableBuffs();
    }

    
    public Map<String, ActiveBuff> getActiveBuffs() {
        return activeBuffs;
    }
    
    public boolean hasAvailableBuffs() {
        return !availableBuffs.isEmpty();
    }
    
    public static class ActiveBuff {
        private String name;
        private String description;
        
        public ActiveBuff(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
    }
    
    public static class BuffChoice {
        private String id;
        private String name;
        private String description;
        
        public BuffChoice(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
    }
}