initial_state([
    at(room3),

    adjacent(room1, corridor), adjacent(corridor, room1),
    switch_at(light1, room1),
    light_on(light1),
    box(box1), box_at(box1, room1), box_found(box1, false),
    box(box2), box_at(box2, room1), box_found(box2, false),
    box(box3), box_at(box3, room1), box_found(box3, false),
    box(box4), box_at(box4, room1), box_found(box4, false),

    adjacent(room2, corridor), adjacent(corridor, room2),
    switch_at(light2, room2),
    light_on(light2),

    adjacent(room3, corridor), adjacent(corridor, room3),
    switch_at(light3, room3),
    light_on(light3),

    adjacent(room4, corridor), adjacent(corridor, room4),
    switch_at(light4, room4),
    light_on(light4),

    on(floor)
]).

% goal_state([at(room1)]).
% goal_state([light_off(light1)]).
goal_state([box_at(box2, room2)]).

% Move from location X to adjacent location Y
act(
    go(X, Y),
    [at(X), adjacent(X, Y)],
    [at(X)],
    [at(Y)]
).

% Push box B from location X to Y (requires light to be on)
act(
    findBox(B, X),
    [at(X), box_at(B, X), box(B), switch_at(S, X), light_on(S), box_found(B, false)],
    [box_found(B, false)],
    [box_found(B, true)]
).

% Push box B from location X to Y (requires light to be on)
act(
    push(B, X, Y),
    [at(X), box_at(B, X), box(B), adjacent(X, Y), box_found(B, true)],
    [at(X), box_at(B, X)],
    [at(Y), box_at(B, Y)]
).

% Climb up onto box B
act(
    climbUp(B),
    [box_at(B, X), at(X), on(floor)],
    [on(floor)],
    [on(box(B))]
).

% Climb down from box B
act(
    climbDown(B),
    [box_at(B, X), at(X), on(box(B))],
    [on(box(B))],
    [on(floor)]
).

% Turn on light S
act(
    turnOn(S),
    [switch_at(S, X), at(X), box_at(B, X), on(box(B)), light_off(S)],
    [light_off(S)],
    [light_on(S)]
).

% Turn off light S
act(
    turnOff(S),
    [switch_at(S, X), at(X), box_at(B, X), on(box(B)), light_on(S)],
    [light_on(S)],
    [light_off(S)]
).
