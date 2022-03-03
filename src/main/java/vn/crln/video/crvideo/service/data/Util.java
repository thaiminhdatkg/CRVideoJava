package vn.crln.video.crvideo.service.data;

import vn.crln.video.crvideo.model.Bound;
import vn.crln.video.crvideo.model.Size;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Util {
    public static class Data {
        public static Integer getInt(Map map, String key) {
            return (Integer)map.getOrDefault(key, null);
        }
        /*
        public static Long getLong(Map map, String key) {
            return (Long) map.getOrDefault(key, null);
        }
         */

        public static String getString(Map map, String key) {
            return (String) map.getOrDefault(key, null);
        }

        public static Size getSize(Map map, String keyWidth, String keyHeight) {
            Size size = new Size(getInt(map, keyWidth), getInt(map, keyHeight));
            if (size.getWidth() == null || size.getHeight() == null) return null;
            return size;
        }

        public static Bound getBound(Map map, String keyX1, String keyY1, String keyX2, String keyY2) {
            Bound bound = new Bound(getInt(map, keyX1), getInt(map, keyY1), getInt(map, keyX2), getInt(map, keyY2));
            if (bound.getX1() == null || bound.getX2() == null || bound.getY1() == null || bound.getY2() == null) return null;
            return bound;
        }

        public static Date getDate(Map map, String key) {
            String str = getString(map, key);
            if (str == null) return null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            try {
                return sdf.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static Boolean getBool(Map map, String key) {
            Integer i = getInt(map, key);
            if (i == null) return null;
            return i.equals(1);
        }
    }

    public static class Check {
        public static class Str {
            public static boolean isWhiteStringOrEmpty(String str) {
                return str == null || str.trim().isEmpty();
            }
        }
    }
}
