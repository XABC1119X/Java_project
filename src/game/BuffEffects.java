package src.game;

import java.util.Timer;
import java.util.TimerTask;

public class BuffEffects {
    private Player player;
    private Timer poisonTimer;
    private Timer regenerationTimer;
    private GameLogic gameLogic;
    private GameInterface gameInterface;

    public BuffEffects(Player player, GameInterface gameInterface, GameLogic gameLogic) {
        this.player = player;
        this.gameInterface = gameInterface;
        this.gameLogic = gameLogic;
    }
    
    public void applyBuffEffect(String buffId) {
        switch (buffId) {
            case "speedup":
                applySpeedupBuff();
                break;
            case "attack":
                applyAttackBuff();
                break;
            case "health":
                applyHealthBuff();
                break;
            case "poison":
                applyPoisonBuff();
                break;
            case "tripleCrit":
                applyTripleCritBuff();
                break;
            case "defense":
                applyDefenseBuff();
                break;
            case "regeneration":
                applyRegenerationBuff();
                break;
            case "vampire":
                applyVampireBuff();
                break;
        }
    }
    
    private void applySpeedupBuff() {
        player.increaseAttackSpeed(100); // 每秒多攻擊一次
    }
    
    private void applyAttackBuff() {
        player.increaseAttackPower(8); // 提升基礎傷害
    }
    
    private void applyHealthBuff() {
        player.increaseMaxHealth(30); // 提升最大生命值
    }
    
    private void applyPoisonBuff() {
        if (poisonTimer != null) {
            poisonTimer.cancel();
        }
        
        poisonTimer = new Timer();
        poisonTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 對敵人造成持續毒傷
                Monster target = gameLogic.getCurrentTarget();
                if (target != null) {
                    target.takeDamage(3, 3); // 每秒3點毒傷
                }
                gameInterface.updateUI();
            }
        }, 0, 1000);
    }
    
    private void applyTripleCritBuff() {
        // 在Player類中添加暴擊傷害倍率屬性並設置為3倍
        player.setCriticalDamageMultiplier(3.0);
    }
    
    private void applyDefenseBuff() {
        // 在Player類中添加傷害減免屬性
        player.setDamageReduction(0.1); // 10%傷害減免
    }
    
    private void applyRegenerationBuff() {
        if (regenerationTimer != null) {
            regenerationTimer.cancel();
        }
        
        regenerationTimer = new Timer();
        regenerationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int healAmount = (int)(player.getMaxHealth() * 0.01); // 每秒恢復1%最大生命值
                player.heal(healAmount);
                gameInterface.updateUI();
            }
        }, 0, 1000);
    }
    
    private void applyVampireBuff() {
        // 在Player類中添加吸血屬性
        player.setLifeSteal(0.1); // 10%吸血
    }
    
    public void stopAllEffects() {
        if (poisonTimer != null) {
            poisonTimer.cancel();
            poisonTimer = null;
        }
        if (regenerationTimer != null) {
            regenerationTimer.cancel();
            regenerationTimer = null;
        }
    }
}