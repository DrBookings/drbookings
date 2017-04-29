package com.github.drbookings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class Test {

    public static void main(final String[] args) throws IOException {

	final Map<String, String> map = new LinkedHashMap<>();
	map.put("k1", "v1");
	map.put("k2", "v2");

	final Properties p = new Properties();
	p.put("id", map);
	p.store(new FileOutputStream(new File("test")), "hey");

    }

}
