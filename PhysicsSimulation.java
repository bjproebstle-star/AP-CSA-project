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

    // Ball properties
        
    // Physics variables
    // Initial vertical velocity
    private java.util.ArrayList<Ball> balls = new java.util.ArrayList<>();
    private JButton addBallButton;

    private double gravity = 0.5;    // Gravity acceleration per frame
    private final double bounceCoefficient = -0.75; 

    // --- DEFAULTS ---
    private static final int DEFAULT_GRAVITY_SLIDER_VAL = 50;
    private static final double DEFAULT_GRAVITY = 0.5;
    private static final double DEFAULT_BALL_Y = 50;
    private static final double DEFAULT_VELOCITY_Y = 0;

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

        // Initialize the instance slider variable
        gravitySlider = new JSlider(50, 400, DEFAULT_GRAVITY_SLIDER_VAL);
        gravitySlider.addChangeListener(this);

        // Initialize the reset button
        resetButton = new JButton("Reset Simulation");

        addBallButton = new JButton("Add Ball");
        addBallButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ball newBall = new Ball();
                newBall.x = 200 + (int)(Math.random() * 400); // Randomize starting x position
                balls.add(newBall);
                repaint();
            }
        });
        
        // Add action listener to reset slider and physics variables
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Reset the UI slider visual
                //gravitySlider.setValue(DEFAULT_GRAVITY_SLIDER_VAL);
                
                // 2. Reset the actual physics variables
                gravity = DEFAULT_GRAVITY;
                balls.clear();
                balls.add(new Ball());
            
                
                
                // 3. Force a screen refresh immediately
                repaint();
            }
        });

        // Add components to panel
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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw floor
        g2d.setColor(Color.WHITE);
        int floorY = HEIGHT - 100;
        g2d.drawLine(0, floorY, WIDTH, floorY);

        

            for(Ball b : balls)
            {
                int x = (int) b.x;
                int y = (int) b.y;
                int r =  b.radius;

                g2d.setColor(b.color);
                g2d.fillOval(x - r, y - r, r * 2, r * 2);


            }



    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Apply physics
        int floorY = HEIGHT - 100;

    // NEW: Apply physics to every individual ball in the list
        for (Ball ball : balls) {
        // 1. Apply gravity to vertical velocity
            ball.velocityY += gravity;
        // 2. Update position based on velocity
            ball.y += ball.velocityY;

        // 3. Collision detection with the floor
            if (ball.y + ball.radius >= floorY) {
                ball.y = floorY - ball.radius;
                ball.velocityY = ball.velocityY * bounceCoefficient;

                if (Math.abs(ball.velocityY) < (gravity * 1.5)) {
                    ball.velocityY = 0;
                }
            }
        }
        repaint();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        this.gravity = source.getValue() / 100.0;
    }
    
    private static class Ball {
    double x = 400;
    double y = 50;
    double velocityY = 0;
    int radius = 20;
    
    // Give each ball a random color so you can tell them apart!
    Color color = new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));

    
    public static void main(String[] args) {
        JFrame frame = new JFrame("2D Physics Simulation - Falling Ball");
        PhysicsSimulation sim = new PhysicsSimulation();



        

        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Add the simulation to the center
        frame.add(sim, BorderLayout.CENTER);

        // Get the control panel from the simulation instance and add it to the bottom
        JPanel panel = sim.createControlPanel();
        frame.add(panel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }
}
}