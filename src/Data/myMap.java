package Data;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class myMap {

    final int N = 5; //map matrix NxN
    private Location[][] map;

    private Lock lock = new ReentrantLock();

    class Location {
        private List<String> currentUsers; //list of current users in this location
        private List<String> history; //list of users visited this location

        private Lock lock = new ReentrantLock();
        private Condition isEmpty = lock.newCondition();

        public void entry(String username){
            lock.lock();
            try {
                currentUsers.add(username);
                if(! history.contains(username)) history.add(username);
            }finally {
                lock.unlock();
            }
        }

        public void exit(String username){
            lock.lock();
            try{
                currentUsers.remove(username);
                if(currentUsers.isEmpty()) isEmpty.signalAll();
            }finally {
                lock.unlock();
            }
        }

        public void waitEmpty(String username) throws InterruptedException {
            lock.lock();
            try{
                /* waits for place to be empty*/
                while(! currentUsers.isEmpty()) isEmpty.wait();
                //notification place is empty
            }finally {
                lock.unlock();
            }
        }
    }

    public Location getLocation(int node){
        lock.lock();
        try{
            return map[node/N][node%N];
        }finally {
            lock.unlock();
        }
    }

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
