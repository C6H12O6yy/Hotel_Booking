package com.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.exception.InvalidBookingRequestException;
import com.web.exception.ResourceNotFoundException;
import com.web.model.BookedRoom;
import com.web.model.Room;
import com.web.response.BookingResponse;
import com.web.response.RoomResponse;
import com.web.service.BookedRoomService;
import com.web.service.RoomService;

import lombok.RequiredArgsConstructor;

@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookedRoomController {
	private final BookedRoomService bookedRoomService;
	private final RoomService roomService;
	
	@GetMapping("/all-bookings")
	public ResponseEntity<List<BookingResponse>> getAllBookings(){
		List<BookedRoom> bookings = bookedRoomService.getAllBooking();
		List<BookingResponse> bookingResponses = new ArrayList<>();
		for(BookedRoom booking : bookings) {
			BookingResponse bookingResponse = getBookingResponse(booking);
			bookingResponses.add(bookingResponse);
		}
		return ResponseEntity.ok(bookingResponses);
	}
	

	@GetMapping("/confirmation/{confirmationCode}")
	public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode){
		try {
			BookedRoom booking = bookedRoomService.findByBookingConfirmationCode(confirmationCode);
			BookingResponse bookingResponse = getBookingResponse(booking);
			return ResponseEntity.ok(bookingResponse);
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		
		}
	}
	
	@PostMapping("/room/{roomId}/booking")
	public ResponseEntity<?> saveBooking(@PathVariable Long roomId, 
			@RequestBody BookedRoom bookingRequest){
		try {
			String confirmationCode = bookedRoomService.saveBooking(roomId, bookingRequest);
			return ResponseEntity.ok("Room booked successfully, Your booking confirmation code is :" + confirmationCode);
		} catch (InvalidBookingRequestException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@DeleteMapping("/booking/{bookingId}/delete")
	public void cancelBooking(@PathVariable Long bookingId) {
		bookedRoomService.cancelBooking(bookingId);
	}
	

	private BookingResponse getBookingResponse(BookedRoom booking) {
		Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
		RoomResponse room = new RoomResponse(
				theRoom.getId(),
				theRoom.getRoomType(),
				theRoom.getRoomPrice());
	
		return new BookingResponse(booking.getBookingId(), booking.getCheckInDate(),
				booking.getCheckOutDate(),booking.getGuestFullName(),
				booking.getGuestEmail(), booking.getNumOfAdults(),
				booking.getNumOfChildren(), booking.getTotalNumOfGuest(),
				booking.getBookingConfirmationCode(), room);
	}
}
