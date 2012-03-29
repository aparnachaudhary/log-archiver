package net.arunoday.logview.util;

import java.util.Map;

public class MapCompare {

	public static <K, V> boolean areMapsEquals(Map<K, V> m1, Map<K, V> m2) {
		if (null == m1 ^ m2 == null) {
			return false;
		}
		if (m1 == null && m2 == null) {
			return true;
		}
		if (m1.size() != m2.size()) {
			return false;
		}

		for (K key : m1.keySet()) {
			if (!m2.containsKey(key)) {
				return false;
			}
			V v1 = m1.get(key);
			V v2 = m2.get(key);
			if (null == v1 ^ v2 == null) {
				return false;
			}
			if (v1 != null && !v1.equals(v2)) {
				return false;
			}
		}
		return true;
	}

}
