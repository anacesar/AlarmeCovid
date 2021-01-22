package Server;

public class mapWorker implements Runnable {
    private AlarmeCovid alarmeCovid;

    public mapWorker(AlarmeCovid alarmeCovid){
        this.alarmeCovid = alarmeCovid;
    }


    @Override
    public void run() {
        //bucar o N
        int N = alarmeCovid.N;

        /* array of threads to deal with each location in map */
        Thread[] loc = new Thread[N*N-1];

        //para cada loc criar thread esperar vazio
        try{
            for(int i=0; i<N*N-1; i++){
                int finalI = i +1;
                loc[i] = new Thread( () -> alarmeCovid.empty_map(finalI));
                loc[i].start();
            }

            for (Thread t: loc) t.join();

        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
