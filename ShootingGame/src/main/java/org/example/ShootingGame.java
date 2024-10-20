package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShootingGame extends JPanel implements ActionListener {
  private int playerX = 250;
  private final int playerY = 450;
  private final List<Bullet> bullets = new ArrayList<>();
  private final List<Enemy> enemies = new ArrayList<>();
  private final Timer timer;
  private final Random random = new Random();
  private int lives = 3;
  private boolean gameRunning = false;
  private boolean shooting = false; // 弾を撃つフラグ

  public ShootingGame() {
    setPreferredSize(new Dimension(500, 500));
    setBackground(Color.BLACK);
    setFocusable(true);
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          playerX -= 15; // プレイヤーの移動速度を上げる
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          playerX += 15; // プレイヤーの移動速度を上げる
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
          shooting = true; // 弾を撃つフラグを立てる
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER && !gameRunning) {
          startGame(); // エンターキーでゲームスタート
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
          shooting = false; // 弾を撃つフラグを下ろす
        }
      }
    });
    timer = new Timer(20, this);
    timer.start();
    showOpeningScreen();
  }

  private void showOpeningScreen() {
    JOptionPane.showMessageDialog(this, "シューティングゲームへようこそ！\n\nエンターキーを押してゲームスタート", "オープニング", JOptionPane.INFORMATION_MESSAGE);
  }

  private void startGame() {
    lives = 3;
    bullets.clear();
    enemies.clear();
    gameRunning = true;
    spawnEnemies();
    timer.start();
  }

  private void spawnEnemies() {
    // 敵を追加する間隔（ミリ秒）
    int enemySpawnRate = 1000;
    Timer enemySpawner = new Timer(enemySpawnRate, e -> {
      enemies.add(new Enemy(random.nextInt(480), 0)); // 新しい敵を画面上部に追加
    });
    enemySpawner.start();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setColor(Color.WHITE);
    g.fillRect(playerX, playerY, 40, 20); // プレイヤー
    for (Bullet bullet : bullets) {
      g.setColor(Color.YELLOW);
      g.fillRect(bullet.x, bullet.y, 5, 10); // 弾
    }
    for (Enemy enemy : enemies) {
      g.setColor(Color.RED);
      g.fillRect(enemy.x, enemy.y, 40, 20); // 敵
    }
    g.setColor(Color.WHITE);
    g.drawString("Lives: " + lives, 10, 20); // 残機表示
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (!gameRunning) return; // ゲームが実行中でない場合は何もしない

    if (shooting) {
      bullets.add(new Bullet(playerX + 20, playerY)); // 弾を撃つ
    }

    for (int i = 0; i < bullets.size(); i++) {
      Bullet bullet = bullets.get(i);
      bullet.y -= 5; // 弾の移動
      if (bullet.y < 0) {
        bullets.remove(i);
        i--;
      }
    }
    for (int i = 0; i < enemies.size(); i++) {
      Enemy enemy = enemies.get(i);
      enemy.y += 1; // 敵の移動
      if (enemy.y > 500) {
        enemies.remove(i);
        i--;
      }
      if (enemy.y + 20 >= playerY && enemy.x < playerX + 40 && enemy.x + 40 > playerX) {
        // 敵がプレイヤーに当たった場合
        lives--;
        enemies.remove(i);
        i--;
        if (lives <= 0) {
          gameRunning = false;
          timer.stop();
          int result = JOptionPane.showConfirmDialog(this, "ゲームオーバー！\n再スタートしますか？", "終了", JOptionPane.YES_NO_OPTION);
          if (result == JOptionPane.YES_OPTION) {
            startGame();
          } else {
            System.exit(0);
          }
        }
      }
    }
    checkCollision();
    repaint();
  }

  private void checkCollision() {
    for (int i = 0; i < bullets.size(); i++) {
      Bullet bullet = bullets.get(i);
      for (int j = 0; j < enemies.size(); j++) {
        Enemy enemy = enemies.get(j);
        if (bullet.x < enemy.x + 40 && bullet.x + 5 > enemy.x && bullet.y < enemy.y + 20 && bullet.y + 10 > enemy.y) {
          bullets.remove(i);
          enemies.remove(j);
          i--;
          break;
        }
      }
    }
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("シューティングゲーム");
    ShootingGame game = new ShootingGame();
    frame.add(game);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  static class Bullet {
    int x, y;

    Bullet(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  static class Enemy {
    int x, y;

    Enemy(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
