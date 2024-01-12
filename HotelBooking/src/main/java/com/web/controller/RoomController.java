package com.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.web.exception.PhotoRetrievalException;
import com.web.model.BookedRoom;
import com.web.model.Room;
import com.web.response.BookingResponse;
import com.web.response.RoomResponse;
import com.web.service.BookedRoomService;
import com.web.service.RoomService;
import lombok.RequiredArgsConstructor;

@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
	
	private final RoomService roomService;
	private final BookedRoomService bookedRoomService;
	
	@PostMapping("/add/new-room")
	public ResponseEntity<RoomResponse> addNewRoom(
			@RequestParam("photo") MultipartFile photo,
			@RequestParam("roomType") String roomType,
			@RequestParam("roomPrice") BigDecimal roomPrice) throws SerialException, SQLException, IOException{
		Room saveRoom = roomService.addNewRoom(photo, roomType, roomPrice);
		RoomResponse response = new RoomResponse(saveRoom.getId(), saveRoom.getRoomType(),
				saveRoom.getRoomPrice());
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/room/types")
	public List<String> getRoomTypes(){
		return roomService.getAllRoomTypes();
	}
	
	@GetMapping("/all-rooms")
	public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException{
		List<Room> rooms = roomService.getAllRooms();
		List<RoomResponse> roomResponses = new ArrayList<>();
		for(Room room : rooms) {
			byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
			if(photoBytes != null && photoBytes.length > 0) {
				String base64Photo = Base64.encodeBase64String(photoBytes);
				RoomResponse roomResponse = getRoomResponse(room);
				roomResponse.setPhoto(base64Photo);
				roomResponses.add(roomResponse);
			}
		}
		return ResponseEntity.ok(roomResponses);
	}

	private RoomResponse getRoomResponse(Room room) {
		List<BookedRoom> bookings = getAllBookingByRoomId(room.getId());
//		List<BookingResponse> bookingInfo = bookings
//				.stream()
//				.map(booking -> new BookingResponse(booking.getBookingId(), booking.getCheckInDate(), booking.getCheckOutDate(), booking.getBookingConfirmationCode())).toList();
		byte[] photoBytes = null;
		Blob photoBlob = room.getPhoto();
		if(photoBlob != null) {
			try {
				photoBytes = photoBlob.getBytes(1,  (int)photoBlob.length());
			} catch (SQLException e) {
				throw new PhotoRetrievalException("Error retrieving photo");
			}
		}
		return new RoomResponse(room.getId(),
				room.getRoomType(), room.getRoomPrice(),
				room.isBooked(), photoBytes);
	}

	private List<BookedRoom> getAllBookingByRoomId(Long roomId) {
		
		return bookedRoomService.getAllBookingByRoomId(roomId);
	}
}
