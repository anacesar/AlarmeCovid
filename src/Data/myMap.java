package Data;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class myMap {

    final int N = 5; //map matrix NxN
    private Location[][] map;


    class Location {
        private List<String> currentUsers; //list of current users in this location
        private List<String> history; //list of users visited this location

        private Lock lock = new ReentrantLock();
        private Condition isEmpty = lock.newCondition();


    }

    public Location getLocation(int node){
        return map[node/N][node%N];
    }


}
