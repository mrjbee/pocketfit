package team.monroe.org.pocketfit.manage;

import android.content.Context;

import org.monroe.team.android.box.utils.SerializationMap;

import java.util.HashSet;
import java.util.Set;

import team.monroe.org.pocketfit.presentations.Routine;

public class RoutineManager {

    private final SerializationMap<String, Routine> routineSerializationMap;

    public RoutineManager(Context context) {
        this.routineSerializationMap = new SerializationMap<>("routine.map", context);
    }

    public void updateOrCreate(Routine routine) {
        routineSerializationMap.put(routine.id, routine);
    }

    public Routine get(String routineId) {
        return routineSerializationMap.get(routineId);
    }

    public Set<String> listIds() {
        return new HashSet<>(routineSerializationMap.keys());
    }

    public void remove(String routineId) {
        routineSerializationMap.remove(routineId);
    }
}
