package com.web.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.web.exception.InvalidBookingRequestException;
import com.web.model.BookedRoom;
import com.web.model.Room;
import com.web.repository.BookedRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BookedRoomServiceImpl implements BookedRoomService {
	private final BookedRoomRepository bookedRoomRepository;
	private final RoomService roomService;

	@Override
	public List<BookedRoom> getAllBooking() {
		return bookedRoomRepository.findAll();
	}

	@Override
	public List<BookedRoom> getAllBookingByRoomId(Long roomId) {
		return bookedRoomRepository.findByRoomId(roomId);
	}

	@Override
	public void cancelBooking(Long bookingId) {
		bookedRoomRepository.deleteById(bookingId);
	}

	@Override
	public String saveBooking(Long roomId, BookedRoom bookingRequest) {
		if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
			throw new InvalidBookingRequestException("Check-in date must come before chech-out date");
		}
		Room room = roomService.getRoomById(roomId).get();
		List<BookedRoom> existingBookings = room.getBookings();
		boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
		if(roomIsAvailable) {
			room.addBooking(bookingRequest);
			bookedRoomRepository.save(bookingRequest);
			
		}else {
			throw new InvalidBookingRequestException("Sorry, This room is not availabel for the seleted dates.");
		}
		return bookingRequest.getBookingConfirmationCode();
	}

	private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
		return existingBookings.stream()
				.noneMatch(existingBooking -> bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
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
								&& bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate())));
	}

	@Override
	public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
		return bookedRoomRepository.findByBookingConfirmationCode(confirmationCode);
	}

}
