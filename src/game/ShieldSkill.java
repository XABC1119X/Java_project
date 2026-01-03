package src.game;

import java.util.TimerTask;
import javax.swing.ImageIcon;

/**
 * 護盾技能
 * 短時間內減少傷害
 */
public class ShieldSkill implements Skill {
    private long lastUsedTime = 0;
    private static final int COOLDOWN = 15; // 15秒冷卻
    private static final int DURATION = 5000; // 5秒護盾時間
    private static final double SHIELD_REDUCTION = 0.5; // 減少50%傷害
    
    @Override
    public String getId() {
        return "shield";
    }
    
    @Override
    public String getName() {
        return "護盾";
    }
    
    @Override
    public String getDescription() {
        return "5秒內減少50%傷害";
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
        
        // 增加傷害減免
        double originalReduction = player.getDamageReduction();
        player.setDamageReduction(originalReduction + SHIELD_REDUCTION);
        lastUsedTime = System.currentTimeMillis();
        
        // 5秒後恢復
        TimerManager.getInstance().createTimer(new TimerTask() {
            @Override
            public void run() {
                player.setDamageReduction(originalReduction);
            }
        }, DURATION);
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

