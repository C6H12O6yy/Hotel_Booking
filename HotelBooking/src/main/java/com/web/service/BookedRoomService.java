package com.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.web.model.BookedRoom;

@Service
public interface BookedRoomService {

	List<BookedRoom> getAllBookingByRoomId(Long roomId);

}
