initial_state([
    at(room3),
    
    adjacent(room4, corridor), adjacent(corridor, room4),
    light_on(room4),
    
    adjacent(room3, corridor), adjacent(corridor, room3),
    light_off(room3),
    
    adjacent(room2, corridor), adjacent(corridor, room2),
    light_off(room2),
    
    adjacent(room1, corridor), adjacent(corridor, room1),
    light_on(room1),
    box(box1), box_at(box1, room1),
    box(box2), box_at(box2, room1),
    box(box3), box_at(box3, room1),
    box(box4), box_at(box4, room1),
    
    on(floor)
]).

goal_state([at(room1)]).
% goal_state([light_off(light1)]).
% goal_state([ box_at(box2, room2) ]).

% Move from location X to adjacent location Y (e.g., room-to-room or within-room)
act(
    go(X, Y),
    [at(X), adjacent(X, Y)],
    [at(X)],
    [at(Y)]
).

% Push box B from location X to Y (same room only, and light must be on at X)
act(
    push(B, X, Y),
    [at(X), box_at(B, X), box(B), adjacent(X, Y), light_on(X)],
    [box_at(B, X)],
    [box_at(B, Y)]
).

% Climb up onto box B (must be at same location as box and on floor)
act(
    climbUp(B),
    [box_at(B, X), at(X), on(floor)],
    [on(floor)],
    [on(box(B))]
).

% Climb down from box B (must be on the box and at the same location)
act(
    climbDown(B),
    [box_at(B, X), at(X), on(box(B))],
    [on(box(B))],
    [on(floor)]
).

% Turn on light S (must be on box under switch S, and the switch must be off)
act(
    turnOn(S),
    [switch_at(S, X), at(X), box_at(B, X), on(box(B)), light_off(S)],
    [light_off(S)],
    [light_on(S)]
).

% Turn off light S (must be on box under switch S, and the switch must be on)
act(
    turnOff(S),
    [switch_at(S, X), at(X), box_at(B, X), on(box(B)), light_on(S)],
    [light_on(S)],
    [light_off(S)]
).

