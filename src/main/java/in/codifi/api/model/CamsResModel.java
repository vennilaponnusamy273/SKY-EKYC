package in.codifi.api.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CamsResModel {
    private String txnId;
    private String fiuId;
    private String statusCode;
    private String message;
    private String sessionId;
    private String token;
    private String clientIP;
    private UserGroup userGroup;

    @Getter
    @Setter
    public static class UserGroup {
        private int id;
        private String name;
        private List<String> permissions;
    }
}
