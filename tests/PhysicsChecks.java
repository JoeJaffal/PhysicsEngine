public class PhysicsChecks {
    static int checks;
    static void near(double actual, double expected) {
        if (Math.abs(actual - expected) > 1e-7 || !Double.isFinite(actual))
            throw new AssertionError(actual + " != " + expected);
        checks++;
    }
    static void collision(Entity a, Entity b) {
        DrawPanel.circles.clear();
        DrawPanel.circles.add(a); DrawPanel.circles.add(b);
        a.entityCollision();
        if (Math.hypot(a.Xcenter-b.Xcenter,a.Ycenter-b.Ycenter) < (a.diameter+b.diameter)/2)
            throw new AssertionError("Unresolved overlap");
        checks++;
    }
    public static void main(String[] args) {
        Entity fall=new Entity(100,100,0.05);
        fall.updatePos();
        near(fall.y,100+0.5*Main.gravity*Main.t*Main.t);
        near(fall.yvel,Main.gravity*Main.t);
        Entity shortForm=new Entity(100,100,0.05), fullForm=new Entity(100,100,0.05,0,0);
        near(shortForm.mass,fullForm.mass); near(shortForm.diameter,fullForm.diameter);
        near(shortForm.Xcenter,fullForm.Xcenter);
        Entity a=new Entity(100,100,0.05,1,1,0), b=new Entity(110,100,0.05,3,-1,0);
        double momentum=a.mass*a.xvel+b.mass*b.xvel;
        double energy=a.mass*a.xvel*a.xvel+b.mass*b.xvel*b.xvel;
        double center=(a.mass*a.Xcenter+b.mass*b.Xcenter)/(a.mass+b.mass);
        collision(a,b);
        near(a.mass*a.xvel+b.mass*b.xvel,momentum);
        near(a.mass*a.xvel*a.xvel+b.mass*b.xvel*b.xvel,energy);
        near((a.mass*a.Xcenter+b.mass*b.Xcenter)/(a.mass+b.mass),center);
        a=new Entity(100,100,0.05,-1,0);b=new Entity(110,100,0.05,1,0);
        collision(a,b);near(a.xvel,-300);near(b.xvel,300);
        collision(new Entity(100,100,0.05),new Entity(100,100,0.05));
        Entity ground=new Entity(100,715,0.05,1,0);
        ground.friction();double acceleration=ground.xacc;ground.friction();near(ground.xacc,acceleration);
        ground.y=100;ground.friction();near(ground.xacc,0);
        ground.y=715;ground.xvel=0.01;ground.friction();near(ground.xvel,0);
        ground.xvel=0;ground.yvel=0;
        for(int i=0;i<1000;i++){ground.updatePos();ground.borderCollision();}
        near(ground.y,715);near(ground.yvel,0);near(ground.Ycenter,722.5);
        Entity wall=new Entity(-1,100,0.05,1,0);wall.borderCollision();near(wall.xvel,300);near(wall.Xcenter,7.5);
        System.out.println("Passed "+checks+" physics checks.");
    }
}
