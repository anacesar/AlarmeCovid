package Data;

import exceptions.*;

public interface AlarmCovidInterface {

    boolean registration(String username, String password, String special_password) throws AlreadyRegistedException, SpecialPasswordInvalidException;
    boolean authentication(String username, String password) throws InvalidLoginException, QuarantineException;
    void notify_positive(String username);
    int nr_people_location(int node) throws InvalidLocationException;
    void notify_empty_location(String username, int node) throws InvalidLocationException;
    void update_location(String username, int new_location) throws InvalidLocationException;
    void view_map();

    //special client
    void download_map(String username);
}
