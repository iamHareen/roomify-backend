package com.hdev.roomify.service.impl;

import com.hdev.roomify.dto.LoginRequestDTO;
import com.hdev.roomify.dto.ResponseDTO;
import com.hdev.roomify.dto.UserDTO;
import com.hdev.roomify.entity.User;
import com.hdev.roomify.exception.GeneralException;
import com.hdev.roomify.repo.UserRepository;
import com.hdev.roomify.service.interfac.IUserService;
import com.hdev.roomify.utils.JWTUtils;
import com.hdev.roomify.utils.Utils;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
public class UserService implements IUserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JWTUtils jwtUtils;
    private AuthenticationManager authenticationManager;


    @Override
    public ResponseDTO register(User user) {
        ResponseDTO response = new ResponseDTO();
        try {
            if (user.getRole() == null || user.getRole().isBlank()) {
                user.setRole("USER");
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new GeneralException(user.getEmail() + "Already Exists");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);
            response.setStatusCode(201);
            response.setMessage("User Registered Successfully");
            response.setUser(userDTO);
        } catch (GeneralException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Occurred During User Registration " + e.getMessage());

        }
        return response;
    }

    @Override
    public ResponseDTO login(LoginRequestDTO loginRequest) {

        ResponseDTO response = new ResponseDTO();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new GeneralException("User Not found"));

            var token = jwtUtils.generateToken(user);
            response.setStatusCode(200);
            response.setToken(token);
            response.setRole(user.getRole());
            response.setExpirationTime("7 Days");
            response.setMessage("User Login Successfully");

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Occurred During User Login " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO getAllUsers() {

        ResponseDTO response = new ResponseDTO();
        try {
            List<User> userList = userRepository.findAll();
            List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);
            response.setStatusCode(200);
            response.setMessage("All Users Successfully Fetched");
            response.setUserList(userDTOList);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO getUserBookingHistory(String userId) {

        ResponseDTO response = new ResponseDTO();

        try {
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new GeneralException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusUserBookingsAndRoom(user);
            response.setStatusCode(200);
            response.setMessage("User Booking History Successfully Fetched");
            response.setUser(userDTO);

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO deleteUser(String userId) {

        ResponseDTO response = new ResponseDTO();

        try {
            userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new GeneralException("User Not Found"));
            userRepository.deleteById(Long.valueOf(userId));
            response.setStatusCode(200);
            response.setMessage("User Deleted Successfully");

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO getUserById(String userId) {

        ResponseDTO response = new ResponseDTO();

        try {
            User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new GeneralException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("User Fetched Successfully");
            response.setUser(userDTO);

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {

            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

    @Override
    public ResponseDTO getMyInfo(String email) {

        ResponseDTO response = new ResponseDTO();

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new GeneralException("User Not Found"));
            UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
            response.setStatusCode(200);
            response.setMessage("User Information Fetched Successfully");
            response.setUser(userDTO);

        } catch (GeneralException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all users " + e.getMessage());
        }
        return response;
    }

}
