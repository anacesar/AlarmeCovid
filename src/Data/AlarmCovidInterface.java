package Data;

import exceptions.*;

public interface AlarmCovidInterface {

    void registration(String username, String password, String special_password) throws AlreadyRegistedException, SpecialPasswordInvalidException;
    void authentication(String username, String password) throws InvalidLoginException;
    void notify_positive(String username);
    int nr_people_location(int node) throws InvalidLocationException;
    void notify_empty_location(String usename, int node) throws InvalidLocationException;
    void update_location(String username, int new_location);

    //special client
    void download_map();
}
