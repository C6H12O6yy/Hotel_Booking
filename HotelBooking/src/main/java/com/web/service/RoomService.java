package com.web.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.sql.rowset.serial.SerialException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.web.model.Room;

@Service
public interface RoomService {

	Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) throws SerialException, SQLException, IOException;

	List<String> getAllRoomTypes();
	
}
