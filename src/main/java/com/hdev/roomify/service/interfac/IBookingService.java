package com.hdev.roomify.service.interfac;

import com.hdev.roomify.dto.ResponseDTO;
import com.hdev.roomify.entity.Booking;

public interface IBookingService {
    ResponseDTO saveBooking(Long roomId, Long userId, Booking bookingRequest);

    ResponseDTO findBookingByConfirmationCode(String confirmationCode);

    ResponseDTO getAllBookings();

    ResponseDTO cancelBooking(Long bookingId);
}
