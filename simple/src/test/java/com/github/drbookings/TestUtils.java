package com.github.drbookings;

import com.github.drbookings.model.data.Guest;
import com.github.drbookings.model.data.Room;

public class TestUtils {

    public static Guest getTestGuest() {
	return new Guest("testGuest");
    }

    public static Room getTestRoom() {
	return new Room("testRoom");
    }

}
