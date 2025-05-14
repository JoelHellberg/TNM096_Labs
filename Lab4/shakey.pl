% actions
act( go(X,Y),                         % action name
     [is_adjacent(X,Y), is_on_floor], % preconditions
     [is_on_pos(X)],                  % delete
     [is_on_pos(Y)]                   % add
     ).

act( push(B, X, Y),
     [is_adjacent(X,Y), is_on_floor, light_is_on(S)],
     [is_on_pos(X)],
     [is_on_pos(Y)]
     ).

act( climb_up(B),
     [next_to_box(X,B), is_on_floor],
     [is_on_floor],
     [is_on_box(B)],
     ).

act( climb_down(B),
     [next_to_box(X,B), is_on_box(B)],
     [is_on_box(B)],
     [is_on_floor],
     ).

act( turn_on(S),
     [is_on_box(B), box_under_light(B,S), light_is_off],
     [light_is_off],
     [light_is_on],
     ).

act( turn_off(S),
     [is_on_box(B), box_under_light(B,S), light_is_on],
     [light_is_on],
     [light_is_off],
     ).
