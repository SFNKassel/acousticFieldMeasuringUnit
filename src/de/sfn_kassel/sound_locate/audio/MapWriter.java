package de.sfn_kassel.sound_locate.audio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class MapWriter {
	final Map<?, ?> map;
	
	public MapWriter(Map<?, ?> map) {
		this.map = map;
	}
	
	public void writeToCsv(File f, String columnSeperator, String lineSeperator) throws IOException {
		StringBuilder sb = new StringBuilder();
		map.forEach((v, k) -> sb.append(v + columnSeperator + k + lineSeperator));
		
		BufferedWriter out = new BufferedWriter(new FileWriter(f));
		out.write(sb.toString());
		out.close();
	}
}
