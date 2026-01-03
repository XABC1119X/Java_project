package src.game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.function.Consumer;

public class BuffSelPanel extends JPanel {
    private Consumer<String> onBuffSelected;
    
    public BuffSelPanel(BuffManager buffManager, Consumer<String> onBuffSelected) {
        
        this.onBuffSelected = onBuffSelected;
        setLayout(new GridBagLayout());
        setBackground(new Color(0, 0, 0, 150));
        setOpaque(false);
        
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(40, 40, 40));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        
        JLabel titleLabel = new JLabel("選擇一個增益");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        contentPanel.add(titleLabel, gbc);
        
        if (!buffManager.hasAvailableBuffs()) {
            // Show message when no buffs are available
            gbc.gridy = 1;
            JLabel noBuffsLabel = new JLabel("已獲得所有增益效果！");
            noBuffsLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
            noBuffsLabel.setForeground(Color.LIGHT_GRAY);
            contentPanel.add(noBuffsLabel, gbc);
        } else {
            // Create buff buttons from random choices
            List<BuffManager.BuffChoice> buffChoices = buffManager.getRandomBuffChoices();
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(10, 10, 10, 10);
            
            for (int i = 0; i < buffChoices.size(); i++) {
                BuffManager.BuffChoice buff = buffChoices.get(i);
                BuffButton buffButton = new BuffButton(buff.getName(), buff.getDescription(), buff.getId());
                gbc.gridx = i;
                contentPanel.add(buffButton, gbc);
            }
        }
        
        add(contentPanel);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
    
    private class BuffButton extends JPanel {
        public BuffButton(String title, String description, String buffId) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(60, 60, 60));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            setPreferredSize(new Dimension(150, 150));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
            descLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
            descLabel.setForeground(Color.LIGHT_GRAY);
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            add(Box.createVerticalGlue());
            add(titleLabel);
            add(Box.createVerticalStrut(10));
            add(descLabel);
            add(Box.createVerticalGlue());
            
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    onBuffSelected.accept(buffId);
                }
                
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(80, 80, 80));
                }
                
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(60, 60, 60));
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
            g2d.dispose();
            super.paintComponent(g);
        }
    }
}