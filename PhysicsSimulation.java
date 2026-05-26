import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PhysicsSimulation extends JPanel implements ActionListener {

    // Window dimensions
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // Ball properties
    private double ballX = 400;      // X position (center of screen)
    private double ballY = 50;       // Y position (starting high up)
    private final int radius = 20;   // Ball radius
    
    // Physics variables
    private double velocityY = 0;    // Initial vertical velocity
    private final double gravity = 0.5;    // Gravity acceleration per frame
    private final double bounceCoefficient = -0.75; // Energy retained after a bounce (negative to reverse direction)

    public PhysicsSimulation() {
        // Set up a timer to update the simulation roughly 60 times per second (~16ms per frame)
        Timer timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Enable anti-aliasing for smooth circles
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw the floor line
        g2d.setColor(Color.WHITE);
        int floorY = HEIGHT - 100;
        g2d.drawLine(0, floorY, WIDTH, floorY);

        // Draw the falling ball
        g2d.setColor(Color.CYAN);
        // Cast to int for rendering; subtract radius to center the drawing on (ballX, ballY)
        g2d.fillOval((int)ballX - radius, (int)ballY - radius, radius * 2, radius * 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 1. Apply gravity to vertical velocity
        velocityY += gravity;

        // 2. Update position based on velocity
        ballY += velocityY;

        // 3. Collision detection with the floor
        int floorY = HEIGHT - 100;
        
        // Check if the bottom of the ball hits or passes the floor
        if (ballY + radius >= floorY) {
            // Snap the ball exactly to the floor surface so it doesn't sink
            ballY = floorY - radius;
            
            // Reverse velocity and apply friction/energy loss
            velocityY = velocityY * bounceCoefficient;

            // Rest condition: If the bounce velocity is tiny, just stop it completely
            if (Math.abs(velocityY) < 0.8) {
                velocityY = 0;
            }
        }

        // 4. Repaint the screen
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("2D Physics Simulation - Falling Ball");
        PhysicsSimulation sim = new PhysicsSimulation();
        
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(sim);
        frame.setLocationRelativeTo(null); // Center window on screen
        frame.setVisible(true);
    }
}
