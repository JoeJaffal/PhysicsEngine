# PhysicsEngine

This is my first real challenge/project where I am trying to reduce my dependence on ChatGPT and really test my own coding skills.

I have always had a passion for game development, so I decided that the best project for me right now is to try building a simple physics engine from scratch.

## Why Java?

I chose Java mainly because it felt like the simplest option for opening windows and drawing graphics compared to other languages I considered, like C++. From what I found, C++ makes the window setup process more complicated.

Another reason I chose Java is because of its object-oriented nature, which fits well with the way I want to structure the project, and the way I think real physics engines are made.

## What I Learned First

I started by learning how to open a window using `JFrame`.

After that, I placed a custom panel inside the window by extending `JPanel`. This allowed me to draw shapes on the screen. I also overrode the painting behavior so the panel could fit the needs of my project.

Everything up to this point was learned through Library documentation, and basic research.

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

## How the Simulation Works

In the `main` file, I define final variables such as:

- Gravity
- Window borders
- Delay between frames, which is related to the time used in the formulas
- Other simulation constants

The program starts by initializing the window and screen.

Then, I use Java’s `Timer` library to simulate frames. The timer works like a simple game loop. On every tick:

1. The physics of each entity is updated.
2. The screen is repainted using `repaint()`.
3. The new positions are drawn on the panel.

## Current Status

Right now (21/5/2026), the engine can create circular entities, update their motion, apply simple physics, handle border collisions, and repaint their new positions frame by frame.

This is still an early version, but the main goal is learning, experimenting, and building the system step by step from scratch.


## Note on commits
The first batch was committed as whole due to issues from my side (I accidentally uninstalled git)
