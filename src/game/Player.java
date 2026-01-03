package src.game;

import java.awt.Point;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Player {
    private int health;
    private int maxHealth;
    private int attackPower;
    private int attackSpeed;
    private int coins;
    private double criticalDamageMultiplier; 
    private double damageReduction;
    private double lifeSteal;
    private double criticalRate;
    private static final Random random = new Random();

    // Images
    private ImageIcon heroPortrait;
    private ImageIcon attackPortrait;
    // private ImageIcon defensePortrait;
    private ImageIcon defeatPortrait;
    private JLabel portraitLabel;

    // Slash effect
    private ImageIcon slashEffect;
    private JLabel slashLabel;

    public Player() {
        initializeStats();
        initializeImages();
        initializeSlashEffect();
    }
    
    private void initializeSlashEffect() {
        slashEffect = new ImageIcon("src\\assets\\images\\effect\\slashEffect.gif");
        slashLabel = new JLabel(slashEffect);
        slashLabel.setBounds(0, 0, 600, 600); 
        slashLabel.setVisible(false); 
    }

    public void addSlashEffect() {
        Point position = portraitLabel.getLocation();
        // int width = portraitLabel.getWidth();
        // int height = portraitLabel.getHeight();
        
        slashLabel.setLocation(position.x +70, position.y - 198);
  
        portraitLabel.getParent().add(slashLabel);
        portraitLabel.getParent().setComponentZOrder(slashLabel, 0); 
    
        slashLabel.setVisible(true); 
    
        portraitLabel.getParent().repaint();
    }

    private void initializeImages() {
        heroPortrait = new ImageIcon("src\\assets\\images\\player\\hero.png");
        attackPortrait = new ImageIcon("src\\assets\\images\\player\\attack.png");
        // defensePortrait = new ImageIcon("src\\assets\\images\\player\\DEF.png");
        defeatPortrait = new ImageIcon("src\\assets\\images\\player\\GGgirl.png");
        
        portraitLabel = new JLabel(heroPortrait);
        portraitLabel.setBounds(50, 130, 250, 250);
    }

    private void initializeStats() {
        this.health = 100;
        this.maxHealth = 100;
        this.attackPower = 30;
        this.attackSpeed = 2000;
        this.coins = 100;
        this.criticalRate = 0.0;
        this.criticalDamageMultiplier = 2.0;
        this.damageReduction = 0.0; 
        this.lifeSteal = 0.0;
    }

    private void moveHero(int deltaX) {
        Point position = portraitLabel.getLocation();
        portraitLabel.setLocation(position.x + deltaX, position.y);
    }





    public void showAttackAnimation() {
        moveHero(150);
        portraitLabel.setIcon(attackPortrait);
        addSlashEffect();
        // Reset after delay
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                portraitLabel.setIcon(heroPortrait);
                moveHero(-150);
                slashLabel.setVisible(false);
            }
        }, 500);
    }
/* 
    public void showDefenseAnimation() {
        portraitLabel.setIcon(defensePortrait);
    }
*/
    public void showDefeatAnimation() {
        portraitLabel.setIcon(defeatPortrait);
    }

  

    public void takeDamage(int damage) {
        int reducedDamage = (int)(damage * (1 - damageReduction));
        this.health = Math.max(0, this.health - reducedDamage);
        
        DamageNumber damageNumber = new DamageNumber(reducedDamage, false, portraitLabel.getParent());
        damageNumber.setStartPosition(
            portraitLabel.getX() + portraitLabel.getWidth() / 2,
            portraitLabel.getY() + portraitLabel.getHeight() / 2
        );
        portraitLabel.getParent().add(damageNumber);
        portraitLabel.getParent().setComponentZOrder(damageNumber, 0);
    }

    public void lifeSteal(int amount) {
        if(!(lifeSteal == 0)) {
            int actualHeal = Math.min(maxHealth - health, this.health + (int) Math.ceil(amount * lifeSteal));
            this.health += actualHeal;
            // 創建治療數字特效
            DamageNumber healNumber = new DamageNumber(actualHeal, DamageNumber.NumberType.HEALING, portraitLabel.getParent());
            
            // 設置治療數字出現的位置（在玩家角色的中心位置）
            healNumber.setStartPosition(
                portraitLabel.getX() + portraitLabel.getWidth() / 2,
                portraitLabel.getY() + portraitLabel.getHeight() / 2
            );
            
            // 將治療數字添加到畫面上
            portraitLabel.getParent().add(healNumber);
            // 確保治療數字顯示在最上層
            portraitLabel.getParent().setComponentZOrder(healNumber, 0);
        } 
    }
    public void heal(int amount) {
        // 限制治療後的血量不超過最大血量
        int actualHeal = Math.min(maxHealth - health, amount);
        this.health += actualHeal;
    
        // 創建治療數字特效
        DamageNumber healNumber = new DamageNumber(actualHeal, DamageNumber.NumberType.HEALING, portraitLabel.getParent());
        
        // 設置治療數字出現的位置（在玩家角色的中心位置）
        healNumber.setStartPosition(
            portraitLabel.getX() + portraitLabel.getWidth() / 2,
            portraitLabel.getY() + portraitLabel.getHeight() / 2
        );
        
        // 將治療數字添加到畫面上
        portraitLabel.getParent().add(healNumber);
        // 確保治療數字顯示在最上層
        portraitLabel.getParent().setComponentZOrder(healNumber, 0);
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }
    
    public boolean spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

    public void increaseMaxHealth(int amount) {
        this.maxHealth += amount;
        this.health += amount;
    }

    public void increaseAttackPower(int amount) {
        this.attackPower += amount;
    }
    
    public void increaseCriticalRate(double amount) {
        this.criticalRate = Math.min(1.0, this.criticalRate + amount);
    }
    
    public void increaseAttackSpeed(int reduction) {
        this.attackSpeed = Math.max(600, this.attackSpeed - reduction);
    }
    

    // Getters and setters
    public int getCoins() { return coins; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAttackPower() { return attackPower; }
    public int getCriticalAttackPower() {
        if (random.nextDouble() < criticalRate) {
            return (int)(attackPower * criticalDamageMultiplier); // 使用暴擊傷害倍率
        }
        return attackPower;
    }
    public int getAttackSpeed() { return attackSpeed; }
    public double getCriticalRate() { return criticalRate; }
    public JLabel getPortraitLabel() { return portraitLabel; }
    public void setCriticalDamageMultiplier(double multiplier) {
        this.criticalDamageMultiplier = multiplier;
    }
    
    public void setDamageReduction(double reduction) {
        this.damageReduction = reduction;
    }
    
    public void setLifeSteal(double lifeSteal) {
        this.lifeSteal = lifeSteal;
    }
}