import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PhysicsSimulation extends JPanel implements ActionListener, ChangeListener {

    // Window dimensions
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private java.util.ArrayList<Ball> balls = new java.util.ArrayList<>();
    private JButton addBallButton;

    private double gravity = 0.5;
    private final double bounceCoefficient = -0.75;

    // --- DEFAULTS ---
    private static final int DEFAULT_GRAVITY_SLIDER_VAL = 50;
    private static final double DEFAULT_GRAVITY = 0.5;

    // --- UI COMPONENTS ---
    private JSlider gravitySlider;
    private JButton resetButton;

    public PhysicsSimulation() {
        balls.add(new Ball());

        // Set up a timer to update the simulation roughly 60 times per second
        Timer timer = new Timer(16, this);
        timer.start();
    }

    // A helper method to create and return the control panel
    public JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.LIGHT_GRAY);

        JLabel label = new JLabel("Gravity:");

        gravitySlider = new JSlider(50, 400, DEFAULT_GRAVITY_SLIDER_VAL);
        gravitySlider.addChangeListener(this);

        resetButton = new JButton("Reset Simulation");

        addBallButton = new JButton("Add Ball");
        addBallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                balls.add(new Ball());
                repaint();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gravitySlider.setValue(DEFAULT_GRAVITY_SLIDER_VAL);
                gravity = DEFAULT_GRAVITY;

                balls.clear();
                balls.add(new Ball());

                repaint();
            }
        });

        controlPanel.add(label);
        controlPanel.add(gravitySlider);
        controlPanel.add(addBallButton);
        controlPanel.add(resetButton);

        return controlPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Draw background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw floor
        g2d.setColor(Color.WHITE);
        int floorY = HEIGHT - 100;
        g2d.drawLine(0, floorY, WIDTH, floorY);

        for (Ball b : balls) {
            int x = (int) b.x;
            int y = (int) b.y;
            int r = b.radius;

            g2d.setColor(b.color);
            g2d.fillOval(x - r, y - r, r * 2, r * 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int floorY = HEIGHT - 100;

        for (Ball ball : balls) {
            // Apply gravity
            ball.velocityY += gravity;

            // Move ball
            ball.x += ball.velocityX;
            ball.y += ball.velocityY;

            // Bounce off left wall
            if (ball.x - ball.radius <= 0) {
                ball.x = ball.radius;
                ball.velocityX *= -1;
            }

            // Bounce off right wall
            if (ball.x + ball.radius >= WIDTH) {
                ball.x = WIDTH - ball.radius;
                ball.velocityX *= -1;
            }

            // Bounce off floor
            if (ball.y + ball.radius >= floorY) {
                ball.y = floorY - ball.radius;
                ball.velocityY = ball.velocityY * bounceCoefficient;

                if (Math.abs(ball.velocityY) < (gravity * 1.5)) {
                    ball.velocityY = 0;
                }
            }
        }

        handleBallCollisions();
        repaint();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        this.gravity = source.getValue() / 100.0;
    }

    private static class Ball {
        double x = 200 + Math.random() * 400;
        double y = 50 + Math.random() * 80;
        double velocityX = -3 + Math.random() * 6;
        double velocityY = -2 + Math.random() * 4;
        int radius = 20;

        Color color = new Color(
            (int) (Math.random() * 256),
            (int) (Math.random() * 256),
            (int) (Math.random() * 256)
        );
    }

    private static JPanel createHomeScreen(CardLayout cardLayout, JPanel mainPanel) {
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new GridBagLayout());
        homePanel.setBackground(Color.DARK_GRAY);

        JLabel title = new JLabel("Welcome to the Physics Simulation!");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        JButton startButton = new JButton("Start Simulation");
        startButton.setFont(new Font("Arial", Font.PLAIN, 22));

        startButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "SIMULATION");
        });

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        content.add(startButton);

        homePanel.add(content);

        return homePanel;
    }

    private void handleBallCollisions() {
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball b1 = balls.get(i);
                Ball b2 = balls.get(j);

                double dx = b2.x - b1.x;
                double dy = b2.y - b1.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                double minDist = b1.radius + b2.radius;

                if (distance < minDist && distance > 0) {
                    double normalX = dx / distance;
                    double normalY = dy / distance;
                    double overlap = minDist - distance;

                    // Push balls apart so they are no longer overlapping
                    b1.x -= normalX * overlap / 2;
                    b1.y -= normalY * overlap / 2;
                    b2.x += normalX * overlap / 2;
                    b2.y += normalY * overlap / 2;

                    double relativeVelocityX = b2.velocityX - b1.velocityX;
                    double relativeVelocityY = b2.velocityY - b1.velocityY;

                    double speed = relativeVelocityX * normalX + relativeVelocityY * normalY;

                    // Only bounce them if they are moving toward each other
                    if (speed < 0) {
                        double impulse = speed;

                        b1.velocityX += impulse * normalX;
                        b1.velocityY += impulse * normalY;

                        b2.velocityX -= impulse * normalX;
                        b2.velocityY -= impulse * normalY;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("2D Physics Simulation - Falling Ball");

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        PhysicsSimulation sim = new PhysicsSimulation();

        JPanel simulationScreen = new JPanel(new BorderLayout());
        simulationScreen.add(sim, BorderLayout.CENTER);
        simulationScreen.add(sim.createControlPanel(), BorderLayout.SOUTH);

        JPanel homeScreen = createHomeScreen(cardLayout, mainPanel);

        mainPanel.add(homeScreen, "HOME");
        mainPanel.add(simulationScreen, "SIMULATION");

        frame.add(mainPanel);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cardLayout.show(mainPanel, "HOME");
    }
}
