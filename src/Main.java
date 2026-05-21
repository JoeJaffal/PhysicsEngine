import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.Timer;


class DrawPanel extends JPanel {
    private ArrayList<Entity> circles = new ArrayList<>();

    public DrawPanel() {
        setBackground(Color.WHITE);
    }

    public void addCircle(Entity circle) {
        circles.add(circle);
        repaint();
    }

    public void clear() {
        circles.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Entity circle : circles) {
            g.setColor(Color.BLUE);
            g.fillOval((int)circle.x, (int)circle.y, (int)circle.mass, (int)circle.mass);
        }
    }
}


class Entity {
    double x;
    double y;
    double mass;

    double xvel = 800;
    double yvel = 0;
    double velocity = Math.sqrt(xvel*xvel + yvel*yvel);

    double xacc = 0;
    double yacc;
    double acceleration = Math.sqrt(xacc*xacc + yacc*yacc);
    boolean recCol=false;

    public Entity(double x, double y, double mass) {
        this.x = x;
        this.y = y;
        this.yacc = Main.gravity;
        this.mass = mass*10;
    }

    public void handlePhysics(){
        updatePos();
        borderCollision();
        friction();
    }

    public void updateVel(){
        this.xvel = this.xacc * Main.t+ this.xvel;
        this.yvel = this.yacc * Main.t + this.yvel;
    }
    public void updatePos(){
        this.updateVel();
        this.x = 0.5 * this.xacc * Main.t*Main.t + this.xvel * Main.t + this.x;
        this.y = 0.5 * this.yacc * Main.t*Main.t + this.yvel * Main.t + this.y;
    }
    public void borderCollision() {
        if (Main.ground - this.y < this.mass) {
            this.y=Main.ground-this.mass;
            this.yvel = -this.yvel;
        }
        if (this.y < 0){
            this.y=0;
            this.yvel = -this.yvel;
            this.yacc = -this.yacc;
        }

        if (Main.wall - this.x < this.mass) {
            this.x = Main.wall-this.mass;
            this.xvel = -this.xvel;
            this.xacc = -this.xacc;
        }
        if (this.x < 0){
            this.x = 0;
            this.xvel = -this.xvel;
            this.xacc = -this.xacc;
        }
    }
    public void friction(){
        if (this.y>=Main.ground-this.mass) {
            if (Math.abs(xvel) <= 1){
                xvel = 0;
                xacc = 0;
                return;
            }
            double coeff = 0.002;
            double normal = this.mass * Main.gravity;

            if (this.xvel>0) {
                this.xacc -= ((coeff * normal) / this.mass);
                if (this.xacc * Main.t+ this.xvel < 0){
                    this.xvel = 0;
                    this.xacc = 0;
                }
            }
            if (this.xvel<0) {
                this.xacc += ((coeff * normal) / this.mass);
                if (this.xacc * Main.t+ this.xvel > 0){
                    this.xvel = 0;
                    this.xacc = 0;
                }
            }
        }
    }
}

public class Main {
    public static final int ground = 730;
    public static final int wall = 1280;
    public static final double gravity = 9800;
    public static final double delay = 16;
    public static final double t = delay/1000;
    static DrawPanel InitScreen(){
        JFrame window = new JFrame("Physics Engine");

        DrawPanel canvas = new DrawPanel();

        window.add(canvas);
        window.setSize(1290,750);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        return canvas;
    }

    public static void main(String[] args) {
        DrawPanel world = InitScreen();

        // Objects are created in MAIN
        Entity e1 = new Entity(200, 400, 10);
        Entity e2 = new Entity(100, 100, 50);
        world.addCircle(e1);

        Timer timer = new Timer((int)delay, e -> {
            e1.handlePhysics();
            world.repaint();
        });
        timer.start();
    }
}