package com.yto.ylp.maven.utils;

public class CustomEncoder {
	public static String escapeUnicode(String value) {
		StringBuilder sb = new StringBuilder();
		value.codePoints().forEach(codePoint -> {
			if (codePoint >= 0x0020 && codePoint <= 0x007e) {
				// ASCII 字符，直接追加
				sb.append((char) codePoint);
			} else if (codePoint <= 0xFFFF) {
				// 基本多语言平面（BMP）内的字符，转为  格式
				sb.append("\\u");
				sb.append(String.format("%04X", codePoint));
			} else {
				// 补充平面（Supplementary Plane）内的字符，需要使用代理对
				// 此时codePoint是一个int，需要将其拆分成两个char（高代理和低代理）
				char[] surrogates = Character.toChars(codePoint);
				sb.append("\\u");
				sb.append(String.format("%04X", (int) surrogates[0])); // 高代理
				sb.append("\\u");
				sb.append(String.format("%04X", (int) surrogates[1])); // 低代理
			}
		});
		return sb.toString();
	}
}
