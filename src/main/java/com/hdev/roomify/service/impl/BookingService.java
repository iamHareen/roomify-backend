package com.hdev.roomify.service.impl;

import com.hdev.roomify.dto.BookingDTO;
import com.hdev.roomify.dto.ResponseDTO;
import com.hdev.roomify.entity.Booking;
import com.hdev.roomify.entity.Room;
import com.hdev.roomify.entity.User;
import com.hdev.roomify.exception.GeneralException;
import com.hdev.roomify.repo.BookingRepository;
import com.hdev.roomify.repo.RoomRepository;
import com.hdev.roomify.repo.UserRepository;
import com.hdev.roomify.service.interfac.IBookingService;
import com.hdev.roomify.service.interfac.IRoomService;
import com.hdev.roomify.utils.Utils;
import lombok.Data;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
public class BookingService implements IBookingService {

    private BookingRepository bookingRepository;
    private IRoomService roomService;
    private RoomRepository roomRepository;
    private UserRepository userRepository;

    @Override
    public ResponseDTO saveBooking(Long roomId, Long userId, Booking bookingRequest) {

        ResponseDTO response = new ResponseDTO();

        try {
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new IllegalArgumentException("Check in date must come after check out date");
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new GeneralException("Room Not Found"));
            User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException("User Not Found"));

            List<Booking> existingBookings = room.getBookings();

            if (!roomIsAvailable(bookingRequest, existingBookings)) {
                throw new GeneralException("Room not Available for selected date range");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            bookingRepository.save(bookingRequest);
            response.setStatusCode(201);
            response.setMessage("Successfully saved room");
            response.setBookingConfirmationCode(bookingConfirmationCode);

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Saving a booking: " + e.getMessage());

        }
        return response;
    }


    @Override
    public ResponseDTO findBookingByConfirmationCode(String confirmationCode) {

        ResponseDTO response = new ResponseDTO();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new GeneralException("Booking Not Found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBooking(bookingDTO);

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Finding a booking: " + e.getMessage());

        }
        return response;
    }


    @Override
    public ResponseDTO getAllBookings() {

        ResponseDTO response = new ResponseDTO();

        try {
            List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingList(bookingDTOList);

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Getting all bookings: " + e.getMessage());

        }
        return response;
    }


    @Override
    public ResponseDTO cancelBooking(Long bookingId) {

        ResponseDTO response = new ResponseDTO();

        try {
            bookingRepository.findById(bookingId).orElseThrow(() -> new GeneralException("Booking Does Not Exist"));
            bookingRepository.deleteById(bookingId);
            response.setStatusCode(200);
            response.setMessage("Successfully cancelled booking");

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Cancelling a booking: " + e.getMessage());

        }
        return response;
    }


    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {

        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }
}
