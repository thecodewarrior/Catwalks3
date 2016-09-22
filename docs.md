Documentation and mod design document

# Blocks

### Common mechanics
All the blocks except for (LIST HERE) have four materials, Steel, IE Steel, Wooden, and Custom. The Custom variant isn't used normally and has no recipes.

* When a Speed Boost decoration is added to a block the player will get a speed increase, unless there is special handling of Speed Boost decorations, in which case the block should never give a speed boost
* When a Rope Light decoration is added to a block the block should emit a light level of 15
* If the hitbox for the side of a block appears to be flat, then you should be able to place a block on the outside from either side of the face
* The sides of most "complex" blocks can be toggled using a blowtorch and right-clicking
* The hitboxes will shrink when disabled, typically down or on all sides

## Catwalks
* Catwalks have collision boxes 1.5 blocks high, like fences, except when the player is sneaking
* you can place blocks from the inside of a catwalk by right-clicking on the side you want to place it on

## Catwalk Stairs
* Catwalk Stairs are placed sloping up and away from the player
* they have 1.5 block high sides and ends, though when walking down them you can jump over the sides
* the top and bottom of the sides can be toggled independantly

## Caged Ladders
* Caged Ladders are placed with the stair side away from the player
* they are normally the same speed as vanilla ladders, but are faster when a Speed Boost upgrade is added
* they don't give a horizontal speed boost when they have the Speed Boost upgrade
* the hitboxes of disabled sides will either contract on both sides, or toward the ladder side, depending on which is applicable
* there is a small hitbox at the top of the stair for extending a tower of them while climbing.
* ***Tiny details you probably won't care about***
 * the top and bottom hitboxes don't appear if there is a ladder above or below the block, respectively
 * they have connections to nearby blocks, based on some very complicated logic I won't go in to
 * if the player isn't activly trying to move, they won't climb (stops drifting and then hopping up for a fraction of a second when they hit the side)
 * if the player isn't pushing at more than a 10º angle to a side, they won't climb (stops accedental climbing when straifing inside the ladder)

## Scaffolds
* Scaffolds are one of the more utility focused blocks in Catwalks 3
* You can extend a line of Scaffolds up to 32 blocks by sneak+right-clicking on the side of a scaffold while holding a scaffold
* You can retract a line of Scaffolds up to 32 blocks by sneak+left-clicking on the side of a scaffold while holding a scaffold
* ***Wooden Scaffolds Only*** 
 * they act as ladders, at the same speed as vanilla ladders
 * they can be broken instantly with your fist
 * right clicking on the top of a block the player is standing on will place the block and move the player up, allowing the player to easily nerd-pole

# Items

## Decorations

### Caution Tape
Caution Tape is purely aesthetic and adds caution tape for metal-based materials, and something else (TBD) to wooden-based materials

### Rope Lights
Rope Lights make blocks glow at a strength of 15. Visually, they add rope lights to metal-based materials, and something else (TBD) to wooden-based materials

### Speed Boost
Speed Boost makes the player speed up in blocks it's applied to, most of the time just like a speed potion, but in some cases (like the Caged Ladder) it has other special functionality. It also adds a visual indicator to the blocks it's applied to.

## Ladder Grabber
Whenever the Ladder Grabber is enabled it will hold the player on ladders as if they were holding shift. Though it needs to be on the hotbar or offhand to do so. It can be toggled on and off by sneak+right-clicking it

## Blowtorch
The primary manipulation tool of Catwalks 3, it has no functionality on it's own, but most blocks will react to it.


# PLANNING AREA

## Blocks

### Support Column
* Does not accept decorations, but has all four materials
* When placed, it has an axis, and will have extensions along this axis
* When right clicked with a blowtorch it will either become axis-less or if it has no axis, it will align with the side clicked
* Meta consists of 2 bits for the 4 materials and 2 for the axis (X, Y, Z, None)

## Node Network

Connecting: Right click on source, right click on dest, optionally select which input

### Particle Emitter
* Emits particles in the direction specified by the node's facing direction
* Particle properties can be changed in the GUI
 * Speed + rand
 * Direction rand
 * Lifetime + rand
 * Color + brightness randomness
 * Texture (vanilla or some custom ones)
 * Acceleration + rand
 * Size + rand
 * Rotation speed + rand
* When connected to a redstone node it can be toggled using power

### Redstone nodes
* Redstone reception node
 * Turns 0-15 redstone signal into 0-n
* Redstone emmision node
 * Turns 0-n number into 0-15 redstone signal

### Number nodes
* Manipulation node
* Clock node
 * Can have a sequence it runs through or just two values