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
    }

    public void setXvel(double xvel) {
        this.xvel = xvel*Main.unit;
    }
    public void setYvel(double yvel) {
        this.xvel = xvel*Main.unit;
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
    public static double getDist(Entity d1, Entity d2){
        return Math.sqrt((d1.Xcenter-d2.Xcenter)*(d1.Xcenter-d2.Xcenter) + (d1.Ycenter-d2.Ycenter)*(d1.Ycenter-d2.Ycenter));
    }
    public void entityCollision(){
        double tempX;
        double tempY;
        for (int i=0; i<DrawPanel.circles.size()-1; i++){
            for (int j=i+1; j<DrawPanel.circles.size(); j++){
                Entity e1 = DrawPanel.circles.get(i);
                Entity e2 = DrawPanel.circles.get(j);
                if (getDist(e1,e2) <= e1.mass/2 + e2.mass/2){
                    // SET THEM APART THEN CALC
                    tempX = e1.xvel;
                    tempY = e1.yvel;
                    e1.xvel = ((e1.mass-e2.mass)/(e1.mass+e2.mass))*e1.xvel + ((2*e2.mass)/(e1.mass+e2.mass))* e2.xvel;
                    e2.xvel = ((2*e1.mass)/(e1.mass+e2.mass))*tempX + ((e2.mass-e1.mass)/(e1.mass+e2.mass))* e2.xvel;
                    e1.yvel = ((e1.mass-e2.mass)/(e1.mass+e2.mass))*e1.yvel + ((2*e2.mass)/(e1.mass+e2.mass))* e2.yvel;
                    e2.yvel = ((2*e1.mass)/(e1.mass+e2.mass))*tempY + ((e2.mass-e1.mass)/(e1.mass+e2.mass))* e2.yvel;
                }
            }
        }
    }
}

public class Main {
    public static final double delay = 8;
    //unit is how many pixels represent a meter; 300 means screen is 4x2
    public static final double unit = 300;
    public static final double gravity = 25 * unit;
    public static double coeff = 0.002;

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

        // Objects are created in MAIN
        Entity e1 = new Entity(200, 300, 0.2, 2, 0);
        Entity e2 = new Entity(800, 200, 0.4, -2, 0);
        world.addCircle(e1);
        world.addCircle(e2);

        Timer timer = new Timer((int)delay, e -> {
            for (Entity entity : DrawPanel.circles) {
                entity.handlePhysics();
                world.repaint();
            }
        });
        timer.start();
    }
}