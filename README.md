# PhysicsEngine

This is my first real challenge/project where I am trying to reduce my dependence on ChatGPT and really test my own coding skills.

I have always had a passion for game development, so I decided that the best project for me right now is to try building a simple physics engine from scratch.

## Why Java?

I chose Java mainly because it felt like the simplest option for opening windows and drawing graphics compared to other languages I considered, like C++. From what I found, C++ makes the window setup process more complicated.

Another reason I chose Java is because of its object-oriented nature, which fits well with the way I want to structure the project, and the way I think real physics engines are made.

## What I Learned First

I started by learning how to open a window using `JFrame`.

After that, I placed a custom panel inside the window by extending `JPanel`. This allowed me to draw shapes on the screen. I also overrode the painting behavior so the panel could fit the needs of my project.

Everything up to this point was learned through library documentation and basic research.

## The Real Challenge

From this point forward, I want to rely mainly on my own thinking.

I may occasionally reference a physics book, mainly:

**Fundamentals of Physics Extended, 10th Edition — Jearl Walker**

The goal is to translate my understanding of physics into code myself.

## Current Progress

I started by creating an `Entity` class.

For now, an entity is basically a circle with extra physical properties, such as:

- Position
- Mass, which is related to the size of the circle
- Velocity
- Acceleration

I then created several physics-related methods inside the `Entity` class:

- `updateVel()`
- `updatePos()`
- `borderCollision()`
- `friction()`

These functions are based on ideas from Newton’s First and Second Laws of Motion.

I also added a `handlePhysics()` function inside the `Entity` class. This function calls the other physics functions together, which keeps the `main` file cleaner and easier to read.

## Unit System

At first, the simulation was using pixels directly inside the physics calculations. This meant values like position and velocity were effectively being treated as pixels and pixels per second.

I later fixed this by adding a `unit` variable in `main`, which controls how many pixels represent one meter.

Now, the physics calculations are handled in meters, while rendering still happens in pixels. This separates the actual physics logic from the screen drawing logic and makes the simulation more realistic and easier to reason about.

I also fixed a small mistake inside `updateVel()` during this update.

## How the Simulation Works

In the `main` file, I define final variables such as:

- Unit, which controls the scale of how many pixels represent one meter
- Gravity
- Window borders
- Delay between frames, which is related to the time used in the formulas
- Other simulation constants

The program starts by initializing the window and screen.

Then, I use Java’s `Timer` library to simulate frames. The timer works like a simple game loop. On every tick:

1. The physics of each entity is updated.
2. The screen is repainted using `repaint()`.
3. The new positions are drawn on the panel.

## Entity Collision System

I started working on entity-to-entity collision.

At first, I had to rethink how collision response should work. The correct idea is not to simply apply the conservation of linear momentum separately to the global x and y velocity components.

Instead, the collision should be handled along the collision normal.

The collision normal is the direction between the centers of the two circles at the moment of contact. Since the collision force acts along this line, the velocities are projected onto the normal direction, and the collision response is calculated there.

This means the collision is treated like a one-dimensional collision along the normal vector. After calculating the result along that direction, the velocity can be split back into x and y components.

The tangential component is not directly affected by the collision itself, because for ideal circle collision, the collision impulse acts along the normal direction.

In simple terms:

collision normal = direction between the two circle centers

and:

collision response = momentum/velocity exchange along that normal direction

## Collision Jittering and Overlap Correction

While testing entity collision, I found a problem similar to the jittering issue I previously had with ground collision.

For ground collision, jittering happens when the entity keeps slightly passing through the ground and then being corrected again every frame. The fix was to use a stricter collision check, and when the condition is met, place the entity exactly on the surface.

With entity collision, the problem is more complicated because both objects can move, and the collision surface is not fixed like the ground.

To fix the overlap after collision, I currently treat one entity as fixed temporarily. Then I move the other entity out of the overlap using:

movement distance = overlap
movement direction = collision normal

This separates the two circles so they are no longer inside each other.

## Tunneling Problem

Another issue I noticed is tunneling.

Since collision is checked once per frame, it is possible for a fast-moving entity to pass too far into another entity before the collision is detected. In extreme cases, the entities could completely pass through each other between two frames, causing the collision to not be detected at all.

To reduce this problem, I started storing the previous position of the entity before collision detection. This helps make the correction more accurate because I can compare the corrected position with where the entity came from, not only where it ended up after the frame update.

This solution works well in most normal cases, but it does not completely solve high-speed tunneling yet.

A possible future solution is continuous collision detection.

## Current Features

- Window creation using `JFrame`
- Custom drawing panel using `JPanel`
- Circle-based entities
- Position, velocity, acceleration, and mass
- Meter-based physics with pixel conversion for rendering
- Gravity
- Friction
- Border collision
- Basic entity-to-entity collision
- Collision response using the normal vector between entities
- Basic overlap correction
- Previous-position tracking to reduce tunneling problems

## Next Steps

- Improve entity collision stability
- Improve the way velocities are updated after collision
- Handle high-speed tunneling cases
- Research or implement continuous collision detection
- Add better debugging visuals for collision normals and velocity vectors
- Keep improving the engine step by step without relying too much on external help

## Note on Commits

The first batch was committed as a whole due to issues from my side. I accidentally uninstalled Git, so the early progress could not be separated into smaller commits.
