package com.hdev.roomify.service.impl;

import com.hdev.roomify.dto.ResponseDTO;
import com.hdev.roomify.dto.RoomDTO;
import com.hdev.roomify.entity.Room;
import com.hdev.roomify.exception.GeneralException;
import com.hdev.roomify.repo.BookingRepository;
import com.hdev.roomify.repo.RoomRepository;
import com.hdev.roomify.service.AwsS3Service;
import com.hdev.roomify.service.interfac.IRoomService;
import com.hdev.roomify.utils.Utils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Service
@RequiredArgsConstructor
public class RoomService implements IRoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final AwsS3Service awsS3Service;

    @Override
    public ResponseDTO addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        ResponseDTO response = new ResponseDTO();

        try {
            String imageUrl = awsS3Service.saveImageToS3(photo);
            Room room = new Room();
            room.setRoomPhotoUrl(imageUrl);
            room.setRoomType(roomType);
            room.setRoomPrice(roomPrice);
            room.setRoomDescription(description);
            Room savedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
            response.setStatusCode(201);
            response.setMessage("Successfully added new room");
            response.setRoom(roomDTO);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public ResponseDTO getAllRooms() {
        ResponseDTO response = new ResponseDTO();

        try {
            List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successfully retrieved rooms");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO deleteRoom(Long roomId) {
        ResponseDTO response = new ResponseDTO();

        try {
            roomRepository.findById(roomId).orElseThrow(() -> new GeneralException("Room Not Found"));
            roomRepository.deleteById(roomId);
            response.setStatusCode(200);
            response.setMessage("successful");

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) {
        ResponseDTO response = new ResponseDTO();

        try {
            String imageUrl = null;
            if (photo != null && !photo.isEmpty()) {
                imageUrl = awsS3Service.saveImageToS3(photo);
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new GeneralException("Room Not Found"));
            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice);
            if (description != null) room.setRoomDescription(description);
            if (imageUrl != null) room.setRoomPhotoUrl(imageUrl);

            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);

            response.setStatusCode(200);
            response.setMessage("Successfully updated room");
            response.setRoom(roomDTO);

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO getRoomById(Long roomId) {
        ResponseDTO response = new ResponseDTO();

        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new GeneralException("Room Not Found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("Successfully retrieved room");
            response.setRoom(roomDTO);

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        ResponseDTO response = new ResponseDTO();

        try {
            List<Room> availableRooms = roomRepository.findAvailableRoomsByDatesAndTypes(checkInDate, checkOutDate, roomType);
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);
            response.setStatusCode(200);
            response.setMessage("Successfully retrieved available rooms");
            response.setRoomList(roomDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO getAllAvailableRooms() {
        ResponseDTO response = new ResponseDTO();

        try {
            List<Room> roomList = roomRepository.getAllAvailableRooms();
            List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);
            response.setStatusCode(200);
            response.setMessage("Successfully retrieved available rooms");
            response.setRoomList(roomDTOList);

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
    }
}