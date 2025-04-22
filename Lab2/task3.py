import random

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

def main():
    assign_classes()
    schedule_min_conflict()
    print_schedule()

def assign_classes():
    remaining_classes = classes.copy()
    random.shuffle(remaining_classes)

    for hour in schedule:
        for room in schedule[hour]:
            if remaining_classes:
                schedule[hour][room] = remaining_classes.pop()

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

def print_schedule():
    print(f"{'Time':^12}", end="")
    for room in classrooms:
        print(f"{room:^12}", end="")
    print("\n" + "-" * (12 * (len(classrooms) + 1)))

    for hour in sorted(schedule):
        print(f"{hour}:00".center(12), end="  ")
        for room in classrooms:
            class_name = schedule[hour][room] if schedule[hour][room] else "-"
            print(f"{class_name:^12}", end="  ")
        print()

if __name__ == "__main__":
    main()
