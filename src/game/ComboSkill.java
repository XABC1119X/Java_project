package src.game;

import java.util.TimerTask;
import javax.swing.ImageIcon;

/**
 * 連擊技能
 * 快速連續攻擊多次
 */
public class ComboSkill implements Skill {
    private long lastUsedTime = 0;
    private static final int COOLDOWN = 12; // 12秒冷卻
    private static final int COMBO_COUNT = 5; // 連擊5次
    private static final int COMBO_INTERVAL = 200; // 每次攻擊間隔200ms
    
    @Override
    public String getId() {
        return "combo";
    }
    
    @Override
    public String getName() {
        return "連擊";
    }
    
    @Override
    public String getDescription() {
        return "快速連續攻擊5次";
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
        
        // 執行連擊
        for (int i = 0; i < COMBO_COUNT; i++) {
            TimerManager.getInstance().createTimer(new TimerTask() {
                @Override
                public void run() {
                    int damage = player.getCriticalAttackPower();
                    player.showAttackAnimation();
                    player.lifeSteal(damage);
                    monster.takeDamage(damage, player.getAttackPower());
                    gameLogic.getGameInterface().updateUI();
                }
            }, i * COMBO_INTERVAL);
        }
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

