package src.game;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * 商店面板
 * 提供購買永久升級和道具的介面
 */
public class ShopPanel extends JPanel {
    private Player player;
    private List<ShopItem> availableItems;
    private JLabel coinsLabel;
    private Runnable onClose;
    
    public ShopPanel(Player player, Runnable onClose) {
        this.player = player;
        this.onClose = onClose;
        initializeShopItems();
        setupUI();
    }
    
    private void initializeShopItems() {
        availableItems = new ArrayList<>();
        
        // 永久升級
        availableItems.add(new ShopItem("attack_power_1", "攻擊力提升 I", 
            "永久提升攻擊力 +10", 100, ShopItem.ShopItemType.ATTACK_POWER, 10, true));
        availableItems.add(new ShopItem("attack_power_2", "攻擊力提升 II", 
            "永久提升攻擊力 +20", 200, ShopItem.ShopItemType.ATTACK_POWER, 20, true));
        availableItems.add(new ShopItem("max_health_1", "生命值提升 I", 
            "永久提升最大生命值 +50", 150, ShopItem.ShopItemType.MAX_HEALTH, 50, true));
        availableItems.add(new ShopItem("max_health_2", "生命值提升 II", 
            "永久提升最大生命值 +100", 300, ShopItem.ShopItemType.MAX_HEALTH, 100, true));
        availableItems.add(new ShopItem("critical_rate_1", "暴擊率提升 I", 
            "永久提升暴擊率 +5%", 150, ShopItem.ShopItemType.CRITICAL_RATE, 5, true));
        availableItems.add(new ShopItem("attack_speed_1", "攻速提升 I", 
            "永久提升攻擊速度 -200ms", 200, ShopItem.ShopItemType.ATTACK_SPEED, 200, true));
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(GameConstants.UI_BACKGROUND_DARK);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // 標題面板
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // 商品列表
        JPanel itemsPanel = createItemsPanel();
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(GameConstants.UI_BACKGROUND_DARK);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = GameConstants.UI_BACKGROUND_LIGHT;
                this.trackColor = GameConstants.UI_BACKGROUND_MEDIUM;
            }
        });
        add(scrollPane, BorderLayout.CENTER);
        
        // 底部面板（金幣顯示和關閉按鈕）
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GameConstants.UI_BACKGROUND_DARK);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        JLabel title = new JLabel("商店", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 32));
        title.setForeground(GameConstants.UI_TEXT_PRIMARY);
        
        panel.add(title, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 15));
        panel.setBackground(GameConstants.UI_BACKGROUND_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        for (ShopItem item : availableItems) {
            panel.add(createItemCard(item));
        }
        
        return panel;
    }
    
    private JPanel createItemCard(ShopItem item) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 繪製圓角背景
                if (item.isPurchased()) {
                    g2d.setColor(new Color(60, 60, 60, 150));
                } else {
                    g2d.setColor(GameConstants.UI_BACKGROUND_MEDIUM);
                }
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                
                // 邊框
                g2d.setColor(GameConstants.UI_BACKGROUND_LIGHT);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 15, 15));
                
                g2d.dispose();
            }
        };
        
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(200, 180));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 名稱
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        nameLabel.setForeground(GameConstants.UI_TEXT_PRIMARY);
        card.add(nameLabel, BorderLayout.NORTH);
        
        // 描述
        JLabel descLabel = new JLabel("<html><body style='width: 170px'>" + 
                                     item.getDescription() + "</body></html>");
        descLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        descLabel.setForeground(GameConstants.UI_TEXT_SECONDARY);
        card.add(descLabel, BorderLayout.CENTER);
        
        // 價格和購買按鈕
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        
        JLabel priceLabel = new JLabel("價格: " + item.getCost());
        priceLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        priceLabel.setForeground(GameConstants.UI_ACCENT_COINS);
        bottomPanel.add(priceLabel, BorderLayout.WEST);
        
        JButton buyButton = new JButton(item.isPurchased() ? "已購買" : "購買");
        buyButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        buyButton.setEnabled(!item.isPurchased() && player.getCoins() >= item.getCost());
        buyButton.addActionListener(e -> {
            if (player.spendCoins(item.getCost())) {
                item.applyEffect(player);
                item.setPurchased(true);
                buyButton.setText("已購買");
                buyButton.setEnabled(false);
                updateCoinsLabel();
                updateAllBuyButtons();
                JOptionPane.showMessageDialog(this, "購買成功！", "商店", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        bottomPanel.add(buyButton, BorderLayout.EAST);
        
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        // 點擊卡片也可以購買
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!item.isPurchased() && player.getCoins() >= item.getCost()) {
                    buyButton.doClick();
                }
            }
        });
        
        return card;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GameConstants.UI_BACKGROUND_DARK);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        coinsLabel = new JLabel("金幣: " + player.getCoins());
        coinsLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        coinsLabel.setForeground(GameConstants.UI_ACCENT_COINS);
        panel.add(coinsLabel, BorderLayout.WEST);
        
        JButton closeButton = new JButton("關閉");
        closeButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        closeButton.addActionListener(e -> {
            if (onClose != null) {
                onClose.run();
            }
        });
        panel.add(closeButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private void updateCoinsLabel() {
        coinsLabel.setText("金幣: " + player.getCoins());
    }
    
    private void updateAllBuyButtons() {
        // 更新所有購買按鈕的啟用狀態
        for (Component comp : getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                JViewport viewport = scrollPane.getViewport();
                Component view = viewport.getView();
                if (view instanceof JPanel) {
                    updateButtonsInPanel((JPanel) view);
                }
            }
        }
    }
    
    private void updateButtonsInPanel(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel card = (JPanel) comp;
                for (Component cardComp : card.getComponents()) {
                    if (cardComp instanceof JPanel) {
                        JPanel bottomPanel = (JPanel) cardComp;
                        for (Component bottomComp : bottomPanel.getComponents()) {
                            if (bottomComp instanceof JButton) {
                                JButton button = (JButton) bottomComp;
                                if (!button.getText().equals("已購買")) {
                                    // 找到對應的商品
                                    for (ShopItem item : availableItems) {
                                        if (item.getCost() <= player.getCoins() && !item.isPurchased()) {
                                            button.setEnabled(true);
                                            break;
                                        } else {
                                            button.setEnabled(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

