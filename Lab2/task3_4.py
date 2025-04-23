import random
import copy

classes = ['MT101', 'MT102', 'MT103', 'MT104', 'MT105', 'MT106', 'MT107',
 'MT201', 'MT202', 'MT203', 'MT204', 'MT205', 'MT206',
 'MT301', 'MT302', 'MT303', 'MT304',
 'MT401', 'MT402', 'MT403',
 'MT501', 'MT502']

classrooms = ['TP51', 'SP34', 'K3']

schedule = {
    9: {'TP51': '', 'SP34': '', 'K3': ''},
    10: {'TP51': '', 'SP34': '', 'K3': ''},
    11: {'TP51': '', 'SP34': '', 'K3': ''},
    12: {'TP51': '', 'SP34': '', 'K3': ''},
    13: {'TP51': '', 'SP34': '', 'K3': ''},
    14: {'TP51': '', 'SP34': '', 'K3': ''},
    15: {'TP51': '', 'SP34': '', 'K3': ''},
    16: {'TP51': '', 'SP34': '', 'K3': ''},
}

hours_to_avoid = [9, 12, 16]
preferred_hours_for_mt501_02 = [13, 14]

def main():
    assign_classes()
    schedule_min_conflict()
    print(f"Total conflicts: {total_conflicts()}")
    print_schedule(schedule)
    
    create_empty_schedule()
    assign_classes()
    optimal_schedule, unsatisfied_count = schedule_with_preferences()
    print(f"Optimal schedule with {unsatisfied_count} unsatisfied preferences and {total_conflicts()} total conflicts:")
    print_schedule(optimal_schedule)


def create_empty_schedule():
    return {hour: {room: '' for room in classrooms} for hour in range(9, 17)}

def assign_classes():
    # Always reset the remaining classes explicitly here
    remaining_classes = classes.copy()
    random.shuffle(remaining_classes)

    # Clear the schedule explicitly before assignment
    for hour in schedule:
        for room in schedule[hour]:
            schedule[hour][room] = ''

    for hour in schedule:
        for room in schedule[hour]:
            if remaining_classes:
                schedule[hour][room] = remaining_classes.pop()
            else:
                # No more classes to assign
                schedule[hour][room] = ''

def schedule_min_conflict(max_iterations=1000):
    for _ in range(max_iterations):
        if total_conflicts() == 0:
            break

        # Randomize to order to not always start at the top
        time_slots = list(schedule.keys())
        random.shuffle(time_slots)

        for time in time_slots:
            if conflicts_in_slot(schedule[time]) > 0:
                move_first_conflict_from_slot(schedule[time])

def move_first_conflict_from_slot(slot_in_time):
    conflicting_class = None
    original_room = None
    rooms = list(slot_in_time.keys())

    # Find a conflict
    for i in range(len(rooms)):
        for j in range(i + 1, len(rooms)):
            a, b = slot_in_time[rooms[i]], slot_in_time[rooms[j]]
            if a and b and a[2] == b[2] and a[2] != '5':
                conflicting_class = a
                original_room = rooms[i]
                break
        if conflicting_class:
            break

    if not conflicting_class:
        return

    best_time = None
    best_room = None
    min_conflicts = float('inf')

    all_times = list(schedule.keys())
    random.shuffle(all_times)

    for time in all_times:
        if schedule[time] is slot_in_time:
            continue

        random_rooms = classrooms[:]
        random.shuffle(random_rooms)

        for room in random_rooms:
            if schedule[time][room] == '':
                # Simulate move
                slot_in_time[original_room] = ''
                schedule[time][room] = conflicting_class
                conflicts = conflicts_in_slot(slot_in_time) + conflicts_in_slot(schedule[time])

                if conflicts < min_conflicts:
                    min_conflicts = conflicts
                    best_time = time
                    best_room = room
                # Undo
                schedule[time][room] = ''
                slot_in_time[original_room] = conflicting_class

    if best_time is not None:
        schedule[best_time][best_room] = conflicting_class
        slot_in_time[original_room] = ''

def total_conflicts():
    return sum(conflicts_in_slot(slot) for slot in schedule.values())

def conflicts_in_slot(slot):
    conflictsAmount = 0
    
    classTP51 = slot['TP51']
    classSP34 = slot['SP34']
    classK3 = slot['K3']

    # Check for conflicts directly
    if (classTP51 and classSP34) and (classTP51[2] == classSP34[2]) and (classTP51[2] != '5'):
        conflictsAmount += 1
    if (classTP51 and classK3) and (classTP51[2] == classK3[2]) and (classTP51[2] != '5'):
        conflictsAmount += 1
    if (classSP34 and classK3) and (classSP34[2] == classK3[2]) and (classSP34[2] != '5'):
        conflictsAmount += 1

    return conflictsAmount

def print_schedule(s):
    print(f"{'Time':^12}", end="")
    for room in classrooms:
        print(f"{room:^12}", end="")
    print("\n" + "-" * (12 * (len(classrooms) + 1)))

    for hour in sorted(s):
        print(f"{hour}:00".center(12), end="  ")
        for room in classrooms:
            class_name = s[hour][room] if s[hour][room] else "-"
            print(f"{class_name:^12}", end="  ")
        print()

def count_unsatisfied_pref(schedule):
    unsatisfied = 0
    for hour in hours_to_avoid:
        for room in schedule[hour]:
            if schedule[hour][room]:
                unsatisfied += 1
    
    for course in ['MT501', 'MT502']:
        scheduled_hour = next((hour for hour, rooms in schedule.items() if course in rooms.values()), None)
        if scheduled_hour not in preferred_hours_for_mt501_02:
            unsatisfied += 2

    return unsatisfied

def schedule_with_preferences(iterations=1000):
    best_schedule = None
    min_unsatisfied = float('inf')

    for _ in range(iterations):
        # Shuffle and reassign classes to explore new configurations
        assign_classes()

        # Use min-conflicts logic to refine the schedule
        for _ in range(100):  # Limit the number of adjustments per iteration
            if total_conflicts() == 0 and count_unsatisfied_pref(schedule) == 0:
                break

            # Randomly pick a time slot with conflicts or unsatisfied preferences
            time_slots = list(schedule.keys())
            random.shuffle(time_slots)

            for time in time_slots:
                if conflicts_in_slot(schedule[time]) > 0 or has_unsatisfied_preferences(schedule, time):
                    move_first_conflict_from_slot(schedule[time])
                    break

        # Evaluate the current schedule
        unsatisfied_preferences = count_unsatisfied_pref(schedule)

        # Update the best schedule if the current one is better
        if unsatisfied_preferences < min_unsatisfied:
            min_unsatisfied = unsatisfied_preferences
            best_schedule = copy.deepcopy(schedule)

    return best_schedule, min_unsatisfied
        
def has_unsatisfied_preferences(schedule, time):
    for room in schedule[time]:
        if time in hours_to_avoid and schedule[time][room]:
            return True
        if schedule[time][room] in ['MT501', 'MT502'] and time not in preferred_hours_for_mt501_02:
            return True
    return False


if __name__ == "__main__":
    main()
