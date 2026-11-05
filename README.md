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
- Diameter (independent of mass)
- Mass in kilograms
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

Positions and collision geometry currently use pixels internally. Constructor diameters and velocities are supplied in meters and meters per second and converted using `Main.unit` (pixels per meter). Gravity is scaled to pixels per second squared. Mass is stored separately in kilograms and is never used as a drawing size.

The three- and five-argument constructors preserve the original demo's size-to-mass default. The six-argument constructor accepts independent diameter and mass: `Entity(xPixels, yPixels, diameterMeters, massKg, vxMetersPerSecond, vyMetersPerSecond)`.

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


## Collision Jittering and Overlap Correction

While testing entity collision, I found a problem similar to the jittering issue I previously had with ground collision.

For ground collision, jittering happens when the entity keeps slightly passing through the ground and then being corrected again every frame. The fix was to use a stricter collision check, and when the condition is met, place the entity exactly on the surface.

With entity collision, we make use of the fact that we have the normal vector, meaning we have the line on which the entities are allowed to move when correcting their positions.

To fix overlap, I move the two entities in opposite directions along the contact normal, weighted by inverse mass. This preserves the center of mass. Position correction also runs when overlapping bodies are already separating; a velocity impulse is only applied when they are approaching.

This separates the two circles so they are no longer inside each other.

## Collision Detection Problems

Another issue I noticed is that on higher speeds, it isn't really guaranteed that: frame 1, no collision, center1 is at left, center2 is at right. frame 2, collision, center1 still at left, center2 still at right.

Since collision is checked once per frame, it is possible for a fast-moving entity to pass too far into another entity before the collision is detected. In extreme cases, the entities could completely pass through each other between two frames, causing the collision to not be detected at all.

Previous positions are stored, but the current collision checks do not use them for swept collision detection. Fast bodies can still tunnel through each other between updates.

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
- Previous-position tracking (continuous collision detection is not implemented)

## Next Steps

- Improve entity collision stability
- Improve the way velocities are updated after collision
- Handle high-speed tunneling cases
- Research or implement continuous collision detection
- Add better debugging visuals for collision normals and velocity vectors
- Keep improving the engine step by step without relying too much on external help

## Note on Commits

The first batch was committed as a whole due to issues from my side. I accidentally uninstalled Git, so the early progress could not be separated into smaller commits.

## Correctness checks

The position update applies constant-acceleration displacement before updating velocity, so acceleration is not counted twice. Ground friction is recalculated rather than accumulated, and clamps small velocities to avoid reversing a body. Wall corrections refresh collision centers and only reverse velocity pointing into a wall. A small impact threshold lets circles settle on the floor.

Run with JDK 17 or later:

```sh
javac -d build/app src/Main.java
java -cp build/app Main
```

Run the headless regression checks:

```sh
javac -d build/checks src/Main.java tests/PhysicsChecks.java
java -cp build/checks PhysicsChecks
```

The checks cover free fall, independent mass and size, constructor consistency, unequal-mass elastic collisions, momentum and energy conservation, penetration correction (including coincident centers and separating bodies), friction, and floor/wall contact.

## Collision detection roadmap

I have read about more advanced collision-detection algorithms and plan to further enhance my implementation. For now, it still uses discrete, pairwise circle-overlap checks. The existing call pattern also repeats the pair scan for each entity; this has deliberately been left unchanged in this correctness update. Broad-phase acceleration and continuous collision detection are future improvements, not current features.

The timer still uses an assumed fixed interval rather than an elapsed-time accumulator. Dense piles, wall/contact interactions, and high-speed impacts need further work; the floor resting check does not establish a general stacking solver.
