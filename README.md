# Militia

A simple turn based boardgame made with JOGL.

Controls

    wasd : move cursor
    f : show movable tiles
    q : show attackable tiles
    e : use special skill
    i : toggle information(hp, mp, attack chance, move chance) display
    enter : do some action(on movable tile : move, in attackable tile : attack)
    spacebar : finish current turn
    
Unit skills

    Knight(shield shape) : become invincible for 1 turn
    Warrior(axe shape) : attacks all surrounding tiles
    Wizard(staff shape) : teleports and freezes surrounding tiles for 1 turn
    
Note

    Move -> attack (O)
    Attack -> move (X)
    Any attack made by wizard will deplete its mp
    Any attack can damage anyone(including your units)
    
