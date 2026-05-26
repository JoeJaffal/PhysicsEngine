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
            g.fillOval((int)circle.x, (int)circle.y, (int)circle.mass, (int)circle.mass);
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
    double Xcenter;
    double Ycenter;
    double previousX;
    double previousY;

    double xvel;
    double yvel;
    double velocity = Math.sqrt(xvel*xvel + yvel*yvel);

    double xacc;
    double yacc;
    double acceleration = Math.sqrt(xacc*xacc + yacc*yacc);

    public Entity(double x, double y, double mass) {
        this.x = x;
        this.y = y;
        this.yacc = Main.gravity;
        this.mass = mass;
    }
    public Entity(double x, double y, double mass, double xvel, double yvel) {
        this.x = x;
        this.y = y;
        this.yacc = Main.gravity;
        this.mass = mass*Main.unit;
        this.Xcenter = this.mass/2 + x;
        this.Ycenter = this.mass/2 + y;
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
        updatePos();
        borderCollision();
        friction();
        entityCollision();
    }

    public void updateVel(){
        this.xvel = this.xacc * Main.t+ this.xvel;
        this.yvel = this.yacc * Main.t + this.yvel;
        if (this.xvel>0) {
            if (this.xacc * Main.t+ this.xvel < 0){
                this.xvel = 0;
                this.xacc = 0;
            }
        }
        if (this.xvel<0) {
            if (this.xacc * Main.t+ this.xvel > 0){
                this.xvel = 0;
                this.xacc = 0;
            }
        }
    }
    public void updatePos(){
        this.updateVel();
        this.previousX = this.x;
        this.previousY = this.y;
        this.x = 0.5 * this.xacc * Main.t*Main.t + this.xvel * Main.t + this.x;
        this.y = 0.5 * this.yacc * Main.t*Main.t + this.yvel * Main.t + this.y;
        this.Xcenter = this.mass/2 + this.x;
        this.Ycenter = this.mass/2 + this.y;
    }
    public void borderCollision() {
        if (Main.Bottom_Right_Corner.y - this.y < this.mass) {
            this.y=Main.Bottom_Right_Corner.y-this.mass;
            this.yvel = -this.yvel;
        }
        if (this.y < Main.Zero.y){
            this.y=Main.Zero.y;
            this.yvel = -this.yvel;
        }

        if (Main.Bottom_Right_Corner.x - this.x < this.mass) {
            this.x = Main.Bottom_Right_Corner.x-this.mass;
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
        if (this.y>=Main.Bottom_Right_Corner.y-this.mass) {
            if (Math.abs(xvel) <= 1){
                xvel = 0;
                xacc = 0;
                return;
            }

            double normal = this.mass * Main.gravity;
            if (this.xvel<0) {
                this.xacc += ((Main.coeff * normal) / this.mass);
            }
            if (this.xvel>0) {
                this.xacc -= ((Main.coeff * normal) / this.mass);
            }


        }
    }
    public void entityCollision() {
        double EPS = 0.0001;

        for (int i = 0; i < DrawPanel.circles.size() - 1; i++) {
            for (int j = i + 1; j < DrawPanel.circles.size(); j++) {
                Entity e1 = DrawPanel.circles.get(i);
                Entity e2 = DrawPanel.circles.get(j);

                double r1 = e1.mass / 2.0;
                double r2 = e2.mass / 2.0;
                double minDist = r1 + r2;
                double sysXcenter = e2.Xcenter - e1.Xcenter;
                double sysYcenter = e2.Ycenter - e1.Ycenter;
                double dist = Math.sqrt(sysXcenter * sysXcenter + sysYcenter * sysYcenter);

                if (dist < minDist - EPS) { // Collision detected
                    if (dist == 0) {
                        sysXcenter = 1;
                        sysYcenter = 0;
                        dist = 1;
                    }

                    double xnormal = sysXcenter / dist;
                    double ynormal = sysYcenter / dist;
                    double systemXvel = e2.xvel - e1.xvel;
                    double systemYvel = e2.yvel - e1.yvel;

                    double velAlongNormal = systemXvel * xnormal + systemYvel * ynormal;
                    if (velAlongNormal > 0) {
                        continue;
                    }
                    double overlap = minDist - dist + EPS;
                    double ratio1 = r1/(r1+r2);

                    e1.Xcenter -= xnormal * overlap/(ratio1);
                    e1.Ycenter -= ynormal * overlap/(ratio1);
                    e2.Xcenter -= xnormal * overlap/(1-ratio1);
                    e2.Ycenter -= ynormal * overlap/(1-ratio1);
                    e1.x = e1.Xcenter - r1;
                    e1.y = e1.Ycenter - r1;
                    e2.x = e2.Xcenter - r2;
                    e2.y = e2.Ycenter - r2;

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