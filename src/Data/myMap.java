package Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class myMap {
    private int N; //map matrix NxN
    private Location[][] map;

    public myMap(int n) {
        N = n;
        map = new Location[N][N];
        map[0][0] = new Location(); //users home -> node 0
    }

    public static class Location {
        private String address;
        private List<String> currentUsers; //list of current users in this location
        private List<String> history; //list of users visited this location

        private Lock location_lock = new ReentrantLock();
        private Condition isEmpty = location_lock.newCondition();

        public Location(){
            address = "";
        }

        public Location(String address, List<String> currentUsers, List<String> history){
            this.address = address;
            this.currentUsers = new ArrayList<>(currentUsers);
            this.history = new ArrayList<>(history);
        }

        public void entry(String username){
            currentUsers.add(username);
            if(! history.contains(username)) history.add(username);
        }

        public void exit(String username){
            System.out.println(username + " exited " + address );
            currentUsers.remove(username);
            if(currentUsers.isEmpty()){
                System.out.println("place empty, signal all....");
                isEmpty.signalAll();
            }
        }

        public List<String> getCurrentUsers(){
            location_lock.lock();
            try{
                return new ArrayList<>(currentUsers);
            }finally {
                location_lock.unlock();
            }
        }

        public List<String> getHistory(){
            location_lock.lock();
            try{
                return history;
            }finally {
                location_lock.unlock();
            }
        }

        public String getAdress(){ return this.address; }

        public void lock(){ this.location_lock.lock();}

        public void unlock(){ this.location_lock.unlock();}

        public void await() throws InterruptedException { this.isEmpty.await();}

        @Override
        public String toString() {
            return "Location{" +
                    "address='" + address + '\'' +
                    ", currentUsers=" + currentUsers +
                    ", history=" + history +
                    '}';
        }

    }

    public Location getLocation(int node){
        return map[node/N][node%N];
    }

    public void putLocation(int node, Location location){
        this.map[node/N][node%N] = location;
    }

    public int nr_people(int node){
        return getLocation(node).currentUsers.size();
    }

}
