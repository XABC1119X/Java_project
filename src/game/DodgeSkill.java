package src.game;

import java.util.TimerTask;
import javax.swing.ImageIcon;

/**
 * 閃避技能
 * 短時間內免疫傷害
 */
public class DodgeSkill implements Skill {
    private long lastUsedTime = 0;
    private static final int COOLDOWN = 10; // 10秒冷卻
    private static final int DURATION = 3000; // 3秒無敵時間
    
    @Override
    public String getId() {
        return "dodge";
    }
    
    @Override
    public String getName() {
        return "閃避";
    }
    
    @Override
    public String getDescription() {
        return "3秒內免疫所有傷害";
    }
    
    @Override
    public int getCooldown() {
        return COOLDOWN;
    }
    
    @Override
    public ImageIcon getIcon() {
        // 可以返回一個圖示，這裡返回 null 表示使用預設
        return null;
    }
    
    @Override
    public void execute(Player player, Monster monster, GameLogic gameLogic) {
        if (isOnCooldown()) return;
        
        // 設置無敵狀態
        player.setInvulnerable(true);
        lastUsedTime = System.currentTimeMillis();
        
        // 3秒後取消無敵
        TimerManager.getInstance().createTimer(new TimerTask() {
            @Override
            public void run() {
                player.setInvulnerable(false);
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

