package src.game;

import java.util.function.Consumer;

/**
 * 魔法卡片類型枚舉
 * 定義所有可用的魔法卡片及其效果
 */
public enum MagicCardType {
    ATTACK_SPEED("攻速", GameConstants.MAGIC_CARD_ATTACK_SPEED_COST, 
                 p -> p.increaseAttackSpeed(GameConstants.MAGIC_CARD_ATTACK_SPEED_VALUE)),
    ATTACK_POWER("攻擊力", GameConstants.MAGIC_CARD_ATTACK_POWER_COST,
                 p -> p.increaseAttackPower(GameConstants.MAGIC_CARD_ATTACK_POWER_VALUE)),
    HEALTH("生命值", GameConstants.MAGIC_CARD_HEALTH_COST,
           p -> p.increaseMaxHealth(GameConstants.MAGIC_CARD_HEALTH_VALUE)),
    CRITICAL_RATE("暴擊率", GameConstants.MAGIC_CARD_CRITICAL_RATE_COST,
                   p -> p.increaseCriticalRate(GameConstants.MAGIC_CARD_CRITICAL_RATE_VALUE));
    
    private final String displayName;
    private final int cost;
    private final Consumer<Player> effect;
    
    MagicCardType(String displayName, int cost, Consumer<Player> effect) {
        this.displayName = displayName;
        this.cost = cost;
        this.effect = effect;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getCost() {
        return cost;
    }
    
    public void applyEffect(Player player) {
        effect.accept(player);
    }
    
    public String getCardText() {
        return displayName + " +" + getValue() + "\n(消耗" + cost + "金幣)";
    }
    
    private String getValue() {
        switch (this) {
            case ATTACK_SPEED:
                return "1";
            case ATTACK_POWER:
                return String.valueOf(GameConstants.MAGIC_CARD_ATTACK_POWER_VALUE);
            case HEALTH:
                return String.valueOf(GameConstants.MAGIC_CARD_HEALTH_VALUE);
            case CRITICAL_RATE:
                return "10%";
            default:
                return "";
        }
    }
    
    /**
     * 根據文字描述找到對應的卡片類型
     */
    public static MagicCardType fromText(String text) {
        for (MagicCardType type : values()) {
            if (text.contains(type.displayName)) {
                return type;
            }
        }
        return null;
    }
}

