package com.zipline.global.util;

import org.springframework.stereotype.Component;

import de.huxhorn.sulky.ulid.ULID;

@Component
public class ULIDGenerator {
	private static final ULID ulid = new ULID();

	public static String generate() {
		return ulid.nextULID();
	}

	private ULIDGenerator() {
	}
}
