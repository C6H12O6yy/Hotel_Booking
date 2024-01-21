package com.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web.model.BookedRoom;

public interface BookedRoomRepository extends JpaRepository<BookedRoom, Long>{
	BookedRoom findByBookingConfirmationCode(String confirmationCode);
	
	List<BookedRoom> findByRoomId(Long roomId);
}
