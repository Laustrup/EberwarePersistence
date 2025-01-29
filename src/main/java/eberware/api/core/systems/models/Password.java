package eberware.api.core.systems.models;

import eberware.api.core.systems.libaries.SecurityLibrary;
import lombok.Getter;

@Getter
public class Password {

    private String _content;

    private String _stash;

    private String _combined;

    public Password(String content, String stash) {
        _content = content;
        _stash = stash;
        _combined = _content + SecurityLibrary.passwordEncodingCharacter + _stash;
    }

    public static Password convert(String gibberish) {
        String[] encodings = gibberish.split(String.valueOf(SecurityLibrary.passwordEncodingCharacter));
        return new Password(encodings[0], encodings[1]);
    }

    public static String convert(Password password) {
        return password.get_content() + SecurityLibrary.passwordEncodingCharacter + password.get_stash();
    }
}
