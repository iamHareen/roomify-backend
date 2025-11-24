package com.hdev.roomify.service.interfac;

import com.hdev.roomify.dto.LoginRequestDTO;
import com.hdev.roomify.dto.ResponseDTO;
import com.hdev.roomify.entity.User;

public interface IUserService {
    ResponseDTO register(User user);

    ResponseDTO login(LoginRequestDTO loginRequestDTO);

    ResponseDTO getAllUsers();

    ResponseDTO getUserBookingHistory(String userId);

    ResponseDTO deleteUser(String userId);

    ResponseDTO getUserById(String userId);

    ResponseDTO getMyInfo(String email);
}
