package eberware.api.web.controllers;

import eberware.api.core.systems.booking.models.Booking;
import eberware.api.core.systems.models.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("booking")
public class BookingController {

    @PostMapping
    public List<Booking.DTO> upsert(
            @RequestBody Booking.DTO booking,
            @AuthenticationPrincipal User user
    ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
