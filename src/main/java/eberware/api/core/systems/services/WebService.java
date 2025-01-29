package eberware.api.core.systems.services;

import eberware.api.core.systems.models.Response;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

public class WebService {

    public static <DTO> ResponseEntity<DTO> respond(
            Supplier<Response<DTO>> manager
    ) {
        Response<DTO> response = manager.get();

        return new ResponseEntity<>(
                response.get_object(),
                response.get_httpStatus()
        );
    }
}
