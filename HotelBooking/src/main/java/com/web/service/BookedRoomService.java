package com.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.web.model.BookedRoom;

@Service
public interface BookedRoomService {

	List<BookedRoom> getAllBookingByRoomId(Long roomId);

	void cancelBooking(Long bookingId);

	String saveBooking(Long roomId, BookedRoom bookingRequest);

	BookedRoom findByBookingConfirmationCode(String confirmationCode);

	List<BookedRoom> getAllBooking();

}
