package src.game;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

/**
 * 技能欄 UI
 * 顯示已裝備的技能並允許使用
 */
public class SkillBar extends JPanel {
    private SkillManager skillManager;
    private Player player;
    private Monster monster;
    private GameLogic gameLogic;
    private List<SkillButton> skillButtons = new ArrayList<>();
    private Timer cooldownTimer;
    
    public SkillBar(SkillManager skillManager, Player player, Monster monster, GameLogic gameLogic) {
        this.skillManager = skillManager;
        this.player = player;
        this.monster = monster;
        this.gameLogic = gameLogic;
        
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setOpaque(false);
        
        // 創建技能按鈕
        for (Skill skill : skillManager.getEquippedSkills()) {
            SkillButton button = new SkillButton(skill);
            skillButtons.add(button);
            add(button);
        }
        
        // 啟動冷卻時間更新計時器
        startCooldownTimer();
    }
    
    private void startCooldownTimer() {
        cooldownTimer = new Timer(100, e -> {
            for (SkillButton button : skillButtons) {
                button.updateCooldown();
            }
        });
        cooldownTimer.start();
    }
    
    public void stopTimer() {
        if (cooldownTimer != null) {
            cooldownTimer.stop();
        }
    }
    
    private class SkillButton extends JButton {
        private Skill skill;
        
        public SkillButton(Skill skill) {
            this.skill = skill;
            setPreferredSize(new Dimension(60, 60));
            setToolTipText(skill.getName() + " - " + skill.getDescription() + " (冷卻: " + skill.getCooldown() + "秒)");
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            
            // 設置快捷鍵
            char key = (char) ('1' + skillButtons.size());
            setMnemonic(key);
            setText(String.valueOf(key));
            
            addActionListener(e -> {
                if (!skill.isOnCooldown()) {
                    skillManager.useSkill(skill.getId(), player, monster, gameLogic);
                    repaint();
                }
            });
        }
        
        public void updateCooldown() {
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            
            // 繪製圓形背景
            if (skill.isOnCooldown()) {
                g2d.setColor(new Color(40, 40, 40, 200));
            } else {
                g2d.setColor(new Color(60, 60, 60, 200));
            }
            g2d.fill(new Ellipse2D.Float(x, y, size, size));
            
            // 繪製邊框
            g2d.setColor(skill.isOnCooldown() ? Color.GRAY : Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(new Ellipse2D.Float(x + 1, y + 1, size - 2, size - 2));
            
            // 繪製冷卻遮罩
            if (skill.isOnCooldown()) {
                int remaining = skill.getRemainingCooldown();
                double cooldownPercent = (double) remaining / skill.getCooldown();
                int maskHeight = (int) (size * cooldownPercent);
                
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fill(new Ellipse2D.Float(x, y + size - maskHeight, size, maskHeight));
                
                // 顯示剩餘時間
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                String time = String.valueOf(remaining);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (size - fm.stringWidth(time)) / 2;
                int textY = y + size / 2 + fm.getAscent() / 2;
                g2d.drawString(time, textX, textY);
            } else {
                // 顯示快捷鍵
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                String key = getText();
                int textX = x + (size - fm.stringWidth(key)) / 2;
                int textY = y + size / 2 + fm.getAscent() / 2;
                g2d.drawString(key, textX, textY);
            }
            
            g2d.dispose();
        }
    }
}

