package es.unex.spilab.contigo.service;

public interface SecurityService {
    String findLoggedInUsername();

    void autologin(String username, String password);
}
