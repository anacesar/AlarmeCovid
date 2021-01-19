package Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class myMap {

    final int N = 5; //map matrix NxN
    private Location[][] map = new Location[N][N];

    public myMap() {
        int i, j;
        for(i=0; i<N; i++)
            for(j=0; j<N; j++)
                map[i][j] = new Location();
    }

    class Location {
        private List<String> currentUsers; //list of current users in this location
        private List<String> history; //list of users visited this location

        private Lock location_lock = new ReentrantLock();
        private Condition isEmpty = location_lock.newCondition();

        public void entry(String username){
            currentUsers.add(username);
            if(! history.contains(username)) history.add(username);
        }

        public void exit(String username){
            currentUsers.remove(username);
            if(currentUsers.isEmpty()) isEmpty.signalAll();
        }

        public List<String> getCurrentUsers(){
            location_lock.lock();
            try{
                return currentUsers;
            }finally {
                location_lock.unlock();
            }
        }

        public void waitEmpty(String username) throws InterruptedException {
            location_lock.lock();
            try{
                /* waits for place to be empty*/
                while(! currentUsers.isEmpty()) isEmpty.wait();
                //notification place is empty
            }finally {
                location_lock.unlock();
            }
        }

        public void lock(){ this.location_lock.lock();}

        public void unlock(){ this.location_lock.unlock();}
    }

    public Location getLocation(int node){
        return map[node/N][node%N];
    }

    /* not needed */
    public int getNode(Location location) {
        int node = -1;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                node++;
                if(map[i][j].equals(location)) return node;
            }
        }
        return node;
    }

}
