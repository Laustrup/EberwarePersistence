package eberware.api.core.systems.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class Password {

    private final String _encryption;

    private final int _picking;
}
