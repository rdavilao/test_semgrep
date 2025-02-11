package au.com.projects.tender.exception;

public class LoginException extends Exception {

    private final String nickname;

    public LoginException(String nickname, String message) {
        super(message);
        this.nickname = nickname;
    }

    public LoginException(String nickname, String message, Throwable cause) {
        super(message, cause);
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
