package src.game;

import javax.swing.ImageIcon;

/**
 * 治療技能
 * 立即恢復生命值
 */
public class HealSkill implements Skill {
    private long lastUsedTime = 0;
    private static final int COOLDOWN = 8; // 8秒冷卻
    private static final int HEAL_AMOUNT = 50; // 恢復50點生命值
    
    @Override
    public String getId() {
        return "heal";
    }
    
    @Override
    public String getName() {
        return "治療";
    }
    
    @Override
    public String getDescription() {
        return "立即恢復50點生命值";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public ImageIcon getIcon() {
        return null;
    }
    
    @Override
    public void execute(Player player, Monster monster, GameLogic gameLogic) {
        if (isOnCooldown()) return;
        
        player.heal(HEAL_AMOUNT);
        lastUsedTime = System.currentTimeMillis();
    }
    
    @Override
    public boolean isOnCooldown() {
        return (System.currentTimeMillis() - lastUsedTime) < COOLDOWN * 1000;
    }
    
    @Override
    public int getRemainingCooldown() {
        if (!isOnCooldown()) return 0;
        long elapsed = System.currentTimeMillis() - lastUsedTime;
        return (int) ((COOLDOWN * 1000 - elapsed) / 1000) + 1;
    }
}

