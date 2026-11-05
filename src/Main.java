import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.Timer;

class DrawPanel extends JPanel {
    static public ArrayList<Entity> circles = new ArrayList<>();

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
            g.fillOval((int)circle.x, (int)circle.y, (int)circle.diameter, (int)circle.diameter);
        }
    }
}

class Corner {
    double x;
    double y;
    public Corner (double x, double y){
        this.x = x;
        this.y=y;
    }
}

class Entity {
    double x;
    double y;
    double mass;
    double diameter;
    double Xcenter;
    double Ycenter;
    double previousX;
    double previousY;

    double xvel;
    double yvel;

    double xacc;
    double yacc;

    public Entity(double x, double y, double diameterMeters) {
        this(x, y, diameterMeters, 0, 0);
    }
    public Entity(double x, double y, double diameterMeters, double xvel, double yvel) {
        this(x, y, diameterMeters, diameterMeters, xvel, yvel);
    }
    public Entity(double x, double y, double diameterMeters, double massKg, double xvel, double yvel) {
        if (!Double.isFinite(diameterMeters) || diameterMeters <= 0 ||
                !Double.isFinite(massKg) || massKg <= 0) {
            throw new IllegalArgumentException("Diameter and mass must be finite and positive");
        }
        this.x = x;
        this.y = y;
        this.yacc = Main.gravity;
        this.mass = massKg;
        this.diameter = diameterMeters * Main.unit;
        syncCenter();
        this.xvel = xvel*Main.unit;
        this.yvel = yvel*Main.unit;
        this.previousX = x;
        this.previousY = y;
    }

    public void setXvel(double xvel) {
        this.xvel = xvel*Main.unit;
    }
    public void setYvel(double yvel) {
        this.yvel = yvel*Main.unit;
    }

    public void handlePhysics(){
        friction();
        updatePos();
        borderCollision();
        entityCollision();
    }

    void syncCenter() {
        Xcenter = x + diameter / 2;
        Ycenter = y + diameter / 2;
    }

    public void updateVel(){
        double previousVelocity = this.xvel;
        this.xvel = this.xacc * Main.t+ this.xvel;
        this.yvel = this.yacc * Main.t + this.yvel;
        if (y >= Main.Bottom_Right_Corner.y - diameter && previousVelocity * xvel < 0) {
            xvel = 0;
        }
    }
    public void updatePos(){
        this.previousX = this.x;
        this.previousY = this.y;
        this.x = 0.5 * this.xacc * Main.t*Main.t + this.xvel * Main.t + this.x;
        this.y = 0.5 * this.yacc * Main.t*Main.t + this.yvel * Main.t + this.y;
        this.updateVel();
        syncCenter();
    }
    public void borderCollision() {
        if (Main.Bottom_Right_Corner.y - this.y <= this.diameter) {
            this.y=Main.Bottom_Right_Corner.y-this.diameter;
            if (this.yvel > 0) this.yvel = Math.abs(this.yvel) <= Main.gravity * Main.t ? 0 : -this.yvel;
        }
        if (this.y < Main.Zero.y){
            this.y=Main.Zero.y;
            if (this.yvel < 0) this.yvel = -this.yvel;
        }

        if (Main.Bottom_Right_Corner.x - this.x < this.diameter) {
            this.x = Main.Bottom_Right_Corner.x-this.diameter;
            if (this.xvel > 0) this.xvel = -this.xvel;
        }
        if (this.x < 0){
            this.x = 0;
            if (this.xvel < 0) this.xvel = -this.xvel;
        }
        syncCenter();
    }
    public void friction(){
        this.xacc = 0;
        if (this.y>=Main.Bottom_Right_Corner.y-this.diameter && yvel >= 0) {
            double deceleration = Main.coeff * Main.gravity;
            if (Math.abs(xvel) <= deceleration * Main.t){
                xvel = 0;
                xacc = 0;
                return;
            }

            this.xacc = -Math.copySign(deceleration, xvel);


        }
    }
    public void entityCollision() {
        double EPS = 0.0001;

        for (int i = 0; i < DrawPanel.circles.size() - 1; i++) {
            for (int j = i + 1; j < DrawPanel.circles.size(); j++) {
                Entity e1 = DrawPanel.circles.get(i);
                Entity e2 = DrawPanel.circles.get(j);

                e1.syncCenter();
                e2.syncCenter();
                double r1 = e1.diameter / 2.0;
                double r2 = e2.diameter / 2.0;
                double minDist = r1 + r2;
                double sysXcenter = e2.Xcenter - e1.Xcenter;
                double sysYcenter = e2.Ycenter - e1.Ycenter;
                double dist = Math.sqrt(sysXcenter * sysXcenter + sysYcenter * sysYcenter);

                if (dist < minDist - EPS) { // Collision detected
                    if (dist == 0) {
                        sysXcenter = 1;
                        sysYcenter = 0;
                    }

                    double xnormal = dist == 0 ? 1 : sysXcenter / dist;
                    double ynormal = dist == 0 ? 0 : sysYcenter / dist;
                    double systemXvel = e2.xvel - e1.xvel;
                    double systemYvel = e2.yvel - e1.yvel;

                    double velAlongNormal = systemXvel * xnormal + systemYvel * ynormal;
                    double overlap = minDist - dist + EPS;
                    double inverseMass1 = 1 / e1.mass;
                    double inverseMass2 = 1 / e2.mass;
                    double correction1 = overlap * inverseMass1 / (inverseMass1 + inverseMass2);
                    double correction2 = overlap * inverseMass2 / (inverseMass1 + inverseMass2);

                    e1.Xcenter -= xnormal * correction1;
                    e1.Ycenter -= ynormal * correction1;
                    e2.Xcenter += xnormal * correction2;
                    e2.Ycenter += ynormal * correction2;
                    e1.x = e1.Xcenter - r1;
                    e1.y = e1.Ycenter - r1;
                    e2.x = e2.Xcenter - r2;
                    e2.y = e2.Ycenter - r2;
                    if (velAlongNormal >= 0) continue;

                    double m1 = e1.mass;
                    double m2 = e2.mass;
                    double bounce = (2 * velAlongNormal) / (m1 + m2);
                    e1.xvel += bounce * m2 * xnormal;
                    e1.yvel += bounce * m2 * ynormal;
                    e2.xvel -= bounce * m1 * xnormal;
                    e2.yvel -= bounce * m1 * ynormal;
                }
            }
        }
    }
}

public class Main {
    public static final double delay = 8;
    //unit is how many pixels represent a meter; 300 means screen is 4x2
    public static final double unit = 300;
    public static final double gravity = 9.8 * unit;
    public static double coeff = 0.005;

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

    static Corner Zero = new Corner(0,0);
    static Corner Bottom_Right_Corner = new Corner(1280,730);

    public static void main(String[] args) {
        DrawPanel world = InitScreen();
        Entity e1 = new Entity(20, 20, 0.05, 7, 0);
        world.addCircle(e1);
        Timer timer = new Timer((int)delay, e -> {
            world.addCircle(new Entity(0, 0, 0.05, 7, 0));
            System.out.println(DrawPanel.circles.size());

            for (Entity entity : DrawPanel.circles) {
                entity.handlePhysics();
                world.repaint();
            }
        });
        timer.start();
    }
}
