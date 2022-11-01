package com.charlenes.helpers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class DateTimeHelper {
	
	public static OffsetDateTime getOffsetDateTime() {
		return LocalDateTime.now().atOffset(OffsetDateTime.now().getOffset());
	}

}
