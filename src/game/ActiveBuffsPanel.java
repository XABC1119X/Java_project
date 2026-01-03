package src.game;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ActiveBuffsPanel extends JPanel {
    private BuffManager buffManager;
    private Timer updateTimer;
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    
    public ActiveBuffsPanel(BuffManager buffManager) {
        this.buffManager = buffManager;
        setLayout(new BorderLayout());
        
        // 設定半透明背景
        // setBackground(new Color(0, 0, 0, 100));
        
        // 創建標題面板
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(40, 40, 40, 200));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("當前生效的增益");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // 創建內容面板
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(40, 40, 40));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 創建滾動面板
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(new Color(40, 40, 40, 200));
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        // 自定義滾動條
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 100);
                this.trackColor = new Color(60, 60, 60);
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        
        // 設定面板大小和布局
        setPreferredSize(new Dimension(400, 300));
        setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
        
        // 添加組件
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        startUpdateTimer();
    }
    
    private void updateBuffList() {
        contentPanel.removeAll();
        Map<String, BuffManager.ActiveBuff> activeBuffs = buffManager.getActiveBuffs();
        
        if (activeBuffs.isEmpty()) {
            JLabel noBuffsLabel = new JLabel("目前沒有生效的增益");
            noBuffsLabel.setForeground(Color.LIGHT_GRAY);
            noBuffsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(noBuffsLabel);
        } else {
            for (Map.Entry<String, BuffManager.ActiveBuff> entry : activeBuffs.entrySet()) {
                BuffManager.ActiveBuff buff = entry.getValue();
                
                JPanel buffPanel = createBuffPanel(buff);
                buffPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(buffPanel);
                contentPanel.add(Box.createVerticalStrut(5)); // 添加間距
            }
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createBuffPanel(BuffManager.ActiveBuff buff) {
        // 創建主面板
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 5));
        panel.setBackground(new Color(60, 60, 60, 200));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // 設置固定高度
        int fixedHeight = 80; // 您可以根據需要調整這個值
        panel.setPreferredSize(new Dimension(0, fixedHeight));
        panel.setMinimumSize(new Dimension(0, fixedHeight));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, fixedHeight));
        
        // 創建名稱標籤
        JLabel nameLabel = new JLabel(buff.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        
        // 創建描述標籤，使用 HTML 來支持自動換行
        JLabel descLabel = new JLabel("<html><body style='width: 300px'>" + 
                                    buff.getDescription() + "</body></html>");
        descLabel.setForeground(Color.LIGHT_GRAY);
        
        // 添加組件到面板
        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(descLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void startUpdateTimer() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateBuffList());
            }
        }, 0, 1000);
    }
    
    public void stopTimer() {
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
    }
}