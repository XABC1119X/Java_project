package src.game;

import java.util.TimerTask;
import javax.swing.ImageIcon;

/**
 * 時間緩慢技能
 * 減慢怪物攻擊速度
 */
public class SlowTimeSkill implements Skill {
    private long lastUsedTime = 0;
    private static final int COOLDOWN = 20; // 20秒冷卻
    private static final int DURATION = 5000; // 5秒效果時間
    private static final double SLOW_FACTOR = 0.5; // 減慢50%
    
    @Override
    public String getId() {
        return "slowTime";
    }
    
    @Override
    public String getName() {
        return "時間緩慢";
    }
    
    @Override
    public String getDescription() {
        return "5秒內怪物攻擊速度減慢50%";
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
        
        lastUsedTime = System.currentTimeMillis();
        
        // 減慢怪物攻擊速度（通過 GameLogic 實現）
        gameLogic.slowMonsterAttack(SLOW_FACTOR, DURATION);
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

