package src.game;

import javax.swing.ImageIcon;

/**
 * 商店商品類別
 */
public class ShopItem {
    private String id;
    private String name;
    private String description;
    private int cost;
    private ShopItemType type;
    private int value; // 提升的數值
    private boolean isPermanent;
    private ImageIcon icon;
    private boolean isPurchased;
    
    public enum ShopItemType {
        ATTACK_POWER,
        MAX_HEALTH,
        CRITICAL_RATE,
        ATTACK_SPEED,
        SPECIAL_ITEM
    }
    
    public ShopItem(String id, String name, String description, int cost, 
                   ShopItemType type, int value, boolean isPermanent) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.type = type;
        this.value = value;
        this.isPermanent = isPermanent;
        this.isPurchased = false;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCost() { return cost; }
    public ShopItemType getType() { return type; }
    public int getValue() { return value; }
    public boolean isPermanent() { return isPermanent; }
    public ImageIcon getIcon() { return icon; }
    public void setIcon(ImageIcon icon) { this.icon = icon; }
    public boolean isPurchased() { return isPurchased; }
    public void setPurchased(boolean purchased) { this.isPurchased = purchased; }
    
    /**
     * 應用商品效果到玩家
     */
    public void applyEffect(Player player) {
        if (isPurchased && !isPermanent) {
            return; // 一次性物品已使用
        }
        
        switch (type) {
            case ATTACK_POWER:
                player.increaseAttackPower(value);
                break;
            case MAX_HEALTH:
                player.increaseMaxHealth(value);
                break;
            case CRITICAL_RATE:
                player.increaseCriticalRate(value / 100.0);
                break;
            case ATTACK_SPEED:
                player.increaseAttackSpeed(value);
                break;
            case SPECIAL_ITEM:
                // 特殊物品效果在子類中實現
                break;
        }
        
        if (!isPermanent) {
            isPurchased = true;
        }
    }
}

