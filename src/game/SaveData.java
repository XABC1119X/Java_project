package src.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 遊戲存檔數據類別
 * 保存所有需要持久化的遊戲狀態
 */
public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int level;
    private int playerHealth;
    private int playerMaxHealth;
    private int playerAttackPower;
    private int playerAttackSpeed;
    private int playerCoins;
    private double playerCriticalRate;
    private double playerCriticalDamageMultiplier;
    private double playerDamageReduction;
    private double playerLifeSteal;
    private List<String> activeBuffs;
    private int totalKills;
    private long playTime;
    private Date saveDate;
    
    public SaveData() {
        this.activeBuffs = new ArrayList<>();
        this.saveDate = new Date();
    }
    
    // Getters and Setters
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public int getPlayerHealth() { return playerHealth; }
    public void setPlayerHealth(int playerHealth) { this.playerHealth = playerHealth; }
    
    public int getPlayerMaxHealth() { return playerMaxHealth; }
    public void setPlayerMaxHealth(int playerMaxHealth) { this.playerMaxHealth = playerMaxHealth; }
    
    public int getPlayerAttackPower() { return playerAttackPower; }
    public void setPlayerAttackPower(int playerAttackPower) { this.playerAttackPower = playerAttackPower; }
    
    public int getPlayerAttackSpeed() { return playerAttackSpeed; }
    public void setPlayerAttackSpeed(int playerAttackSpeed) { this.playerAttackSpeed = playerAttackSpeed; }
    
    public int getPlayerCoins() { return playerCoins; }
    public void setPlayerCoins(int playerCoins) { this.playerCoins = playerCoins; }
    
    public double getPlayerCriticalRate() { return playerCriticalRate; }
    public void setPlayerCriticalRate(double playerCriticalRate) { this.playerCriticalRate = playerCriticalRate; }
    
    public double getPlayerCriticalDamageMultiplier() { return playerCriticalDamageMultiplier; }
    public void setPlayerCriticalDamageMultiplier(double playerCriticalDamageMultiplier) { 
        this.playerCriticalDamageMultiplier = playerCriticalDamageMultiplier; 
    }
    
    public double getPlayerDamageReduction() { return playerDamageReduction; }
    public void setPlayerDamageReduction(double playerDamageReduction) { 
        this.playerDamageReduction = playerDamageReduction; 
    }
    
    public double getPlayerLifeSteal() { return playerLifeSteal; }
    public void setPlayerLifeSteal(double playerLifeSteal) { this.playerLifeSteal = playerLifeSteal; }
    
    public List<String> getActiveBuffs() { return activeBuffs; }
    public void setActiveBuffs(List<String> activeBuffs) { this.activeBuffs = activeBuffs; }
    
    public int getTotalKills() { return totalKills; }
    public void setTotalKills(int totalKills) { this.totalKills = totalKills; }
    
    public long getPlayTime() { return playTime; }
    public void setPlayTime(long playTime) { this.playTime = playTime; }
    
    public Date getSaveDate() { return saveDate; }
    public void setSaveDate(Date saveDate) { this.saveDate = saveDate; }
}

